package com.telecom.oms.customer.repository;

import com.telecom.oms.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Customer> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByAccountNumberAndDeletedAtIsNull(String accountNumber);

    Optional<Customer> findByAccountNumberAndDeletedAtIsNull(String accountNumber);

    Page<Customer> findAllByDeletedAtIsNull(Pageable pageable);

}