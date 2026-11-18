package shopping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


 //Keeps track of whats in the cart, no print statements in here so i can test it separately later

public class ShoppingCart {

    private final List<CartItem> items = new ArrayList<>();


     //Adds an item to the cart. If an item with the same name already exists, its quantity is increased instead of creating a duplicate line

     //@return the number of distinct line items now in the cart

    public int addItem(String name, double unitPrice, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity of less than 1 is not allowed.");
        }
        Optional<CartItem> existing = findItem(name);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            items.add(new CartItem(name, unitPrice, quantity));
        }
        return items.size();
    }

    public void editQuantity(String name, int newQuantity) {
        if (newQuantity < 1) {
            throw new IllegalArgumentException("Quantity of less than 1 is not allowed.");
        }
        CartItem item = findItem(name)
                .orElseThrow(() -> new IllegalArgumentException("No item named \"" + name + "\" in the cart."));
        item.setQuantity(newQuantity);
    }

    public void removeItem(String name) {
        boolean removed = items.removeIf(i -> i.getName().equalsIgnoreCase(name));
        if (!removed) {
            throw new IllegalArgumentException("No item named \"" + name + "\" in the cart.");
        }
    }

    public Optional<CartItem> findItem(String name) {
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getLineTotal).sum();
    }
}
