package com.telecom.oms.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(

        @NotBlank(message = "Account number is required")
        String accountNumber,

        @NotEmpty(message = "At least one order item is required")
        List<@Valid OrderItemRequest> items

) {

    public record OrderItemRequest(

            @NotNull(message = "Product ID is required")
            @Positive(message = "Product ID must be positive")
            Long productId,

            @NotNull(message = "Quantity is required")
            @Positive(message = "Quantity must be greater than zero")
            Integer quantity

    ) {
    }
}