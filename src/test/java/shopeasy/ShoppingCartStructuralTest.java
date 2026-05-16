package shopeasy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Task 2 – Structural Testing &amp; Code Coverage (Chapter 3)
 *
 * <p>Target class: {@link ShoppingCart}
 *
 * <h3>Workflow</h3>
 * <ol>
 *   <li>Write an initial test suite based on the specification (Javadoc of ShoppingCart).</li>
 *   <li>Run {@code mvn test} to generate the JaCoCo report:
 *       <pre>  target/site/jacoco/index.html</pre></li>
 *   <li>Open the report, navigate to {@code ShoppingCart}, and identify uncovered branches.</li>
 *   <li>Add tests specifically to cover those branches until branch coverage &gt;= 80%.</li>
 *   <li>Take a screenshot of the final JaCoCo summary and put it in {@code report/jacoco-screenshot.png}.</li>
 * </ol>
 *
 * <h3>Branches to think about</h3>
 * <ul>
 *   <li>{@code addItem}: product already in cart vs. new product</li>
 *   <li>{@code removeItem}: product found vs. not found in cart</li>
 *   <li>{@code updateQuantity}: product found vs. not found, quantity valid vs. invalid</li>
 *   <li>{@code applyDiscount}: zero discount, positive discount</li>
 *   <li>{@code total}: empty cart vs. non-empty cart</li>
 * </ul>
 *
 * <h3>Bonus (PIT Mutation Testing)</h3>
 * Run: {@code mvn org.pitest:pitest-maven:mutationCoverage}
 * <br>Examine the HTML report in {@code target/pit-reports/}. Find two surviving mutants,
 * explain why each survived, and describe a test that would kill it. Add this analysis
 * to your reflection report.
 */
class ShoppingCartStructuralTest {

    private ShoppingCart cart;
    private Product apple;
    private Product banana;

    @BeforeEach
    void setUp() {
        cart   = new ShoppingCart();
        apple  = new Product("P001", "Apple",  1.50, 100);
        banana = new Product("P002", "Banana", 0.80, 50);
    }

    @Test
    void addItemNewProduct() {
        cart.addItem(apple, 5);
        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isCloseTo(7.50, within(0.01));
    }

    @Test
    void addItemExistingProduct() {
        cart.addItem(apple, 3);
        cart.addItem(apple, 2);
        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isCloseTo(7.50, within(0.01));
    }

    @Test
    void addMultipleProducts() {
        cart.addItem(apple, 2);
        cart.addItem(banana, 3);
        assertThat(cart.itemCount()).isEqualTo(2);
        assertThat(cart.total()).isCloseTo(5.40, within(0.01));
    }

    @Test
    void removeItemExisting() {
        cart.addItem(apple, 5);
        cart.addItem(banana, 3);
        cart.removeItem("P001");
        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isCloseTo(2.40, within(0.01));
    }

    @Test
    void removeItemNotFound() {
        cart.addItem(apple, 5);
        cart.removeItem("P999");
        assertThat(cart.itemCount()).isEqualTo(1);
    }

    @Test
    void removeItemFromEmptyCart() {
        cart.removeItem("P001");
        assertThat(cart.itemCount()).isEqualTo(0);
    }

    @Test
    void updateQuantityExisting() {
        cart.addItem(apple, 5);
        cart.updateQuantity("P001", 10);
        assertThat(cart.total()).isCloseTo(15.0, within(0.01));
    }

    @Test
    void updateQuantityNotFound() {
        assertThatThrownBy(() -> cart.updateQuantity("P999", 5))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQuantityInvalid() {
        cart.addItem(apple, 5);
        assertThatThrownBy(() -> cart.updateQuantity("P001", 0))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cart.updateQuantity("P001", -5))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void applyDiscountZero() {
        cart.addItem(apple, 10);
        double result = cart.applyDiscount(0);
        assertThat(result).isCloseTo(15.0, within(0.01));
    }

    @Test
    void applyDiscountPositive() {
        cart.addItem(apple, 10);
        double result = cart.applyDiscount(20);
        assertThat(result).isCloseTo(12.0, within(0.01));
    }

    @Test
    void applyDiscountFull() {
        cart.addItem(apple, 10);
        double result = cart.applyDiscount(100);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void applyDiscountEmptyCart() {
        double result = cart.applyDiscount(50);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void totalEmptyCart() {
        assertThat(cart.total()).isEqualTo(0);
    }

    @Test
    void totalSingleItem() {
        cart.addItem(apple, 5);
        assertThat(cart.total()).isCloseTo(7.50, within(0.01));
    }

    @Test
    void totalMultipleItems() {
        cart.addItem(apple, 2);
        cart.addItem(banana, 4);
        assertThat(cart.total()).isCloseTo(6.20, within(0.01));
    }

    @Test
    void itemCountInitial() {
        assertThat(cart.itemCount()).isEqualTo(0);
    }

    @Test
    void getItemsUnmodifiable() {
        cart.addItem(apple, 3);
        assertThatThrownBy(() -> cart.getItems().add(new CartItem(banana, 1)))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void clearCart() {
        cart.addItem(apple, 5);
        cart.addItem(banana, 3);
        cart.clear();
        assertThat(cart.itemCount()).isEqualTo(0);
        assertThat(cart.total()).isEqualTo(0);
    }

    @Test
    void clearEmptyCart() {
        cart.clear();
        assertThat(cart.itemCount()).isEqualTo(0);
    }

    // -----------------------------------------------------------------------
    // TODO: Write your tests below.
    //
    // Start with happy-path tests, then add tests that target specific branches.
    //
    // HINT: Run `mvn test` after every few tests to see coverage progress.
    // -----------------------------------------------------------------------

}
