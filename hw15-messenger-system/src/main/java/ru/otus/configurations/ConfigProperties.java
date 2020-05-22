package ru.otus.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "service")
public class ConfigProperties {
    private final String frontendServiceClientName = "frontendServiceClientName";
    private final String backendServiceClientName = "backendServiceClientName";
}
