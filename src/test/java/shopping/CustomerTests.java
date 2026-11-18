package shopping;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// unit tests for Customer on its own
class CustomerTests {

    @Test
    // normal valid customer
    void constructor_validInput_setsFields() {
        Customer customer = new Customer("Ibrahim", "IL", ShippingOption.STANDARD);
        assertEquals("Ibrahim", customer.getName());
        assertEquals("IL", customer.getState());
        assertEquals(ShippingOption.STANDARD, customer.getShippingOption());
    }

    @Test
    // state should get uppercased and trimmed automatically
    void constructor_lowercaseStateWithSpaces_getsNormalized() {
        Customer customer = new Customer("Ibrahim", "  il  ", ShippingOption.STANDARD);
        assertEquals("IL", customer.getState());
    }

    @Test
    // null name should throw
    void constructor_nullName_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(null, "IL", ShippingOption.STANDARD));
    }

    @Test
    // blank name should throw too
    void constructor_blankName_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("   ", "IL", ShippingOption.STANDARD));
    }

    @Test
    // null state should throw
    void constructor_nullState_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("Ibrahim", null, ShippingOption.STANDARD));
    }

    @Test
    // blank state should throw
    void constructor_blankState_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("Ibrahim", "   ", ShippingOption.STANDARD));
    }

    @Test
    // null shipping option should throw
    void constructor_nullShippingOption_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("Ibrahim", "IL", null));
    }
}
