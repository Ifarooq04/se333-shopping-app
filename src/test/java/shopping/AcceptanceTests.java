package shopping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

// acceptance tests - these come straight from the actual requirements in the project doc,
// checking that the app does what it's actually supposed to do from a customer's point of view
class AcceptanceTests {

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
    // requirement: "the given item is added to the shopping cart and the user receives
    // a message indicating the current count of items in the cart"
    void addingItem_tellsUserCurrentCartCount() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "20.00", "1",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("The cart now has 1 item(s)"));
    }

    @Test
    // requirement: "the user receives a message that reports the total for the items in the
    // shopping cart. Taxes and shipping charges are included in this sum"
    void gettingTotal_includesTaxAndShipping() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "20.00", "3",   // subtotal 60, over free shipping threshold
                "2",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        // 60 subtotal, 6% IL tax = 3.60, shipping free since over $50 -> total 63.60
        assertTrue(output.contains("Tax: $3.60"));
        assertTrue(output.contains("Shipping: $0.00"));
        assertTrue(output.contains("Total: $63.60"));
    }

    @Test
    // requirement: "when the checkout action is selected, a message 'transaction completed'
    // will be displayed to the user"
    void checkout_showsTransactionCompletedMessage() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "20.00", "1",
                "6",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("transaction completed"));
    }

    @Test
    // requirement: "a quantity of less than 1 or that is non integer should result in an error"
    void addingItem_withZeroQuantity_resultsInError() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "20.00", "0",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Quantity must be at least 1"));
    }

    @Test
    // requirement: a non-integer quantity should also be an error
    void addingItem_withNonIntegerQuantity_resultsInError() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "20.00", "two",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Quantity must be a whole number"));
    }

    @Test
    // requirement: "3 states require the payment of a sales tax... IL, CA, NY, 6%, all other
    // states charge no tax" - checking an untaxed state actually shows $0 tax
    void untaxedState_showsZeroTax() {
        String typedInput = String.join("\n",
                "Ibrahim", "TX", "STANDARD",
                "1", "Shirt", "20.00", "1",
                "2",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Tax: $0.00"));
    }

    @Test
    // requirement: "NEXT_DAY - the cost of next day shipping is $25. There is no free
    // NEXT_DAY shipping" - even with a big subtotal it should still charge $25
    void nextDayShipping_neverFree_evenOverFiftyDollars() {
        String typedInput = String.join("\n",
                "Ibrahim", "TX", "NEXT_DAY",
                "1", "Shirt", "100.00", "1",
                "2",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Shipping: $25.00"));
    }
    @Test
        // requirement: price has to make sense, a negative price should be rejected
    void addingItem_withNegativePrice_resultsInError() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "-5.00",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Price must not be negative"));
    }

    @Test
        // a price that isn't even a number should also be rejected, not crash the app
    void addingItem_withNonNumericPrice_resultsInError() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "free",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Price must be a number"));
    }

    @Test
        // requirement: "the smallest acceptable purchase amount is $1" - a $0 item should
        // get flagged as an invalid purchase amount when checking the total
    void gettingTotal_belowMinimumPurchase_showsError() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "0.00", "1",
                "2",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Purchase amount must be between"));
    }

    @Test
        // same minimum-purchase rule should also block checkout, not just the total screen
    void checkout_belowMinimumPurchase_showsErrorInsteadOfCompleting() {
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "0.00", "1",
                "6",
                "7"
        ) + "\n";

        String output = runApp(typedInput);

        assertTrue(output.contains("Purchase amount must be between"));
        assertFalse(output.contains("transaction completed"));
    }

    @Test
        // covering the actual main() entry point, same as a real user launching the app
    void mainMethod_runsFullSessionFromRealEntryPoint() {
        java.io.InputStream originalIn = System.in;
        String typedInput = String.join("\n",
                "Ibrahim", "IL", "STANDARD",
                "1", "Shirt", "20.00", "1",
                "7"
        ) + "\n";

        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));
        System.setIn(new ByteArrayInputStream(typedInput.getBytes()));

        ShoppingApp.main(new String[] {});

        System.setIn(originalIn);
        String output = outputCapture.toString();
        assertTrue(output.contains("Goodbye!"));
    }
}
