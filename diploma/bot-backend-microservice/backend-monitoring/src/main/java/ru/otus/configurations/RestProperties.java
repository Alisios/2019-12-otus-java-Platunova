package ru.otus.configurations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rest")
@Component
@Setter
@Getter
@NoArgsConstructor
public class RestProperties {
    private String host;
    private String scheme;
    private String pathConcert;
    private String pathTicket;
}
