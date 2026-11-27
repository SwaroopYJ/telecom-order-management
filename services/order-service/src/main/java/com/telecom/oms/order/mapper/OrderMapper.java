package com.telecom.oms.order.mapper;

import com.telecom.oms.order.dto.OrderResponse;
import com.telecom.oms.order.entity.Order;
import com.telecom.oms.order.entity.OrderItem;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {

        List<OrderResponse.OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(OrderMapper::toItemResponse)
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getAccountNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCreatedAt(),
                items
        );
    }

    private static OrderResponse.OrderItemResponse toItemResponse(
            OrderItem item) {

        return new OrderResponse.OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductSku(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getCurrency(),
                item.getBillingFrequency(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }
}