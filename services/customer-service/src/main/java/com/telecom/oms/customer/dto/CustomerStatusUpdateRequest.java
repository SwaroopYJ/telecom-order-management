package com.telecom.oms.customer.dto;

import com.telecom.oms.customer.entity.CustomerStatus;
import jakarta.validation.constraints.NotNull;

public record CustomerStatusUpdateRequest(

        @NotNull(message = "Customer status is required")
        CustomerStatus status

) {
}