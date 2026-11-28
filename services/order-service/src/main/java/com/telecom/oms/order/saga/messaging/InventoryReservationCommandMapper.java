package com.telecom.oms.order.saga.messaging;

import com.telecom.oms.order.entity.Order;
import com.telecom.oms.order.entity.OrderItem;
import com.telecom.oms.order.saga.messaging.dto.InventoryReservationCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class InventoryReservationCommandMapper {

    public InventoryReservationCommand toCommand(
            Order order,
            String sagaId,
            String commandId
    ) {
        List<InventoryReservationCommand.InventoryItem> items =
                order.getItems()
                        .stream()
                        .map(this::toInventoryItem)
                        .toList();

        return new InventoryReservationCommand(
                commandId,
                sagaId,
                order.getId(),
                order.getOrderNumber(),
                order.getAccountNumber(),
                items,
                LocalDateTime.now()
        );
    }

    private InventoryReservationCommand.InventoryItem toInventoryItem(
            OrderItem item
    ) {
        return new InventoryReservationCommand.InventoryItem(
                item.getProductId(),
                item.getProductSku(),
                item.getQuantity()
        );
    }
}