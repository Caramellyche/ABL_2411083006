package com.faizul.order.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitOrderEventPublisher implements OrderEventPublisher {
    public static final String EXCHANGE = "orders.exchange";
    public static final String ROUTING_KEY = "orders.created";
    public static final String ROUTING_KEY_CREATED = "orders.created";
    public static final String ROUTING_KEY_UPDATED = "orders.updated";
    public static final String ROUTING_KEY_DELETED = "orders.deleted";

    private final RabbitTemplate rabbitTemplate;

    public RabbitOrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_CREATED, event);
    }

    @Override
    public void publish(OrderUpdatedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_UPDATED, event);
    }

    @Override
    public void publish(OrderDeletedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_DELETED, event);
    }
}
