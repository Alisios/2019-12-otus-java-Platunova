package ru.otus.configurations;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;


@EnableRabbit
@Configuration
@ComponentScan("ru.otus")
@EnableAutoConfiguration
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Autowired
    RabbitMQConfig(@Qualifier("rabbitMQProperties") RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(rabbitMQProperties.getHost());
        connectionFactory.setUsername(rabbitMQProperties.getUsername());
        connectionFactory.setPassword(rabbitMQProperties.getPassword());
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        connectionFactory.setChannelCacheSize(rabbitMQProperties.getChannelCacheSizeMax());
        connectionFactory.setConnectionCacheSize(rabbitMQProperties.getConnectionCacheSizeMax());
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        rabbitAdmin.setAutoStartup(true);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        rabbitAdmin.setRetryTemplate(retryTemplate);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public DirectExchange frontProducerExchange() {
        return new DirectExchange(rabbitMQProperties.getFrontProducerExchange(), true, false);
    }

    @Bean
    public Queue frontProducerQueue() {
        return QueueBuilder
                .durable(rabbitMQProperties.getFrontProducerQueue())
                .ttl(rabbitMQProperties.getXMessageTtl())
                .deadLetterExchange(rabbitMQProperties.getXDeadLettersExchange())
                .deadLetterRoutingKey(rabbitMQProperties.getXDeadLetterQueue())
                .build();
    }

    @Bean
    public Binding frontProducerBinding() {
        return BindingBuilder.bind(frontProducerQueue())
                .to(frontProducerExchange())
                .with(rabbitMQProperties.getFrontProducerQueue());
    }
}
