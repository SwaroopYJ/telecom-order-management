package com.telecom.oms.order.saga.messaging.dto;

import java.time.LocalDateTime;

public record InventoryReservationResult(
        String commandId,
        String sagaId,
        Long orderId,
        boolean successful,
        String reservationId,
        String failureReason,
        LocalDateTime processedAt
) {
}