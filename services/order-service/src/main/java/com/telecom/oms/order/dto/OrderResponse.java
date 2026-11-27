package com.telecom.oms.order.dto;

import com.telecom.oms.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String accountNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {

    public record OrderItemResponse(
            Long id,
            Long productId,
            String productSku,
            String productName,
            BigDecimal unitPrice,
            String currency,
            String billingFrequency,
            Integer quantity,
            BigDecimal lineTotal
    ) {
    }
}