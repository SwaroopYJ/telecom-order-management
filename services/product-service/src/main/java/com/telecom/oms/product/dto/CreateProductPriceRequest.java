package com.telecom.oms.product.dto;

import com.telecom.oms.product.enums.BillingFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateProductPriceRequest(

        @NotNull(message = "Price amount is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a 3-letter uppercase code")
        String currency,

        @NotNull(message = "Billing frequency is required")
        BillingFrequency billingFrequency,

        @NotNull(message = "Valid from date is required")
        LocalDateTime validFrom,

        LocalDateTime validTo
) {
}