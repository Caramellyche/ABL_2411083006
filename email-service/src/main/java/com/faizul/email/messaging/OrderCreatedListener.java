package com.faizul.email.messaging;

import com.faizul.order.event.OrderCreatedEvent;
import com.faizul.email.service.OrderConfirmationEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final OrderConfirmationEmailService emailService;

    public OrderCreatedListener(OrderConfirmationEmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "orders.confirmation")
    public void onOrderCreated(OrderCreatedEvent event) {
        try {
            emailService.send(event);
            log.info("Confirmation email sent for order {}", event.orderId());
        } catch (RuntimeException exception) {
            log.error("Confirmation email failed for order {}", event == null ? null : event.orderId(), exception);
            throw exception;
        }
    }
}
