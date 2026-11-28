
package com.telecom.oms.order.repository;

import com.telecom.oms.order.entity.OrderIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderIdempotencyRepository
        extends JpaRepository<OrderIdempotency, String> {

    Optional<OrderIdempotency> findByIdempotencyKey(String idempotencyKey);
}
