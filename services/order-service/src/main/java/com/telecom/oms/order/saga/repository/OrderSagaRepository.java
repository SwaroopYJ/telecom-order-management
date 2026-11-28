
package com.telecom.oms.order.saga.repository;

import com.telecom.oms.order.saga.entity.OrderSaga;
import com.telecom.oms.order.saga.enums.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderSagaRepository extends JpaRepository<OrderSaga, Long> {

    Optional<OrderSaga> findBySagaId(String sagaId);

    Optional<OrderSaga> findByOrderId(Long orderId);

    List<OrderSaga> findByStatusAndNextRetryAtLessThanEqual(
            SagaStatus status,
            LocalDateTime currentTime
    );
}
