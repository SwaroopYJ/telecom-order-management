
package com.telecom.oms.order.saga.service;

import com.telecom.oms.order.saga.entity.SagaOutbox;
import com.telecom.oms.order.saga.repository.SagaOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SagaOutboxStatusService {

    private static final int MAX_ATTEMPTS = 5;

    private final SagaOutboxRepository outboxRepository;

    public SagaOutboxStatusService(
            SagaOutboxRepository outboxRepository
    ) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void markPublished(SagaOutbox message) {

        message.setStatus("PUBLISHED");
        message.setPublishedAt(LocalDateTime.now());
        message.setProcessingStartedAt(null);
        message.setAttempts(message.getAttempts() + 1);
        message.setLastError(null);

        outboxRepository.save(message);
    }

    @Transactional
    public void markFailed(SagaOutbox message, String errorMessage) {

        int attempts = message.getAttempts() + 1;

        message.setAttempts(attempts);
        message.setProcessingStartedAt(null);
        message.setLastError(errorMessage);

        if (attempts >= MAX_ATTEMPTS) {
            message.setStatus("FAILED");
        } else {
            message.setStatus("PENDING");
        }

        outboxRepository.save(message);
    }
}
