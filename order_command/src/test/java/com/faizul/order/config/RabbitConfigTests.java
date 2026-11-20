package com.faizul.order.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertSame;

class RabbitConfigTests {

    @Test
    void rabbitListenerContainerFactoryConfiguresMessageConverter() {
        RabbitConfig config = new RabbitConfig();
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);

        SimpleRabbitListenerContainerFactory factory = config.rabbitListenerContainerFactory(connectionFactory, messageConverter);

        Object configuredConverter = ReflectionTestUtils.getField(factory, "messageConverter");
        assertSame(messageConverter, configuredConverter);
    }
}
