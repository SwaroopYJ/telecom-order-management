
package com.telecom.oms.order.saga.service;

import com.telecom.oms.order.entity.Order;
import com.telecom.oms.order.exception.OrderNotFoundException;
import com.telecom.oms.order.repository.OrderRepository;
import com.telecom.oms.order.saga.entity.OrderSaga;
import com.telecom.oms.order.saga.enums.SagaState;
import com.telecom.oms.order.saga.enums.SagaStatus;
import com.telecom.oms.order.saga.messaging.InventoryReservationCommandMapper;
import com.telecom.oms.order.saga.repository.OrderSagaRepository;
import com.telecom.oms.order.saga.repository.SagaStepExecutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSagaServiceTest {

    @Mock
    private OrderSagaRepository orderSagaRepository;

    @Mock
    private SagaStepExecutionRepository sagaStepExecutionRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryReservationCommandMapper commandMapper;

    @Mock
    private SagaOutboxService sagaOutboxService;

    @InjectMocks
    private OrderSagaService orderSagaService;

    @Test
    void shouldCreateSagaForExistingOrder() {

        Long orderId = 1L;

        Order order = mock(Order.class);
        when(order.getId()).thenReturn(orderId);

        when(orderRepository.findWithItemsById(orderId))
                .thenReturn(Optional.of(order));

        when(orderSagaRepository.findByOrderId(orderId))
                .thenReturn(Optional.empty());

        when(orderSagaRepository.save(any(OrderSaga.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderSaga saga = orderSagaService.startSaga(orderId);

        assertNotNull(saga.getSagaId());
        assertEquals(orderId, saga.getOrderId());
        assertEquals("ORDER_FULFILMENT", saga.getSagaType());
        assertEquals(
                SagaState.INVENTORY_RESERVATION_PENDING,
                saga.getCurrentState()
        );
        assertEquals(SagaStatus.RUNNING, saga.getStatus());
        assertEquals(0, saga.getRetryCount());

        verify(orderSagaRepository).save(any(OrderSaga.class));
        verify(sagaStepExecutionRepository).save(any());
        verify(sagaOutboxService).saveInventoryReservationCommand(
                eq(saga.getSagaId()),
                eq(orderId),
                anyString(),
                any()
        );
    }

    @Test
    void shouldReturnExistingSagaForOrder() {

        Long orderId = 1L;

        Order order = mock(Order.class);


        OrderSaga existingSaga = new OrderSaga();
        existingSaga.setSagaId("existing-saga-id");
        existingSaga.setOrderId(orderId);

        when(orderRepository.findWithItemsById(orderId))
                .thenReturn(Optional.of(order));

        when(orderSagaRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(existingSaga));

        OrderSaga result = orderSagaService.startSaga(orderId);

        assertEquals("existing-saga-id", result.getSagaId());

        verify(orderSagaRepository, never()).save(any(OrderSaga.class));
        verifyNoInteractions(sagaOutboxService);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        Long orderId = 999L;

        when(orderRepository.findWithItemsById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderSagaService.startSaga(orderId)
        );

        verifyNoInteractions(orderSagaRepository);
        verifyNoInteractions(sagaOutboxService);
    }

    @Test
    void shouldRetrieveSagaForExistingOrder() {

        Long orderId = 1L;

        OrderSaga existingSaga = new OrderSaga();
        existingSaga.setSagaId("existing-saga-id");
        existingSaga.setOrderId(orderId);

        when(orderRepository.existsById(orderId)).thenReturn(true);

        when(orderSagaRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(existingSaga));

        OrderSaga result = orderSagaService.getSagaByOrderId(orderId);

        assertNotNull(result);
        assertEquals("existing-saga-id", result.getSagaId());
        assertEquals(orderId, result.getOrderId());
    }

    @Test
    void shouldThrowExceptionWhenRetrievingSagaForNonexistentOrder() {

        Long orderId = 999L;

        when(orderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(
                OrderNotFoundException.class,
                () -> orderSagaService.getSagaByOrderId(orderId)
        );
    }

    @Test
    void shouldThrowExceptionWhenSagaDoesNotExistForOrder() {

        Long orderId = 1L;

        when(orderRepository.existsById(orderId)).thenReturn(true);

        when(orderSagaRepository.findByOrderId(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderSagaService.getSagaByOrderId(orderId)
        );
    }
}
