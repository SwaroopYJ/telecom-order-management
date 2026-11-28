
package com.telecom.oms.order.saga.service;

import com.telecom.oms.order.exception.OrderNotFoundException;
import com.telecom.oms.order.repository.OrderRepository;
import com.telecom.oms.order.saga.entity.OrderSaga;
import com.telecom.oms.order.saga.enums.SagaState;
import com.telecom.oms.order.saga.enums.SagaStatus;
import com.telecom.oms.order.saga.repository.OrderSagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderSagaService {

    private static final String ORDER_FULFILMENT_SAGA = "ORDER_FULFILMENT";

    private final OrderSagaRepository orderSagaRepository;
    private final OrderRepository orderRepository;

    public OrderSagaService(
            OrderSagaRepository orderSagaRepository,
            OrderRepository orderRepository
    ) {
        this.orderSagaRepository = orderSagaRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderSaga startSaga(Long orderId) {

        if (orderId == null || !orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(
                    "Order not found with ID: " + orderId
            );
        }

        return orderSagaRepository.findByOrderId(orderId)
                .orElseGet(() -> createSaga(orderId));
    }

    @Transactional(readOnly = true)
    public OrderSaga getSagaByOrderId(Long orderId) {

        if (orderId == null || !orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(
                    "Order not found with ID: " + orderId
            );
        }

        return orderSagaRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Saga not found for order ID: " + orderId
                ));
    }

    private OrderSaga createSaga(Long orderId) {

        OrderSaga saga = new OrderSaga();

        saga.setSagaId(UUID.randomUUID().toString());
        saga.setOrderId(orderId);
        saga.setSagaType(ORDER_FULFILMENT_SAGA);
        saga.setCurrentState(SagaState.STARTED);
        saga.setStatus(SagaStatus.RUNNING);
        saga.setRetryCount(0);

        return orderSagaRepository.save(saga);
    }
}
