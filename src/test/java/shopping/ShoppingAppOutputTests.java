package shopping;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingAppOutputTests {

    private static final String START = "Sam\nIL\nSTANDARD\n";
    private static final String LS = System.lineSeparator();

    // runs the app with the typed input and returns everything it printed
    private String run(String input) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));
        try {
            new ShoppingApp(new Scanner(input)).run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString();
    }

    private int count(String text, String part) {
        return text.split(Pattern.quote(part), -1).length - 1;
    }

    @Test
    void startup_printsWelcomeAndCustomerPrompts() {
        String out = run(START + "7\n");
        assertTrue(out.contains("=== Welcome to the CLI Shopping Application ==="));
        assertTrue(out.contains("Enter your name: "));
        assertTrue(out.contains("Enter your state of residence (e.g. IL): "));
        assertTrue(out.contains("Choose a shipping option (STANDARD / NEXT_DAY): "));
        assertTrue(out.contains("Hi, Sam!"));
    }

    @Test
    void menu_printsBlankLineAndEveryOption() {
        String out = run(START + "7\n");
        assertTrue(out.contains(LS + LS + "1) Add item to cart"));
        assertTrue(out.contains("2) Get current total"));
        assertTrue(out.contains("3) See contents of cart"));
        assertTrue(out.contains("4) Edit quantity of an item"));
        assertTrue(out.contains("5) Remove an item"));
        assertTrue(out.contains("6) Checkout"));
        assertTrue(out.contains("7) Exit"));
        assertTrue(out.contains("Choose an option: "));
    }

    @Test
    void addItem_printsItsPrompts() {
        String out = run(START + "1\nPen\n2.50\n3\n7\n");
        assertTrue(out.contains("Item name: "));
        assertTrue(out.contains("Unit price: "));
        assertTrue(out.contains("Quantity: "));
    }

    @Test
    void editQuantity_printsItsPrompt() {
        String out = run(START + "4\nPen\n2\n7\n");
        assertTrue(out.contains("Item name to edit: "));
    }

    @Test
    void removeItem_printsItsPrompt() {
        String out = run(START + "5\nPen\n7\n");
        assertTrue(out.contains("Item name to remove: "));
    }

    @Test
    void addItem_nonNumberQuantity_onlyShowsWholeNumberError() {
        String out = run(START + "1\nPen\n2.50\nabc\n7\n");
        assertTrue(out.contains("Error: Quantity must be a whole number."));
        assertFalse(out.contains("less than 1"));
    }

    @Test
    void addItem_zeroQuantity_stopsAfterOneError() {
        String out = run(START + "1\nPen\n2.50\n0\n7\n");
        assertTrue(out.contains("Error: Quantity must be at least 1."));
        assertFalse(out.contains("less than 1"));
    }
}