package com.telecom.oms.product.dto;

import com.telecom.oms.product.enums.ProductStatus;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        String categoryCode,
        String categoryName,
        ProductStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}