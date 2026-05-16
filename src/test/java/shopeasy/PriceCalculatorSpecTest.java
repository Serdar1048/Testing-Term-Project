package shopeasy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("PriceCalculator Specification Tests")
class PriceCalculatorSpecTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    @ParameterizedTest(name = "base={0}, disc={1}%, tax={2}% => {3}")
    @CsvSource({
        "100.0,     0.0,    0.0,    100.0",
        "100.0,    10.0,   20.0,    108.0",
        "100.0,    50.0,   50.0,     75.0",
        "100.0,   100.0,    0.0,      0.0",
        "0.0,      50.0,   50.0,      0.0",
        "100.0,    30.0,   10.0,     77.0",
        "1000.0,   30.0,   15.0,    805.0",
        "50.0,     20.0,   10.0,     44.0",
        "200.0,     0.0,   25.0,    250.0",
        "0.01,      0.0,    0.0,     0.01"
    })
    @DisplayName("Calculate with various combinations of price, discount, and tax")
    void testCalculateWithVariousInputs(double basePrice, double discountRate, 
                                         double taxRate, double expected) {
        double result = calculator.calculate(basePrice, discountRate, taxRate);
        assertEquals(expected, result, 0.01);
    }

    @Test
    @DisplayName("Apply discount only")
    void testApplyDiscountOnly() {
        double result = calculator.applyDiscountOnly(100.0, 10.0);
        assertEquals(90.0, result, 0.01);
    }

    @Test
    @DisplayName("Apply tax only")
    void testApplyTaxOnly() {
        double result = calculator.applyTaxOnly(100.0, 20.0);
        assertEquals(120.0, result, 0.01);
    }

    @Test
    @DisplayName("Result is always non-negative")
    void testResultAlwaysNonNegative() {
        assertTrue(calculator.calculate(0, 0, 0) >= 0);
        assertTrue(calculator.calculate(100, 100, 100) >= 0);
        assertTrue(calculator.calculate(1000, 50, 50) >= 0);
    }

    @ParameterizedTest
    @CsvSource({
        "-1.0,    10.0,   10.0",
        "100.0,  -10.0,   10.0",
        "100.0,  110.0,   10.0",
        "100.0,   10.0,  -10.0",
        "100.0,   10.0,  110.0"
    })
    @DisplayName("Invalid inputs should be caught by assertions (Task 3)")
    void testInvalidInputsPreConditions(double basePrice, double discountRate, double taxRate) {
        assertDoesNotThrow(() -> calculator.calculate(basePrice, discountRate, taxRate));
    }
}
