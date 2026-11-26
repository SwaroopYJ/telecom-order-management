package com.telecom.oms.customer.repository;

import com.telecom.oms.customer.entity.CustomerStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerStatusHistoryRepository
        extends JpaRepository<CustomerStatusHistory, Long> {

    List<CustomerStatusHistory> findByCustomerAccountNumberOrderByChangedAtDesc(
            String accountNumber
    );

    List<CustomerStatusHistory> findByCustomerAccountNumberOrderByChangedAtAsc(
            String accountNumber
    );
}
