package com.telecom.oms.order.dto;

public record CustomerResponse(
        Long id,
        String accountNumber,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String dateOfBirth,
        String status,
        String createdAt,
        String updatedAt
) {
}