
package com.telecom.oms.order.service;

import com.telecom.oms.order.client.CustomerServiceClient;
import com.telecom.oms.order.client.ProductServiceClient;
import com.telecom.oms.order.dto.*;
import com.telecom.oms.order.entity.Order;
import com.telecom.oms.order.enums.OrderStatus;
import com.telecom.oms.order.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateOrderRequest(
                "GB71290859",
                List.of(new CreateOrderRequest.OrderItemRequest(1L, 2))
        );
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {

        // Arrange
        CustomerResponse customer = new CustomerResponse(
                1L,
                "GB71290859",
                "Rahul",
                "Sharma",
                "rahul@example.com",
                "9876543210",
                "1995-05-10",
                "ACTIVE",
                "2026-11-27T10:00:00",
                "2026-11-27T10:00:00"
        );

        ProductResponse product = new ProductResponse(
                1L,
                "BB-1001",
                "Fibre Broadband 100 Mbps",
                "Unlimited fibre broadband",
                "BROADBAND",
                "Broadband",
                "ACTIVE",
                "2026-11-27T10:00:00",
                "2026-11-27T10:00:00"
        );

        ProductPriceResponse price = new ProductPriceResponse(
                1L,
                1L,
                "BB-1001",
                new BigDecimal("29.99"),
                "GBP",
                "MONTHLY",
                "2026-11-27T00:00:00",
                null,
                "2026-11-27T00:00:00"
        );

        when(customerServiceClient.getCustomer("GB71290859"))
                .thenReturn(customer);

        when(productServiceClient.getProduct(1L))
                .thenReturn(product);

        when(productServiceClient.getProductPrices(1L))
                .thenReturn(List.of(price));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.orderNumber());
        assertTrue(response.orderNumber().startsWith("ORD-"));
        assertEquals("GB71290859", response.accountNumber());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(0, new BigDecimal("59.98")
                .compareTo(response.totalAmount()));
        assertEquals("GBP", response.currency());
        assertEquals(1, response.items().size());
        assertEquals(2, response.items().get(0).quantity());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_shouldRejectInactiveCustomer() {

        // Arrange
        CustomerResponse customer = new CustomerResponse(
                3L,
                "GB16133324",
                "Audit",
                "Test",
                "audit@example.com",
                "9876543210",
                "1995-05-10",
                "SUSPENDED",
                "2026-11-27T10:00:00",
                "2026-11-27T10:00:00"
        );

        when(customerServiceClient.getCustomer("GB71290859"))
                .thenReturn(customer);

        // Act & Assert
        assertThrows(
                com.telecom.oms.order.exception.InvalidOrderException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(productServiceClient);
    }
    @Test
    void createOrder_shouldRejectInactiveProduct() {

        // Arrange
        CustomerResponse customer = new CustomerResponse(
                1L,
                "GB71290859",
                "Rahul",
                "Sharma",
                "rahul@example.com",
                "9876543210",
                "1995-05-10",
                "ACTIVE",
                "2026-11-27T10:00:00",
                "2026-11-27T10:00:00"
        );

        ProductResponse product = new ProductResponse(
                2L,
                "BB-1002",
                "Fibre Broadband 500 Mbps",
                "High-speed fibre broadband",
                "BROADBAND",
                "Broadband",
                "INACTIVE",
                "2026-11-27T10:00:00",
                "2026-11-27T10:00:00"
        );

        when(customerServiceClient.getCustomer("GB71290859"))
                .thenReturn(customer);

        when(productServiceClient.getProduct(1L))
                .thenReturn(product);

        // Act & Assert
        assertThrows(
                com.telecom.oms.order.exception.InvalidOrderException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
        verify(productServiceClient, never()).getProductPrices(anyLong());
    }

    @Test
    void createOrder_shouldRejectProductWithoutValidPrice() {

        CustomerResponse customer = new CustomerResponse(
                1L, "GB71290859", "Rahul", "Sharma",
                "rahul@example.com", "9876543210",
                "1995-05-10", "ACTIVE",
                "2026-11-27T10:00:00", "2026-11-27T10:00:00"
        );

        ProductResponse product = new ProductResponse(
                1L, "BB-1001", "Fibre Broadband 100 Mbps",
                "Unlimited fibre broadband", "BROADBAND",
                "Broadband", "ACTIVE",
                "2026-11-27T10:00:00", "2026-11-27T10:00:00"
        );

        ProductPriceResponse expiredPrice = new ProductPriceResponse(
                1L, 1L, "BB-1001",
                new BigDecimal("29.99"), "GBP", "MONTHLY",
                "2026-11-01T00:00:00",
                "2026-11-26T23:59:59",
                "2026-11-01T00:00:00"
        );

        when(customerServiceClient.getCustomer("GB71290859"))
                .thenReturn(customer);

        when(productServiceClient.getProduct(1L))
                .thenReturn(product);

        when(productServiceClient.getProductPrices(1L))
                .thenReturn(List.of(expiredPrice));

        assertThrows(
                com.telecom.oms.order.exception.InvalidOrderException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                com.telecom.oms.order.exception.OrderNotFoundException.class,
                () -> orderService.getOrderById(999L)
        );

        verify(orderRepository).findById(999L);
    }
}
