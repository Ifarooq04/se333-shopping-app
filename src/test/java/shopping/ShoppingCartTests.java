package shopping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

// unit tests for ShoppingCart by itself
class ShoppingCartTests {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    // a brand new cart should be empty
    void newCart_isEmpty() {
        assertTrue(cart.isEmpty());
    }

    @Test
    // adding one item should make the cart not empty and return a count of 1
    void addItem_newItem_addsToCart() {
        int count = cart.addItem("Shirt", 20.00, 1);
        assertEquals(1, count);
        assertFalse(cart.isEmpty());
    }

    @Test
    // adding the same item name again should combine quantities instead of making a new line
    void addItem_sameNameTwice_combinesQuantity() {
        cart.addItem("Shirt", 20.00, 2);
        int count = cart.addItem("Shirt", 20.00, 3);
        // still just 1 distinct item in the cart, not 2
        assertEquals(1, count);
        assertEquals(5, cart.findItem("Shirt").get().getQuantity());
    }

    @Test
    // name matching should not care about case, "shirt" should combine with "Shirt"
    void addItem_differentCaseSameName_stillCombines() {
        cart.addItem("Shirt", 20.00, 2);
        cart.addItem("shirt", 20.00, 1);
        assertEquals(3, cart.findItem("Shirt").get().getQuantity());
    }

    @Test
    // adding two different items should give a count of 2
    void addItem_twoDifferentItems_countsBoth() {
        cart.addItem("Shirt", 20.00, 1);
        int count = cart.addItem("Pants", 30.00, 1);
        assertEquals(2, count);
    }

    @Test
    // quantity less than 1 should throw, same rule as CartItem
    void addItem_invalidQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem("Shirt", 20.00, 0));
    }

    @Test
    // editing the quantity of an item that exists should work
    void editQuantity_existingItem_updates() {
        cart.addItem("Shirt", 20.00, 1);
        cart.editQuantity("Shirt", 5);
        assertEquals(5, cart.findItem("Shirt").get().getQuantity());
    }

    @Test
    // editing an item that isn't in the cart should throw
    void editQuantity_itemNotFound_throws() {
        assertThrows(IllegalArgumentException.class, () -> cart.editQuantity("Shirt", 5));
    }

    @Test
    // editing to an invalid quantity should throw
    void editQuantity_invalidNewQuantity_throws() {
        cart.addItem("Shirt", 20.00, 1);
        assertThrows(IllegalArgumentException.class, () -> cart.editQuantity("Shirt", 0));
    }

    @Test
    // removing an item that exists should work and take it out of the cart
    void removeItem_existingItem_removesIt() {
        cart.addItem("Shirt", 20.00, 1);
        cart.removeItem("Shirt");
        assertTrue(cart.isEmpty());
    }

    @Test
    // removing an item that isn't there should throw
    void removeItem_itemNotFound_throws() {
        assertThrows(IllegalArgumentException.class, () -> cart.removeItem("Shirt"));
    }

    @Test
    // findItem should return empty when the item isn't in the cart
    void findItem_notFound_returnsEmptyOptional() {
        Optional<CartItem> result = cart.findItem("Shirt");
        assertTrue(result.isEmpty());
    }

    @Test
    // getItems should list everything currently in the cart
    void getItems_returnsAllItems() {
        cart.addItem("Shirt", 20.00, 1);
        cart.addItem("Pants", 30.00, 1);
        assertEquals(2, cart.getItems().size());
    }

    @Test
    // subtotal should add up all the line totals across every item
    void getSubtotal_addsUpAllLineTotals() {
        cart.addItem("Shirt", 20.00, 2); // 40
        cart.addItem("Pants", 30.00, 1); // 30
        assertEquals(70.00, cart.getSubtotal());
    }

    @Test
    // subtotal of an empty cart should just be 0
    void getSubtotal_emptyCart_isZero() {
        assertEquals(0.00, cart.getSubtotal());
    }
    @Test
    void editQuantity_toMinimumBoundary_updates() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("pen", 2.00, 5);
        cart.editQuantity("pen", 1);
        assertEquals(1, cart.findItem("pen").get().getQuantity());
    }
}
