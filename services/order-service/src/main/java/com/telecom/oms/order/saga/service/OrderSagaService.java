package com.telecom.oms.order.saga.service;

import com.telecom.oms.order.entity.Order;
import com.telecom.oms.order.exception.OrderNotFoundException;
import com.telecom.oms.order.repository.OrderRepository;
import com.telecom.oms.order.saga.entity.OrderSaga;
import com.telecom.oms.order.saga.entity.SagaStepExecution;
import com.telecom.oms.order.saga.enums.SagaState;
import com.telecom.oms.order.saga.enums.SagaStatus;
import com.telecom.oms.order.saga.enums.SagaStepStatus;
import com.telecom.oms.order.saga.messaging.InventoryReservationCommandMapper;
import com.telecom.oms.order.saga.messaging.dto.InventoryReservationCommand;
import com.telecom.oms.order.saga.repository.OrderSagaRepository;
import com.telecom.oms.order.saga.repository.SagaStepExecutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderSagaService {

    private static final String ORDER_FULFILMENT_SAGA = "ORDER_FULFILMENT";
    private static final String INVENTORY_RESERVATION_STEP = "INVENTORY_RESERVATION";

    private final OrderSagaRepository orderSagaRepository;
    private final SagaStepExecutionRepository sagaStepExecutionRepository;
    private final OrderRepository orderRepository;
    private final InventoryReservationCommandMapper commandMapper;
    private final SagaOutboxService sagaOutboxService;

    public OrderSagaService(
            OrderSagaRepository orderSagaRepository,
            SagaStepExecutionRepository sagaStepExecutionRepository,
            OrderRepository orderRepository,
            InventoryReservationCommandMapper commandMapper,
            SagaOutboxService sagaOutboxService
    ) {
        this.orderSagaRepository = orderSagaRepository;
        this.sagaStepExecutionRepository = sagaStepExecutionRepository;
        this.orderRepository = orderRepository;
        this.commandMapper = commandMapper;
        this.sagaOutboxService = sagaOutboxService;
    }

    @Transactional
    public OrderSaga startSaga(Long orderId) {

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with ID: " + orderId
                ));

        return orderSagaRepository.findByOrderId(orderId)
                .orElseGet(() -> createSaga(order));
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

    private OrderSaga createSaga(Order order) {

        OrderSaga saga = new OrderSaga();

        saga.setSagaId(UUID.randomUUID().toString());
        saga.setOrderId(order.getId());
        saga.setSagaType(ORDER_FULFILMENT_SAGA);
        saga.setCurrentState(SagaState.INVENTORY_RESERVATION_PENDING);
        saga.setStatus(SagaStatus.RUNNING);
        saga.setRetryCount(0);

        saga = orderSagaRepository.save(saga);

        String commandId = UUID.randomUUID().toString();

        SagaStepExecution step = new SagaStepExecution();
        step.setSaga(saga);
        step.setStepName(INVENTORY_RESERVATION_STEP);
        step.setStatus(SagaStepStatus.IN_PROGRESS);
        step.setCommandId(commandId);
        step.setAttemptNumber(1);
        step.setStartedAt(LocalDateTime.now());

        sagaStepExecutionRepository.save(step);

        InventoryReservationCommand command =
                commandMapper.toCommand(order, saga.getSagaId(), commandId);

        sagaOutboxService.saveInventoryReservationCommand(
                saga.getSagaId(),
                order.getId(),
                commandId,
                command
        );

        return saga;
    }
}