package shopping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

// system tests - running the WHOLE app start to finish like a real user would,
// typing in a full line of input and checking what gets printed out
class SystemTests {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputCapture;

    private String runApp(String simulatedTyping) {
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));

        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedTyping.getBytes()));
        ShoppingApp app = new ShoppingApp(scanner);
        app.run();

        return outputCapture.toString();
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    // full happy path: enter info, add an item, get the total, checkout, exit
    void fullRun_addItemGetTotalCheckout() {
        String typedInput = String.join("\n",
                "Ibrahim",       // name
                "IL",            // state
                "STANDARD",      // shipping option
                "1",             // menu: add item
                "Shirt",         // item name
                "20.00",         // unit price
                "2",             // quantity
                "2",             // menu: get total
                "6",             // menu: checkout
                "7"              // menu: exit
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Added. The cart now has 1 item(s)."));
        assertTrue(output.contains("transaction completed"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    // entering a bad shipping option first should re-ask instead of crashing
    void invalidShippingOption_reasksUntilValid() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "OVERNIGHT",     // invalid, should get rejected
                "STANDARD",      // valid this time
                "7"              // exit right away
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Please enter STANDARD or NEXT_DAY"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    // picking an invalid menu number should print an error but not crash the app
    void invalidMenuChoice_showsErrorAndKeepsGoing() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "99",            // not a real menu option
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Please enter a number from 1 to 7"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    // trying to checkout with an empty cart should show an error, not "transaction completed"
    void checkoutWithEmptyCart_showsError() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "6",             // checkout with nothing in the cart
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Cannot checkout with an empty cart"));
        assertFalse(output.contains("transaction completed"));
    }

    @Test
    // viewing the cart and removing an item should both work through the full menu flow
    void viewCartThenRemoveItem() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "1", "Shirt", "20.00", "1",  // add item
                "3",                          // view cart
                "5", "Shirt",                 // remove item
                "3",                          // view cart again, should be empty now
                "7"
        ) + "\n";



        String output = runApp(typedInput);

        assertTrue(output.contains("Cart contents:"));
        assertTrue(output.contains("Removed Shirt from the cart."));
        assertTrue(output.contains("Your cart is empty."));
    }
    @Test
// editing an item's quantity through the menu should actually update it
    void editQuantityThroughMenu_updatesItem() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "1", "Shirt", "20.00", "1",   // add item, qty 1
                "4", "Shirt", "5",             // menu: edit quantity -> item name -> new qty
                "3",                           // view cart to confirm
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Updated quantity for Shirt."));
    }
    @Test
// editing a quantity for an item that isn't in the cart should show an error, not crash
    void editQuantityThroughMenu_itemNotFound_showsError() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "1", "Shirt", "20.00", "1",
                "4", "Pants", "5",   // "Pants" was never added
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Error:"));
    }
    @Test
// entering an invalid quantity while editing should just bail out quietly, not crash
    void editQuantityThroughMenu_invalidQuantity_doesNothing() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "1", "Shirt", "20.00", "1",
                "4", "Shirt", "zero",   // invalid quantity input during edit
                "3",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        // quantity should still be 1, unchanged
        assertTrue(output.contains("Shirt"));
    }
    @Test
// requirement: getting the total should also be blocked on an empty cart, same as checkout
    void gettingTotalWithEmptyCart_showsError() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "2",             // get total with nothing in the cart
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Your cart is empty."));
    }

    @Test
// removing an item that was never added should show an error, not crash
    void removeItemThroughMenu_itemNotFound_showsError() {
        String typedInput = String.join("\n",
                "Ibrahim",
                "IL",
                "STANDARD",
                "5", "Pants",   // try to remove something never added
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Error:"));
    }
    @Test
// requirement: item has to have a real name, a blank one should be rejected
    void addingItem_withBlankName_resultsInError() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "", "20.00", "1",   // blank item name, still need price + quantity
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Error:"));
    }
}
