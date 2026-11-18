package shopping;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// integration tests - this time checking that ShoppingCart and PricingCalculator
// actually work right together, not just on their own
class IntegrationTests {

    private final PricingCalculator pricing = new PricingCalculator();

    @Test
    // cart subtotal feeds into pricing correctly for a taxed state with standard shipping
    void cartSubtotal_feedsIntoTotal_taxedStateStandardShipping() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Shirt", 10.00, 3); // subtotal = 30

        double subtotal = cart.getSubtotal();
        double total = pricing.calculateTotal(subtotal, "IL", ShippingOption.STANDARD);

        // 30 subtotal + 1.80 tax (6%) + 10 shipping (under free threshold) = 41.80
        assertEquals(41.80, total);
    }

    @Test
    // adding multiple different items, then combining them into one total with next day shipping
    void multipleItems_combineCorrectly_withNextDayShipping() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Shirt", 20.00, 1); // 20
        cart.addItem("Pants", 40.00, 1); // 40
        // subtotal = 60

        double subtotal = cart.getSubtotal();
        double total = pricing.calculateTotal(subtotal, "CA", ShippingOption.NEXT_DAY);

        // 60 subtotal + 3.60 tax (6% of 60) + 25 next day shipping = 88.60
        assertEquals(88.60, total);
    }

    @Test
    // combining quantities on the same item name should still total up right
    void combinedQuantity_stillCalculatesRightTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Shirt", 25.00, 1);
        cart.addItem("shirt", 25.00, 1); // same item, different case, should combine to qty 2

        double subtotal = cart.getSubtotal(); // 50.00
        boolean isValid = pricing.isValidPurchaseAmount(subtotal);
        double total = pricing.calculateTotal(subtotal, "TX", ShippingOption.STANDARD);

        assertTrue(isValid);
        // 50 subtotal, no tax (TX untaxed), standard shipping is $10 since subtotal is not OVER 50
        assertEquals(60.00, total);
    }

    @Test
    // removing an item should bring the subtotal (and therefore the total) back down
    void removingItem_lowersSubtotalAndTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Shirt", 20.00, 1);
        cart.addItem("Pants", 100.00, 1);
        cart.removeItem("Pants");

        double subtotal = cart.getSubtotal(); // just the shirt now = 20
        double total = pricing.calculateTotal(subtotal, "TX", ShippingOption.STANDARD);

        // 20 subtotal + 0 tax + 10 shipping = 30
        assertEquals(30.00, total);
    }
}
