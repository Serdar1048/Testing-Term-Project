package shopeasy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderProcessor Mock Tests")
class OrderProcessorMockTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderProcessor orderProcessor;

    private ShoppingCart cart;
    private Product widget;
    private Product gadget;

    @BeforeEach
    void setUp() {
        cart   = new ShoppingCart();
        widget = new Product("P001", "Widget", 25.0, 100);
        gadget = new Product("P002", "Gadget", 15.0, 50);
    }

    @Test
    @DisplayName("Happy path: inventory available, payment succeeds → returns non-null Order")
    void processHappyPath() {
        cart.addItem(widget, 2);

        when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
        when(paymentGateway.charge("customer-1", 50.0)).thenReturn(true);

        Order order = orderProcessor.process("customer-1", cart);

        assertThat(order).isNotNull();
        assertThat(order.getCustomerId()).isEqualTo("customer-1");
        assertThat(order.getTotal()).isEqualTo(50.0);
        assertThat(order.getItems()).hasSize(1);
        verify(inventoryService).isAvailable(widget, 2);
        verify(paymentGateway).charge("customer-1", 50.0);
    }

    @Test
    @DisplayName("Inventory failure: isAvailable returns false → returns null, charge never called")
    void processInventoryFailure() {
        cart.addItem(widget, 10);

        when(inventoryService.isAvailable(widget, 10)).thenReturn(false);

        Order order = orderProcessor.process("customer-1", cart);

        assertThat(order).isNull();
        verify(inventoryService).isAvailable(widget, 10);
        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }

    @Test
    @DisplayName("Payment failure: inventory OK, charge returns false → returns null")
    void processPaymentFailure() {
        cart.addItem(widget, 2);

        when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
        when(paymentGateway.charge("customer-1", 50.0)).thenReturn(false);

        Order order = orderProcessor.process("customer-1", cart);

        assertThat(order).isNull();
        verify(inventoryService).isAvailable(widget, 2);
        verify(paymentGateway).charge("customer-1", 50.0);
    }

    @Test
    @DisplayName("Partial quantity available: one item OK, another unavailable → returns null, charge never called")
    void processPartialQuantityAvailable() {
        cart.addItem(widget, 2);
        cart.addItem(gadget, 5);

        when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
        when(inventoryService.isAvailable(gadget, 5)).thenReturn(false);

        Order order = orderProcessor.process("customer-1", cart);

        assertThat(order).isNull();
        verify(inventoryService).isAvailable(widget, 2);
        verify(inventoryService).isAvailable(gadget, 5);
        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }

    @Test
    @DisplayName("Multiple items, all available, payment OK → returns Order with all items")
    void processMultipleItemsAllAvailable() {
        cart.addItem(widget, 2);
        cart.addItem(gadget, 3);

        when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
        when(inventoryService.isAvailable(gadget, 3)).thenReturn(true);
        when(paymentGateway.charge("customer-1", 95.0)).thenReturn(true);

        Order order = orderProcessor.process("customer-1", cart);

        assertThat(order).isNotNull();
        assertThat(order.getCustomerId()).isEqualTo("customer-1");
        assertThat(order.getTotal()).isEqualTo(95.0);
        assertThat(order.getItems()).hasSize(2);
        verify(paymentGateway).charge("customer-1", 95.0);
    }

    @Test
    @DisplayName("Empty cart → throws IllegalArgumentException")
    void processEmptyCart() {
        assertThatThrownBy(() -> orderProcessor.process("customer-1", cart))
            .isInstanceOf(IllegalArgumentException.class);
        
        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }

    @Test
    @DisplayName("Null customer ID → throws IllegalArgumentException")
    void processNullCustomerId() {
        cart.addItem(widget, 1);
        
        assertThatThrownBy(() -> orderProcessor.process(null, cart))
            .isInstanceOf(IllegalArgumentException.class);
        
        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }

    @Test
    @DisplayName("Blank customer ID → throws IllegalArgumentException")
    void processBlankCustomerId() {
        cart.addItem(widget, 1);
        
        assertThatThrownBy(() -> orderProcessor.process("   ", cart))
            .isInstanceOf(IllegalArgumentException.class);
        
        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }
}
