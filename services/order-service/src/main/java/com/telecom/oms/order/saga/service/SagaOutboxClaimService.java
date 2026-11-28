package com.telecom.oms.order.saga.service;

import com.telecom.oms.order.saga.entity.SagaOutbox;
import com.telecom.oms.order.saga.repository.SagaOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class SagaOutboxClaimService {

    private final SagaOutboxRepository outboxRepository;

    public SagaOutboxClaimService(
            SagaOutboxRepository outboxRepository
    ) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public List<SagaOutbox> claimPendingMessages() {
        List<SagaOutbox> messages =
                outboxRepository.findAndLockPendingMessages("PENDING");

        for (SagaOutbox message : messages) {
            message.setStatus("PROCESSING");
            message.setProcessingStartedAt(LocalDateTime.now());
        }

        return messages;
    }
    @Transactional
    public int recoverStuckMessages() {

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);

        return outboxRepository.recoverStuckMessages(cutoffTime);
    }
}