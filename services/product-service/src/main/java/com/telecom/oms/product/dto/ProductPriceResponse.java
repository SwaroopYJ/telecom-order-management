package com.telecom.oms.product.dto;

import com.telecom.oms.product.enums.BillingFrequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductPriceResponse(
        Long id,
        Long productId,
        String productSku,
        BigDecimal amount,
        String currency,
        BillingFrequency billingFrequency,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        LocalDateTime createdAt
) {
}