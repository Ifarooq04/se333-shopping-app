package shopping;

// customer info collected at the start
public class Customer {

    private final String name;
    private final String state;
    private final ShippingOption shippingOption;

    public Customer(String name, String state, ShippingOption shippingOption) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank.");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State must not be blank.");
        }
        if (shippingOption == null) {
            throw new IllegalArgumentException("Shipping option must not be null.");
        }
        this.name = name;
        this.state = state.trim().toUpperCase();
        this.shippingOption = shippingOption;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public ShippingOption getShippingOption() {
        return shippingOption;
    }
}
