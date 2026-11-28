
package com.telecom.oms.order.saga.service;

import com.telecom.oms.order.saga.entity.SagaOutbox;
import com.telecom.oms.order.saga.repository.SagaOutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SagaOutboxStatusServiceTest {

    @Mock
    private SagaOutboxRepository outboxRepository;

    @InjectMocks
    private SagaOutboxStatusService statusService;

    @Test
    void shouldMarkMessageFailedAfterFiveAttempts() {

        SagaOutbox message = new SagaOutbox();
        message.setAttempts(0);

        for (int attempt = 1; attempt <= 5; attempt++) {

            statusService.markFailed(message, "RabbitMQ unavailable");

            assertEquals(attempt, message.getAttempts());

            if (attempt < 5) {
                assertEquals("PENDING", message.getStatus());
            } else {
                assertEquals("FAILED", message.getStatus());
            }
        }
    }

    @Test
    void shouldMarkMessagePublishedAfterSuccessfulAttempt() {

        SagaOutbox message = new SagaOutbox();
        message.setAttempts(2);

        statusService.markPublished(message);

        assertEquals("PUBLISHED", message.getStatus());
        assertEquals(3, message.getAttempts());
        assertNull(message.getLastError());
    }
}
