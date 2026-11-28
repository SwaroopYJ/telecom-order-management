package com.telecom.oms.order.saga.repository;

import com.telecom.oms.order.saga.entity.SagaOutbox;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SagaOutboxRepository
        extends JpaRepository<SagaOutbox, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
            SELECT *
            FROM saga_outbox
            WHERE status = :status
            ORDER BY created_at ASC
            LIMIT 50
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<SagaOutbox> findAndLockPendingMessages(
            @Param("status") String status
    );

    @Modifying
    @Query("""
            UPDATE SagaOutbox s
            SET s.status = 'PENDING',
                s.processingStartedAt = null,
                s.lastError = 'Recovered after processing timeout'
            WHERE s.status = 'PROCESSING'
              AND s.processingStartedAt < :cutoffTime
            """)
    int recoverStuckMessages(
            @Param("cutoffTime") LocalDateTime cutoffTime
    );
}