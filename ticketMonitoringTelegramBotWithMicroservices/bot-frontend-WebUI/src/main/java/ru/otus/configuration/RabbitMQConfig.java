package ru.otus.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
@Slf4j
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

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
    public DirectExchange backProducerToWebExchange() {
        return new DirectExchange(rabbitMQProperties.getBackProducerWebExchange(), true, false);
    }

    @Bean
    public Queue backProducerToWebQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getBackProducerToWebQueue())
                .withArgument("x-dead-letter-exchange", rabbitMQProperties.getXDeadLettersExchange())
                .withArgument("x-dead-letter-routing-key", rabbitMQProperties.getXDeadLetterQueue())
                .withArgument("x-message-ttl", rabbitMQProperties.getXMessageTtl())
                .build();
    }

    @Bean
    public Binding backProducerToWebBinding() {
        return BindingBuilder.bind(backProducerToWebQueue()).to(backProducerToWebExchange()).with(rabbitMQProperties.getBackProducerToWebQueue());
    }


}
