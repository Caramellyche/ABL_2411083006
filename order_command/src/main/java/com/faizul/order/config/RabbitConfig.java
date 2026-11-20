package com.faizul.order.config;

import com.faizul.order.event.RabbitOrderEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitConfig {
    public static final String QUEUE = "orders.confirmation";
    public static final String DEAD_LETTER_QUEUE = "orders.confirmation.dlq";
    public static final String DEAD_LETTER_EXCHANGE = "orders.dlx";
    public static final String DEAD_LETTER_ROUTING_KEY = "orders.confirmation.failed";

    public static final String QUERY_SYNC_QUEUE = "orders.query.sync";

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(RabbitOrderEventPublisher.EXCHANGE, true, false);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Queue orderQueue() {
        return QueueBuilder.durable(QUEUE).deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY).build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Queue querySyncQueue() {
        return QueueBuilder.durable(QUERY_SYNC_QUEUE).build();
    }

    @Bean
    MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter("*");
    }

    @Bean
    Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(RabbitOrderEventPublisher.ROUTING_KEY);
    }

    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    Binding querySyncCreatedBinding(Queue querySyncQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(querySyncQueue).to(orderExchange).with(RabbitOrderEventPublisher.ROUTING_KEY_CREATED);
    }

    @Bean
    Binding querySyncUpdatedBinding(Queue querySyncQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(querySyncQueue).to(orderExchange).with(RabbitOrderEventPublisher.ROUTING_KEY_UPDATED);
    }

    @Bean
    Binding querySyncDeletedBinding(Queue querySyncQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(querySyncQueue).to(orderExchange).with(RabbitOrderEventPublisher.ROUTING_KEY_DELETED);
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                        MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxRetries(2)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());
        return factory;
    }
}
