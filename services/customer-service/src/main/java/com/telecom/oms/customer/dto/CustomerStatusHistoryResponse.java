package com.telecom.oms.customer.dto;

import com.telecom.oms.customer.entity.CustomerStatus;

import java.time.LocalDateTime;

public record CustomerStatusHistoryResponse(
        Long id,
        CustomerStatus previousStatus,
        CustomerStatus newStatus,
        String changedBy,
        LocalDateTime changedAt
) {
}
