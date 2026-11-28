package com.telecom.oms.order.saga.scheduler;

import com.telecom.oms.order.saga.service.SagaOutboxClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SagaOutboxRecoveryScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(SagaOutboxRecoveryScheduler.class);

    private final SagaOutboxClaimService claimService;

    public SagaOutboxRecoveryScheduler(
            SagaOutboxClaimService claimService
    ) {
        this.claimService = claimService;
    }

    @Scheduled(fixedDelay = 60000)
    public void recoverStuckMessages() {

        int recoveredCount = claimService.recoverStuckMessages();

        if (recoveredCount > 0) {
            log.warn(
                    "Recovered {} stuck outbox messages",
                    recoveredCount
            );
        }
    }
}