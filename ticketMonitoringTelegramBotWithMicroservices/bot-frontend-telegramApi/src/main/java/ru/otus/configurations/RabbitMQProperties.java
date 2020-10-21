package ru.otus.configurations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rabbitmq-properties")
@Component("rabbitMQProperties")
@Setter
@Getter
@NoArgsConstructor
public class RabbitMQProperties {

    private String dbExchange;
    private String backProducerExchange;
    private String monitoringExchange;
    private String xDeadLettersExchange;
    private String frontProducerExchange;

    private String dbQueue;
    private String monitoringQueue;
    private String backProducerToWebQueue;
    private String frontProducerQueue;
    private String backProduceQueue;

    private String username;
    private String password;
    private String host;
    private int channelCacheSizeMax;
    private int connectionCacheSizeMax;

    private int xMessageTtl;
    private String xDeadLetterQueue;

}
