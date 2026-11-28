
package com.telecom.oms.order.saga.messaging.dto;

import java.time.LocalDateTime;
import java.util.List;

public record InventoryReservationCommand(

        String commandId,

        String sagaId,

        Long orderId,

        String orderNumber,

        String accountNumber,

        List<InventoryItem> items,

        LocalDateTime requestedAt

) {

    public record InventoryItem(
            Long productId,
            String productSku,
            Integer quantity
    ) {
    }
}
