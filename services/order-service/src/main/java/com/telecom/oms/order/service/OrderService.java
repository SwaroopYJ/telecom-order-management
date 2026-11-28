
package com.telecom.oms.order.service;

import com.telecom.oms.order.dto.CreateOrderRequest;
import com.telecom.oms.order.dto.OrderResponse;
import com.telecom.oms.order.entity.OrderIdempotency;
import com.telecom.oms.order.exception.IdempotencyConflictException;
import com.telecom.oms.order.exception.OrderNotFoundException;
import com.telecom.oms.order.mapper.OrderMapper;
import com.telecom.oms.order.repository.OrderIdempotencyRepository;
import com.telecom.oms.order.repository.OrderRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderIdempotencyRepository idempotencyRepository;
    private final OrderRequestHasher requestHasher;
    private final OrderCreationTransactionService creationTransactionService;

    public OrderService(
            OrderRepository orderRepository,
            OrderIdempotencyRepository idempotencyRepository,
            OrderRequestHasher requestHasher,
            OrderCreationTransactionService creationTransactionService) {

        this.orderRepository = orderRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.requestHasher = requestHasher;
        this.creationTransactionService = creationTransactionService;
    }

    public OrderResponse createOrder(
            CreateOrderRequest request,
            String idempotencyKey) {

        String requestHash = requestHasher.hash(request);

        // Check whether this key has already been used.
        var existing = idempotencyRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            return handleExistingRequest(existing.get(), requestHash);
        }

        try {
            return creationTransactionService.createOrder(
                    request,
                    idempotencyKey,
                    requestHash
            );

        } catch (DataIntegrityViolationException exception) {

            // Another concurrent request may have inserted this key first.
            var concurrentRequest = idempotencyRepository
                    .findByIdempotencyKey(idempotencyKey);

            if (concurrentRequest.isPresent()) {
                return handleExistingRequest(
                        concurrentRequest.get(),
                        requestHash
                );
            }

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with ID: " + orderId));

        return OrderMapper.toResponse(order);
    }

    private OrderResponse handleExistingRequest(
            OrderIdempotency existing,
            String requestHash) {

        if (!existing.getRequestHash().equals(requestHash)) {
            throw new IdempotencyConflictException(
                    "Idempotency key has already been used with a different request");
        }

        if (existing.getOrderId() == null) {
            throw new IllegalStateException(
                    "Idempotency record exists without a completed order");
        }

        var order = orderRepository.findWithItemsById(existing.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order linked to idempotency key was not found"));

        return OrderMapper.toResponse(order);
    }
}
