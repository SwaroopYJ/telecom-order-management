package com.telecom.oms.order.dto;

import java.math.BigDecimal;

public record ProductPriceResponse(
        Long id,
        Long productId,
        String productSku,
        BigDecimal amount,
        String currency,
        String billingFrequency,
        String validFrom,
        String validTo,
        String createdAt
) {
}