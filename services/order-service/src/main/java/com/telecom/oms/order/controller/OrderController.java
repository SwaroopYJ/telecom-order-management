
package com.telecom.oms.order.controller;

import com.telecom.oms.order.dto.CreateOrderRequest;
import com.telecom.oms.order.dto.OrderResponse;
import com.telecom.oms.order.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(
            @PathVariable Long orderId) {

        return orderService.getOrderById(orderId);
    }
}
