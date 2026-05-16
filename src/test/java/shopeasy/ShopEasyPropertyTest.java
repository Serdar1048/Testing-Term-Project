package shopeasy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;

class ShopEasyPropertyTest {

    @Property
    void identityProperty(
            @ForAll @DoubleRange(min = 0, max = 10_000) double basePrice) {
        
        PriceCalculator calc = new PriceCalculator();
        double result = calc.calculate(basePrice, 0, 0);
        
        assertThat(result).isEqualTo(basePrice);
    }

    @Property
    void monotonicityProperty(
            @ForAll @DoubleRange(min = 0, max = 10_000) double basePrice,
            @ForAll @DoubleRange(min = 0, max = 100) double discount1,
            @ForAll @DoubleRange(min = 0, max = 100) double discount2,
            @ForAll @DoubleRange(min = 0, max = 100) double taxRate) {
        
        if (discount1 >= discount2) return;
        
        PriceCalculator calc = new PriceCalculator();
        double price1 = calc.calculate(basePrice, discount1, taxRate);
        double price2 = calc.calculate(basePrice, discount2, taxRate);
        
        assertThat(price2).isLessThanOrEqualTo(price1);
    }

    @Property
    void cartCommutativityProperty(
            @ForAll("validProducts") Product product1,
            @ForAll("validProducts") Product product2,
            @ForAll @IntRange(min = 1, max = 100) int qty1,
            @ForAll @IntRange(min = 1, max = 100) int qty2) {
        
        Assume.that(!product1.getId().equals(product2.getId()));
        
        ShoppingCart cart1 = new ShoppingCart();
        cart1.addItem(product1, qty1);
        cart1.addItem(product2, qty2);
        
        ShoppingCart cart2 = new ShoppingCart();
        cart2.addItem(product2, qty2);
        cart2.addItem(product1, qty1);
        
        assertThat(cart1.total()).isCloseTo(cart2.total(), within(0.001));
    }

    @Provide
    Arbitrary<Product> validProducts() {
        return Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
                Arbitraries.doubles().between(0.01, 500.0)
        ).as((name, price) -> new Product("P-" + name, name, price, 100));
    }
}
