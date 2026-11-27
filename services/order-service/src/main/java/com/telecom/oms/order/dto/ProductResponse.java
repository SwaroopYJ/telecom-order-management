package com.telecom.oms.order.dto;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        String categoryCode,
        String categoryName,
        String status,
        String createdAt,
        String updatedAt
) {
}