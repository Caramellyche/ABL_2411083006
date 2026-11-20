package com.faizul.order.event;

import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class RabbitOrderEventPublisherTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitOrderEventPublisher publisher;

    @Test
    void publishCreatedEventSendsToOrdersExchangeWithCreatedRoutingKey() {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 10L, 20L, LocalDate.now(), "Budi", "budi@example.com", "Kopi", 2, new BigDecimal("20000"));
        publisher.publish(event);
        verify(rabbitTemplate).convertAndSend("orders.exchange", "orders.created", event);
    }

    @Test
    void publishUpdatedEventSendsToOrdersExchangeWithUpdatedRoutingKey() {
        OrderUpdatedEvent event = new OrderUpdatedEvent(1L, 10L, 20L, LocalDate.now(), "Budi", "budi@example.com", "Kopi", 3, new BigDecimal("30000"));
        publisher.publish(event);
        verify(rabbitTemplate).convertAndSend("orders.exchange", "orders.updated", event);
    }

    @Test
    void publishDeletedEventSendsToOrdersExchangeWithDeletedRoutingKey() {
        OrderDeletedEvent event = new OrderDeletedEvent(1L);
        publisher.publish(event);
        verify(rabbitTemplate).convertAndSend("orders.exchange", "orders.deleted", event);
    }
}
