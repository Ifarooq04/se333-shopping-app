package shopping;

import java.util.Set;


 // handles tax + shipping math

public class PricingCalculator {

    private static final Set<String> TAXED_STATES = Set.of("IL", "CA", "NY");
    private static final double SALES_TAX_RATE = 0.06;

    private static final double STANDARD_SHIPPING_COST = 10.00;
    private static final double STANDARD_FREE_SHIPPING_THRESHOLD = 50.00;
    private static final double NEXT_DAY_SHIPPING_COST = 25.00;

    public static final double MIN_PURCHASE_AMOUNT = 1.00;
    public static final double MAX_PURCHASE_AMOUNT = 99_999.99;

    // Returns the sales tax owed on the given subtotal for the given state there
    public double calculateTax(String state, double subtotal) {
        if (state == null) {
            return 0.0;
        }
        if (TAXED_STATES.contains(state.trim().toUpperCase())) {
            return round(subtotal * SALES_TAX_RATE);
        }
        return 0.0;
    }

    // Returns the shipping charge for the given subtotal and shipping option presented
    public double calculateShipping(double subtotal, ShippingOption option) {
        if (option == null) {
            throw new IllegalArgumentException("Shipping option must not be null.");
        }
        if (option == ShippingOption.NEXT_DAY) {
            return NEXT_DAY_SHIPPING_COST;
        }
        // STANDARD
        if (subtotal > STANDARD_FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return STANDARD_SHIPPING_COST;
    }

    // Returns subtotal + tax + shipping, rounded to the nearest cent
    public double calculateTotal(double subtotal, String state, ShippingOption option) {
        double tax = calculateTax(state, subtotal);
        double shipping = calculateShipping(subtotal, option);
        return round(subtotal + tax + shipping);
    }

    // Validates that a raw purchase subtotal falls within the allowed range possible
    public boolean isValidPurchaseAmount(double subtotal) {
        return subtotal >= MIN_PURCHASE_AMOUNT && subtotal <= MAX_PURCHASE_AMOUNT;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
