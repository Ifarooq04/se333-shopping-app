package shopping;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// unit tests for CartItem, just testing this one class on its own
class CartItemTests {

    @Test
    // normal valid item, nothing weird going on
    void constructor_validItem_setsAllFields() {
        CartItem item = new CartItem("Shirt", 20.00, 2);
        assertEquals("Shirt", item.getName());
        assertEquals(20.00, item.getUnitPrice());
        assertEquals(2, item.getQuantity());
    }

    @Test
    // null name should throw
    void constructor_nullName_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 10.00, 1));
    }

    @Test
    // blank name should throw too, not just null
    void constructor_blankName_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem("   ", 10.00, 1));
    }

    @Test
    // negative price doesn't make sense
    void constructor_negativePrice_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem("Shirt", -5.00, 1));
    }

    @Test
    // boundary: price of exactly 0 should be fine, the check is only for negative
    void constructor_zeroPrice_isAllowed() {
        CartItem item = new CartItem("FreeSample", 0.00, 1);
        assertEquals(0.00, item.getUnitPrice());
    }

    @Test
    // boundary: quantity of exactly 1 is the minimum allowed
    void constructor_quantityAtMinimumBoundary() {
        CartItem item = new CartItem("Shirt", 10.00, 1);
        assertEquals(1, item.getQuantity());
    }

    @Test
    // quantity of 0 should throw, has to be at least 1
    void constructor_quantityZero_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem("Shirt", 10.00, 0));
    }

    @Test
    // negative quantity should also throw
    void constructor_negativeQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem("Shirt", 10.00, -1));
    }

    @Test
    void setQuantity_atMinimumBoundary_updates() {
        CartItem item = new CartItem("pen", 2.00, 5);
        item.setQuantity(1);
        assertEquals(1, item.getQuantity());
    }

    @Test
    // setQuantity should throw on an invalid value same as the constructor does
    void setQuantity_invalidValue_throws() {
        CartItem item = new CartItem("Shirt", 10.00, 1);
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(0));
    }

    @Test
    // line total is just price times quantity
    void getLineTotal_calculatesCorrectly() {
        CartItem item = new CartItem("Shirt", 15.00, 3);
        assertEquals(45.00, item.getLineTotal());
    }

    @Test
    // two items with the same name/price/qty should be equal
    void equals_sameValues_returnsTrue() {
        CartItem item1 = new CartItem("Shirt", 15.00, 3);
        CartItem item2 = new CartItem("Shirt", 15.00, 3);
        assertEquals(item1, item2);
    }

    @Test
    // different quantity should make them not equal
    void equals_differentQuantity_returnsFalse() {
        CartItem item1 = new CartItem("Shirt", 15.00, 3);
        CartItem item2 = new CartItem("Shirt", 15.00, 4);
        assertNotEquals(item1, item2);
    }

    @Test
    // comparing to something that isn't even a CartItem should just be false, not crash
    void equals_differentType_returnsFalse() {
        CartItem item = new CartItem("Shirt", 15.00, 3);
        assertNotEquals(item, "Shirt");
    }

    @Test
    // same object compared to itself should be equal
    void equals_sameReference_returnsTrue() {
        CartItem item = new CartItem("Shirt", 15.00, 3);
        assertEquals(item, item);
    }

    @Test
    // equal items should also have equal hash codes
    void hashCode_sameValues_matches() {
        CartItem item1 = new CartItem("Shirt", 15.00, 3);
        CartItem item2 = new CartItem("Shirt", 15.00, 3);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
    @Test
    void hashCode_differentValues_differ() {
        CartItem a = new CartItem("pen", 2.00, 1);
        CartItem b = new CartItem("book", 9.99, 3);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    // just making sure toString has the item info in it and doesn't throw
    void toString_containsNameAndTotal() {
        CartItem item = new CartItem("Shirt", 15.00, 3);
        String text = item.toString();
        assertTrue(text.contains("Shirt"));
        assertTrue(text.contains("45.00"));
    }
    @Test
        // same name and quantity but different price should also not be equal
    void equals_differentPrice_returnsFalse() {
        CartItem item1 = new CartItem("Shirt", 15.00, 3);
        CartItem item2 = new CartItem("Shirt", 25.00, 3);
        assertNotEquals(item1, item2);
    }

    @Test
        // same price and quantity but different name should not be equal either
    void equals_differentName_returnsFalse() {
        CartItem item1 = new CartItem("Shirt", 15.00, 3);
        CartItem item2 = new CartItem("Pants", 15.00, 3);
        assertNotEquals(item1, item2);
    }
}
