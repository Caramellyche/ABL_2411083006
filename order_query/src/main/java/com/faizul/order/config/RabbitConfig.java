package com.faizul.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "orders.exchange";
    public static final String QUERY_SYNC_QUEUE = "orders.query.sync";
    public static final String ROUTING_KEY_CREATED = "orders.created";
    public static final String ROUTING_KEY_UPDATED = "orders.updated";
    public static final String ROUTING_KEY_DELETED = "orders.deleted";

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE, true, false);
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
    Binding querySyncCreatedBinding(Queue querySyncQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(querySyncQueue).to(orderExchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    Binding querySyncUpdatedBinding(Queue querySyncQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(querySyncQueue).to(orderExchange).with(ROUTING_KEY_UPDATED);
    }

    @Bean
    Binding querySyncDeletedBinding(Queue querySyncQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(querySyncQueue).to(orderExchange).with(ROUTING_KEY_DELETED);
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
