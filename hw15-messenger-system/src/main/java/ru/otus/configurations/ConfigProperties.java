package ru.otus.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "service")
@Component
public class ConfigProperties {

    private String frontendServiceClientName ;

    private  String backendServiceClientName;


    public String getFrontendServiceClientName() {
        return frontendServiceClientName;
    }

    public String getBackendServiceClientName() {
        return backendServiceClientName;
    }

    public void setFrontendServiceClientName(String frontendServiceClientName) {
        this.frontendServiceClientName = frontendServiceClientName;
    }

    public void setBackendServiceClientName(String backendServiceClientName) {
        this.backendServiceClientName = backendServiceClientName;
    }
}
