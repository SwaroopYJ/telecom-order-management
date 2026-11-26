package com.telecom.oms.customer.dto;

import com.telecom.oms.customer.entity.CustomerStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String accountNumber,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        CustomerStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}