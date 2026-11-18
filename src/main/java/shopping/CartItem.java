package shopping;

import java.util.Objects;

// one item + qty in the cart
public class CartItem {

    private final String name;
    private final double unitPrice;
    private int quantity;

    public CartItem(String name, double unitPrice, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price must not be negative.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return unitPrice * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem cartItem = (CartItem) o;
        return Double.compare(unitPrice, cartItem.unitPrice) == 0
                && quantity == cartItem.quantity
                && name.equals(cartItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unitPrice, quantity);
    }

    @Override
    public String toString() {
        return String.format("%s x%d @ $%.2f = $%.2f", name, quantity, unitPrice, getLineTotal());
    }
}
