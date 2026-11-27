
package com.telecom.oms.order.service;

import com.telecom.oms.order.client.CustomerServiceClient;
import com.telecom.oms.order.client.ProductServiceClient;
import com.telecom.oms.order.dto.*;
import com.telecom.oms.order.entity.Order;
import com.telecom.oms.order.entity.OrderItem;
import com.telecom.oms.order.enums.OrderStatus;
import com.telecom.oms.order.exception.InvalidOrderException;
import com.telecom.oms.order.exception.OrderNotFoundException;
import com.telecom.oms.order.mapper.OrderMapper;
import com.telecom.oms.order.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerServiceClient customerServiceClient;
    private final ProductServiceClient productServiceClient;

    public OrderService(
            OrderRepository orderRepository,
            CustomerServiceClient customerServiceClient,
            ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.customerServiceClient = customerServiceClient;
        this.productServiceClient = productServiceClient;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        // 1. Validate customer
        CustomerResponse customer =
                customerServiceClient.getCustomer(request.accountNumber());

        if (!"ACTIVE".equals(customer.status())) {
            throw new InvalidOrderException(
                    "Customer is not active: " + request.accountNumber());
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setAccountNumber(customer.accountNumber());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        String orderCurrency = null;

        // 2. Validate products and calculate order total
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.items()) {

            ProductResponse product =
                    productServiceClient.getProduct(itemRequest.productId());

            if (!"ACTIVE".equals(product.status())) {
                throw new InvalidOrderException(
                        "Product is not active: " + product.sku());
            }

            ProductPriceResponse price =
                    getCurrentPrice(itemRequest.productId());

            // All items in one order must use the same currency
            if (orderCurrency == null) {
                orderCurrency = price.currency();
            } else if (!orderCurrency.equals(price.currency())) {
                throw new InvalidOrderException(
                        "All order items must use the same currency");
            }

            BigDecimal lineTotal = price.amount()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.id());
            orderItem.setProductSku(product.sku());
            orderItem.setProductName(product.name());
            orderItem.setUnitPrice(price.amount());
            orderItem.setCurrency(price.currency());
            orderItem.setBillingFrequency(price.billingFrequency());
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setLineTotal(lineTotal);

            order.addItem(orderItem);

            totalAmount = totalAmount.add(lineTotal);
        }

        // 3. Set calculated order details
        order.setCurrency(orderCurrency);
        order.setTotalAmount(totalAmount);

        // 4. Persist order and its items
        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with ID: " + orderId));

        return OrderMapper.toResponse(order);
    }

    private ProductPriceResponse getCurrentPrice(Long productId) {

        LocalDateTime now = LocalDateTime.now();

        return productServiceClient.getProductPrices(productId)
                .stream()
                .filter(price -> {
                    LocalDateTime validFrom =
                            LocalDateTime.parse(price.validFrom());

                    LocalDateTime validTo = price.validTo() == null
                            ? null
                            : LocalDateTime.parse(price.validTo());

                    return !validFrom.isAfter(now)
                            && (validTo == null || !validTo.isBefore(now));
                })
                .findFirst()
                .orElseThrow(() -> new InvalidOrderException(
                        "No currently valid price found for product ID: "
                                + productId));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
