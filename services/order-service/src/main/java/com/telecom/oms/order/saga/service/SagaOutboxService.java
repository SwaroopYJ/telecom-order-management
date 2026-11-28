package com.telecom.oms.order.saga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.oms.order.saga.entity.SagaOutbox;
import com.telecom.oms.order.saga.repository.SagaOutboxRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SagaOutboxService {

    private final SagaOutboxRepository sagaOutboxRepository;
    private final ObjectMapper objectMapper;

    public SagaOutboxService(
            SagaOutboxRepository sagaOutboxRepository,
            ObjectMapper objectMapper
    ) {
        this.sagaOutboxRepository = sagaOutboxRepository;
        this.objectMapper = objectMapper;
    }

    public void saveInventoryReservationCommand(
            String sagaId,
            Long orderId,
            String commandId,
            Object command
    ) {
        try {
            SagaOutbox outbox = new SagaOutbox();

            outbox.setEventId(UUID.randomUUID().toString());
            outbox.setAggregateType("ORDER_SAGA");
            outbox.setAggregateId(sagaId);
            outbox.setEventType("INVENTORY_RESERVATION_REQUESTED");
            outbox.setRoutingKey("inventory.reservation");
            outbox.setPayload(objectMapper.writeValueAsString(command));
            outbox.setStatus("PENDING");
            outbox.setAttempts(0);

            sagaOutboxRepository.save(outbox);

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Failed to serialize inventory reservation command for order "
                            + orderId + " with command ID " + commandId,
                    exception
            );
        }
    }
}