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

    private String backProducerExchange;
    private String xDeadLettersExchange;
    private String xDeadLetterQueue;

    private String username;
    private String password;
    private String host;
    private String channelCacheSizeMax;
    private String connectionCacheSizeMax;

}
