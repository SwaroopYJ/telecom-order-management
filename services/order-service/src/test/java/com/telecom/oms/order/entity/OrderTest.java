
package com.telecom.oms.order.entity;

import com.telecom.oms.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldAllowPendingToProcessing() {
        Order order = new Order();

        order.transitionTo(OrderStatus.PROCESSING);

        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    void shouldAllowProcessingToConfirmed() {
        Order order = new Order();
        order.transitionTo(OrderStatus.PROCESSING);

        order.transitionTo(OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void shouldRejectPendingToConfirmed() {
        Order order = new Order();

        assertThrows(
                IllegalStateException.class,
                () -> order.transitionTo(OrderStatus.CONFIRMED)
        );
    }

    @Test
    void shouldRejectTransitionFromConfirmed() {
        Order order = new Order();
        order.transitionTo(OrderStatus.PROCESSING);
        order.transitionTo(OrderStatus.CONFIRMED);

        assertThrows(
                IllegalStateException.class,
                () -> order.transitionTo(OrderStatus.CANCELLED)
        );
    }

    @Test
    void shouldAllowPendingToCancelled() {
        Order order = new Order();

        order.transitionTo(OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldRejectNullStatus() {
        Order order = new Order();

        assertThrows(
                IllegalArgumentException.class,
                () -> order.transitionTo(null)
        );
    }
}
