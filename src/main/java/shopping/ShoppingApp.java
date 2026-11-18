package shopping;

import java.util.Scanner;

// main CLI loop
public class ShoppingApp {

    private final Scanner in;
    private final ShoppingCart cart = new ShoppingCart();
    private final PricingCalculator pricing = new PricingCalculator();
    private Customer customer;

    public ShoppingApp(Scanner in) {
        this.in = in;
    }

    public static void main(String[] args) {
        ShoppingApp app = new ShoppingApp(new Scanner(System.in));
        app.run();
    }

    public void run() {
        System.out.println("=== Welcome to the CLI Shopping Application ===");
        collectCustomerInfo();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> handleAddItem();
                case "2" -> handleGetTotal();
                case "3" -> handleViewCart();
                case "4" -> handleEditQuantity();
                case "5" -> handleRemoveItem();
                case "6" -> handleCheckout();
                case "7" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Error: Please enter a number from 1 to 7.");
            }
        }
    }

    private void collectCustomerInfo() {
        System.out.print("Enter your name: ");
        String name = in.nextLine().trim();

        System.out.print("Enter your state of residence (e.g. IL): ");
        String state = in.nextLine().trim();

        ShippingOption option = null;
        while (option == null) {
            System.out.print("Choose a shipping option (STANDARD / NEXT_DAY): ");
            String raw = in.nextLine().trim();
            try {
                option = ShippingOption.valueOf(raw.toUpperCase().replace(' ', '_'));
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Please enter STANDARD or NEXT_DAY.");
            }
        }

        customer = new Customer(name, state, option);
        System.out.println("Hi, " + customer.getName() + "! Shipping to " + customer.getState()
                + " via " + customer.getShippingOption() + ".");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1) Add item to cart");
        System.out.println("2) Get current total");
        System.out.println("3) See contents of cart");
        System.out.println("4) Edit quantity of an item");
        System.out.println("5) Remove an item");
        System.out.println("6) Checkout");
        System.out.println("7) Exit");
        System.out.print("Choose an option: ");
    }

    private void handleAddItem() {
        System.out.print("Item name: ");
        String name = in.nextLine().trim();

        double price;
        try {
            System.out.print("Unit price: ");
            price = Double.parseDouble(in.nextLine().trim());
            if (price < 0) {
                System.out.println("Error: Price must not be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Price must be a number.");
            return;
        }

        Integer quantity = readQuantity();
        if (quantity == null) {
            return;
        }

        try {
            int itemCount = cart.addItem(name, price, quantity);
            System.out.println("Added. The cart now has " + itemCount + " item(s).");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Integer readQuantity() {
        System.out.print("Quantity: ");
        String raw = in.nextLine().trim();
        int quantity;
        try {
            quantity = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            System.out.println("Error: Quantity must be a whole number.");
            return null;
        }
        if (quantity < 1) {
            System.out.println("Error: Quantity must be at least 1.");
            return null;
        }
        return quantity;
    }

    private void handleGetTotal() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        double subtotal = cart.getSubtotal();
        if (!pricing.isValidPurchaseAmount(subtotal)) {
            System.out.printf("Error: Purchase amount must be between $%.2f and $%.2f.%n",
                    PricingCalculator.MIN_PURCHASE_AMOUNT, PricingCalculator.MAX_PURCHASE_AMOUNT);
            return;
        }
        double tax = pricing.calculateTax(customer.getState(), subtotal);
        double shipping = pricing.calculateShipping(subtotal, customer.getShippingOption());
        double total = pricing.calculateTotal(subtotal, customer.getState(), customer.getShippingOption());
        System.out.printf("Subtotal: $%.2f | Tax: $%.2f | Shipping: $%.2f | Total: $%.2f%n",
                subtotal, tax, shipping, total);
    }

    private void handleViewCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("Cart contents:");
        cart.getItems().forEach(item -> System.out.println("  " + item));
    }

    private void handleEditQuantity() {
        System.out.print("Item name to edit: ");
        String name = in.nextLine().trim();
        Integer quantity = readQuantity();
        if (quantity == null) {
            return;
        }
        try {
            cart.editQuantity(name, quantity);
            System.out.println("Updated quantity for " + name + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleRemoveItem() {
        System.out.print("Item name to remove: ");
        String name = in.nextLine().trim();
        try {
            cart.removeItem(name);
            System.out.println("Removed " + name + " from the cart.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            System.out.println("Error: Cannot checkout with an empty cart.");
            return;
        }
        double subtotal = cart.getSubtotal();
        if (!pricing.isValidPurchaseAmount(subtotal)) {
            System.out.printf("Error: Purchase amount must be between $%.2f and $%.2f.%n",
                    PricingCalculator.MIN_PURCHASE_AMOUNT, PricingCalculator.MAX_PURCHASE_AMOUNT);
            return;
        }
        System.out.println("transaction completed");
    }

}
