
package com.telecom.oms.order.saga.controller;

import com.telecom.oms.order.saga.entity.OrderSaga;
import com.telecom.oms.order.saga.service.OrderSagaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/saga")
public class OrderSagaController {

    private final OrderSagaService orderSagaService;

    public OrderSagaController(OrderSagaService orderSagaService) {
        this.orderSagaService = orderSagaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderSaga startSaga(@PathVariable Long orderId) {
        return orderSagaService.startSaga(orderId);
    }

    @GetMapping
    public OrderSaga getSaga(@PathVariable Long orderId) {
        return orderSagaService.getSagaByOrderId(orderId);
    }
}
