package com.faizul.order.event;

public interface OrderEventPublisher {
    void publish(OrderCreatedEvent event);
    void publish(OrderUpdatedEvent event);
    void publish(OrderDeletedEvent event);
}
