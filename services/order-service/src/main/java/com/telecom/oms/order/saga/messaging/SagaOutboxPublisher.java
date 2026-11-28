package com.telecom.oms.order.saga.messaging;

import com.telecom.oms.order.saga.config.SagaRabbitMQConfig;
import com.telecom.oms.order.saga.entity.SagaOutbox;
import com.telecom.oms.order.saga.service.SagaOutboxClaimService;
import com.telecom.oms.order.saga.service.SagaOutboxStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SagaOutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(SagaOutboxPublisher.class);

    private final SagaOutboxClaimService claimService;
    private final SagaOutboxStatusService statusService;
    private final RabbitTemplate rabbitTemplate;

    public SagaOutboxPublisher(
            SagaOutboxClaimService claimService,
            SagaOutboxStatusService statusService,
            RabbitTemplate rabbitTemplate
    ) {
        this.claimService = claimService;
        this.statusService = statusService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPendingMessages() {

        List<SagaOutbox> messages =
                claimService.claimPendingMessages();

        for (SagaOutbox message : messages) {
            publish(message);
        }
    }

    private void publish(SagaOutbox message) {

        try {
            CorrelationData correlationData =
                    new CorrelationData(message.getEventId());

            rabbitTemplate.convertAndSend(
                    SagaRabbitMQConfig.SAGA_EXCHANGE,
                    message.getRoutingKey(),
                    message.getPayload(),
                    correlationData
            );

            CorrelationData.Confirm confirm =
                    correlationData.getFuture().get(10, TimeUnit.SECONDS);

            if (!confirm.isAck()) {
                throw new IllegalStateException(
                        "RabbitMQ rejected message: " + confirm.getReason()
                );
            }

            ReturnedMessage returned = correlationData.getReturned();

            if (returned != null) {
                throw new IllegalStateException(
                        "Message was not routed to a queue: "
                                + returned.getReplyText()
                );
            }

            statusService.markPublished(message);

            log.info(
                    "Published outbox event. EventId={}, EventType={}",
                    message.getEventId(),
                    message.getEventType()
            );

        } catch (Exception exception) {

            statusService.markFailed(message, exception.getMessage());

            log.error(
                    "Failed to publish outbox event. EventId={}",
                    message.getEventId(),
                    exception
            );
        }
    }
}