package org.example.rag_system_backend.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${mindvault.messaging.exchange}")
    private String exchangeName;

    @Value("${mindvault.messaging.queue}")
    private String queueName;

    @Value("${mindvault.messaging.routing-key}")
    private String routingKey;

    @Value("${mindvault.messaging.dlx}")
    private String dlxName;

    @Value("${mindvault.messaging.dlq}")
    private String dlqName;

    @Bean
    public TopicExchange fileEventsExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxName, true, false);
    }

    @Bean
    public Queue fileUploadedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", dlxName);
        args.put("x-dead-letter-routing-key", dlqName);
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public Queue fileUploadedDLQ() {
        return new Queue(dlqName, true);
    }

    @Bean
    public Binding fileUploadedBinding(Queue fileUploadedQueue, TopicExchange fileEventsExchange) {
        return BindingBuilder.bind(fileUploadedQueue).to(fileEventsExchange).with(routingKey);
    }

    @Bean
    public Binding dlqBinding(Queue fileUploadedDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(fileUploadedDLQ).to(deadLetterExchange).with(dlqName);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);

        template.setReturnsCallback(returned -> {
            System.err.println("[AMQP RETURN] " + returned);
        });
        template.setConfirmCallback((correlation, ack, cause) -> {
            if (!ack) {
                System.err.println("[AMQP NACK] corrId=" + (correlation != null ? correlation.getId() : "null")
                        + " cause=" + cause);
            }
        });
        return template;
    }
}
