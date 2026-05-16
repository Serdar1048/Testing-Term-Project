package shopeasy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Design by Contract Tests")
class ContractTest {

    private ShoppingCart cart;
    private PriceCalculator calculator;
    private Product product;

    @BeforeEach
    void setUp() {
        cart       = new ShoppingCart();
        calculator = new PriceCalculator();
        product    = new Product("P001", "Widget", 10.0, 50);
    }

    @Test
    @DisplayName("ShoppingCart.addItem - null product violates pre-condition")
    void addItemNullProductThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> cart.addItem(null, 1));
    }

    @Test
    @DisplayName("ShoppingCart.addItem - negative quantity violates pre-condition")
    void addItemNegativeQuantityThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> cart.addItem(product, -5));
    }

    @Test
    @DisplayName("ShoppingCart.addItem - zero quantity violates pre-condition")
    void addItemZeroQuantityThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> cart.addItem(product, 0));
    }

    @Test
    @DisplayName("ShoppingCart.addItem - valid inputs satisfy contract")
    void addItemValidInputsOk() {
        assertDoesNotThrow(() -> cart.addItem(product, 5));
        assertEquals(1, cart.itemCount());
    }

    @Test
    @DisplayName("ShoppingCart.addItem - post-condition satisfied after add")
    void addItemPostConditionSatisfied() {
        cart.addItem(product, 3);
        assertTrue(cart.getItems().stream()
            .anyMatch(i -> i.getProduct().getId().equals("P001")));
    }

    @Test
    @DisplayName("ShoppingCart.applyDiscount - negative discount violates pre-condition")
    void applyDiscountNegativeThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> cart.applyDiscount(-10));
    }

    @Test
    @DisplayName("ShoppingCart.applyDiscount - discount > 100 violates pre-condition")
    void applyDiscountAbove100ThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> cart.applyDiscount(150));
    }

    @Test
    @DisplayName("ShoppingCart.applyDiscount - valid discount satisfies contract")
    void applyDiscountValidRateOk() {
        cart.addItem(product, 10);
        assertDoesNotThrow(() -> cart.applyDiscount(20));
    }

    @Test
    @DisplayName("ShoppingCart.applyDiscount - post-condition: result <= total")
    void applyDiscountPostConditionSatisfied() {
        cart.addItem(product, 10);
        double result = cart.applyDiscount(25);
        assertTrue(result <= cart.total(), "Discounted result must be <= total");
    }

    @Test
    @DisplayName("PriceCalculator.calculate - negative basePrice violates pre-condition")
    void calculateNegativeBasePriceThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> calculator.calculate(-50, 10, 5));
    }

    @Test
    @DisplayName("PriceCalculator.calculate - negative discount violates pre-condition")
    void calculateNegativeDiscountThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> calculator.calculate(100, -10, 5));
    }

    @Test
    @DisplayName("PriceCalculator.calculate - discount > 100 violates pre-condition")
    void calculateDiscountAbove100ThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> calculator.calculate(100, 150, 5));
    }

    @Test
    @DisplayName("PriceCalculator.calculate - negative tax violates pre-condition")
    void calculateNegativeTaxThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> calculator.calculate(100, 10, -5));
    }

    @Test
    @DisplayName("PriceCalculator.calculate - tax > 100 violates pre-condition")
    void calculateTaxAbove100ThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> calculator.calculate(100, 10, 150));
    }

    @Test
    @DisplayName("PriceCalculator.calculate - valid inputs satisfy contract")
    void calculateValidInputsOk() {
        assertDoesNotThrow(() -> calculator.calculate(100, 20, 15));
    }

    @Test
    @DisplayName("PriceCalculator.calculate - post-condition: result >= 0")
    void calculatePostConditionSatisfied() {
        double result = calculator.calculate(100, 50, 50);
        assertTrue(result >= 0, "Result must be >= 0");
    }

    @Test
    @DisplayName("Invariant: ShoppingCart total always >= 0")
    void invariantCartTotalAlwaysNonNegative() {
        assertTrue(cart.total() >= 0);
        cart.addItem(product, 5);
        assertTrue(cart.total() >= 0);
        cart.applyDiscount(100);
        assertTrue(cart.total() >= 0);
    }
}
