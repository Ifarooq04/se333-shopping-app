package shopping;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// unit tests - testing PricingCalculator all by itself, no other classes involved
class PricingCalculatorTests {

    private final PricingCalculator pricing = new PricingCalculator();

    // ---- calculateTax ----

    @Test
    // IL is a taxed state, straightforward valid case
    void tax_ilState_appliesSixPercent() {
        assertEquals(6.00, pricing.calculateTax("IL", 100.00));
    }

    @Test
    // CA is taxed too
    void tax_caState_appliesSixPercent() {
        assertEquals(6.00, pricing.calculateTax("CA", 100.00));
    }

    @Test
    // and NY, the third taxed state
    void tax_nyState_appliesSixPercent() {
        assertEquals(6.00, pricing.calculateTax("NY", 100.00));
    }

    @Test
    // a state not in the taxed list should be 0 tax
    void tax_untaxedState_isZero() {
        assertEquals(0.00, pricing.calculateTax("TX", 100.00));
    }

    @Test
    // null state shouldn't blow up, just no tax
    void tax_nullState_isZero() {
        assertEquals(0.00, pricing.calculateTax(null, 100.00));
    }

    @Test
    // lowercase state should still match since the method uppercases it
    void tax_lowercaseState_stillMatches() {
        assertEquals(6.00, pricing.calculateTax("il", 100.00));
    }

    @Test
    // extra spaces around the state should still work since it trims
    void tax_stateWithWhitespace_stillMatches() {
        assertEquals(6.00, pricing.calculateTax("  IL  ", 100.00));
    }

    // ---- calculateShipping ----

    @Test
    // standard shipping under the free threshold should cost $10
    void shipping_standardUnderThreshold_costsTen() {
        assertEquals(10.00, pricing.calculateShipping(30.00, ShippingOption.STANDARD));
    }

    @Test
    // boundary check: exactly $50 should NOT be free, the rule is "over $50" not "$50 or more"
    void shipping_standardExactlyAtFiftyBoundary_stillCostsTen() {
        assertEquals(10.00, pricing.calculateShipping(50.00, ShippingOption.STANDARD));
    }

    @Test
    // just one cent over $50 should be free
    void shipping_standardJustOverFiftyBoundary_isFree() {
        assertEquals(0.00, pricing.calculateShipping(50.01, ShippingOption.STANDARD));
    }

    @Test
    // next day shipping is always $25 no matter the subtotal, even under $50
    void shipping_nextDayUnderThreshold_isStillTwentyFive() {
        assertEquals(25.00, pricing.calculateShipping(10.00, ShippingOption.NEXT_DAY));
    }

    @Test
    // next day shipping over $50 is still $25, no free next day shipping ever
    void shipping_nextDayOverThreshold_isStillTwentyFive() {
        assertEquals(25.00, pricing.calculateShipping(100.00, ShippingOption.NEXT_DAY));
    }

    @Test
    // null shipping option should throw, can't ship with no option picked
    void shipping_nullOption_throws() {
        assertThrows(IllegalArgumentException.class, () -> pricing.calculateShipping(30.00, null));
    }

    // ---- calculateTotal ----

    @Test
    // put it all together: taxed state + standard shipping under the free threshold
    void total_taxedStateWithStandardShipping() {
        // subtotal 30, tax = 1.80 (6% of 30), shipping = 10 (under free threshold) -> 41.80
        assertEquals(41.80, pricing.calculateTotal(30.00, "IL", ShippingOption.STANDARD));
    }

    @Test
    // untaxed state, standard shipping, subtotal over the free shipping threshold
    void total_untaxedStateWithFreeShipping() {
        // subtotal 60, tax = 0, shipping = 0 (over 50) -> 60.00
        assertEquals(60.00, pricing.calculateTotal(60.00, "TX", ShippingOption.STANDARD));
    }

    @Test
    // taxed state with next day shipping
    void total_taxedStateWithNextDayShipping() {
        // subtotal 100, tax = 6.00, shipping = 25 -> 131.00
        assertEquals(131.00, pricing.calculateTotal(100.00, "NY", ShippingOption.NEXT_DAY));
    }

    // ---- isValidPurchaseAmount ----

    @Test
    // boundary: exactly the minimum $1 should be valid
    void validAmount_atMinimumBoundary() {
        assertTrue(pricing.isValidPurchaseAmount(1.00));
    }

    @Test
    // just under the minimum should be invalid
    void validAmount_justBelowMinimum_isInvalid() {
        assertFalse(pricing.isValidPurchaseAmount(0.99));
    }

    @Test
    // boundary: exactly the max $99,999.99 should be valid
    void validAmount_atMaximumBoundary() {
        assertTrue(pricing.isValidPurchaseAmount(99999.99));
    }

    @Test
    // just over the max should be invalid
    void validAmount_justAboveMaximum_isInvalid() {
        assertFalse(pricing.isValidPurchaseAmount(100000.00));
    }

    @Test
    // something comfortably in the middle of the range should be valid
    void validAmount_middleOfRange_isValid() {
        assertTrue(pricing.isValidPurchaseAmount(500.00));
    }
}
