
package com.telecom.oms.order.service;

import com.telecom.oms.order.dto.CreateOrderRequest;
import com.telecom.oms.order.dto.OrderResponse;
import com.telecom.oms.order.entity.Order;
import com.telecom.oms.order.entity.OrderIdempotency;
import com.telecom.oms.order.enums.OrderStatus;
import com.telecom.oms.order.exception.IdempotencyConflictException;
import com.telecom.oms.order.repository.OrderIdempotencyRepository;
import com.telecom.oms.order.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderIdempotencyRepository idempotencyRepository;

    @Mock
    private OrderRequestHasher requestHasher;

    @Mock
    private OrderCreationTransactionService creationTransactionService;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {

        request = new CreateOrderRequest(
                "GB71290859",
                List.of(
                        new CreateOrderRequest.OrderItemRequest(1L, 2)
                )
        );

        when(requestHasher.hash(request))
                .thenReturn("test-request-hash");
    }

    @Test
    void createOrder_shouldReturnExistingOrderForSameKeyAndRequest() {

        // Arrange
        OrderIdempotency existingIdempotency =
                new OrderIdempotency(
                        "order-key-001",
                        "test-request-hash"
                );

        existingIdempotency.setOrderId(1L);

        Order existingOrder = createExistingOrder();

        when(idempotencyRepository.findByIdempotencyKey("order-key-001"))
                .thenReturn(Optional.of(existingIdempotency));

        when(orderRepository.findWithItemsById(1L))
                .thenReturn(Optional.of(existingOrder));

        // Act
        OrderResponse response =
                orderService.createOrder(request, "order-key-001");

        // Assert
        assertNotNull(response);
        assertEquals("ORD-TEST123", response.orderNumber());
        assertEquals("GB71290859", response.accountNumber());

        verify(creationTransactionService, never())
                .createOrder(any(), anyString(), anyString());
    }

    @Test
    void createOrder_shouldRejectSameKeyWithDifferentRequest() {

        // Arrange
        OrderIdempotency existingIdempotency =
                new OrderIdempotency(
                        "order-key-001",
                        "original-request-hash"
                );

        when(requestHasher.hash(request))
                .thenReturn("different-request-hash");

        when(idempotencyRepository.findByIdempotencyKey("order-key-001"))
                .thenReturn(Optional.of(existingIdempotency));

        // Act & Assert
        assertThrows(
                IdempotencyConflictException.class,
                () -> orderService.createOrder(request, "order-key-001")
        );

        verify(orderRepository, never()).findById(anyLong());

        verify(creationTransactionService, never())
                .createOrder(any(), anyString(), anyString());
    }

    @Test
    void createOrder_shouldCreateNewOrderForNewKey() {

        // Arrange
        OrderResponse expectedResponse = new OrderResponse(
                2L,
                "ORD-NEW1234",
                "GB71290859",
                OrderStatus.PENDING,
                new BigDecimal("59.98"),
                "GBP",
                LocalDateTime.now(),
                List.of()
        );

        when(idempotencyRepository.findByIdempotencyKey("order-key-002"))
                .thenReturn(Optional.empty());

        when(creationTransactionService.createOrder(
                request,
                "order-key-002",
                "test-request-hash"
        )).thenReturn(expectedResponse);

        // Act
        OrderResponse response =
                orderService.createOrder(request, "order-key-002");

        // Assert
        assertNotNull(response);
        assertEquals("ORD-NEW1234", response.orderNumber());

        verify(creationTransactionService, times(1))
                .createOrder(
                        request,
                        "order-key-002",
                        "test-request-hash"
                );
    }

    private Order createExistingOrder() {

        Order order = new Order();

        ReflectionTestUtils.setField(order, "id", 1L);

        order.setOrderNumber("ORD-TEST123");
        order.setAccountNumber("GB71290859");
        order.setTotalAmount(new BigDecimal("59.98"));
        order.setCurrency("GBP");

        return order;
    }
}
