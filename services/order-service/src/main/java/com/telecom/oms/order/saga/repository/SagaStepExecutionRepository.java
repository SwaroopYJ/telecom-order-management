
package com.telecom.oms.order.saga.repository;

import com.telecom.oms.order.saga.entity.SagaStepExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaStepExecutionRepository
        extends JpaRepository<SagaStepExecution, Long> {

    List<SagaStepExecution> findBySaga_IdOrderByCreatedAtAsc(Long sagaId);
}
