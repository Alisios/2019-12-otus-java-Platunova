package ru.otus.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;


@EnableRabbit
@Configuration
@Slf4j
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitProperties;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(rabbitProperties.getHost());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        connectionFactory.setChannelCacheSize(Integer.parseInt(rabbitProperties.getChannelCacheSizeMax()));
        connectionFactory.setConnectionCacheSize(Integer.parseInt(rabbitProperties.getConnectionCacheSizeMax()));
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
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(rabbitProperties.getXDeadLettersExchange(), true, false);
    }

    @Bean
    Queue deadLetterQueue() {
        return new Queue(rabbitProperties.getXDeadLetterQueue(), true, false, false, null);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

}
