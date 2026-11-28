
package com.telecom.oms.order.saga.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaRabbitMQConfig {

    public static final String SAGA_EXCHANGE = "oms.saga.exchange";

    public static final String INVENTORY_RESERVATION_QUEUE =
            "oms.inventory.reservation.queue";

    public static final String INVENTORY_RESERVATION_ROUTING_KEY =
            "inventory.reservation";

    public static final String INVENTORY_RESULT_QUEUE =
            "oms.inventory.result.queue";

    public static final String INVENTORY_RESULT_ROUTING_KEY =
            "inventory.result";

    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(SAGA_EXCHANGE, true, false);
    }

    @Bean
    public Queue inventoryReservationQueue() {
        return QueueBuilder.durable(INVENTORY_RESERVATION_QUEUE).build();
    }

    @Bean
    public Queue inventoryResultQueue() {
        return QueueBuilder.durable(INVENTORY_RESULT_QUEUE).build();
    }

    @Bean
    public Binding inventoryReservationBinding() {
        return BindingBuilder
                .bind(inventoryReservationQueue())
                .to(sagaExchange())
                .with(INVENTORY_RESERVATION_ROUTING_KEY);
    }

    @Bean
    public Binding inventoryResultBinding() {
        return BindingBuilder
                .bind(inventoryResultQueue())
                .to(sagaExchange())
                .with(INVENTORY_RESULT_ROUTING_KEY);
    }
}
