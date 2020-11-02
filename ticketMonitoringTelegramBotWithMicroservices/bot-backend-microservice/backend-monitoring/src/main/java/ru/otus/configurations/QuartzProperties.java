package ru.otus.configurations;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "quartz")
@Component
@Setter
@Getter
@NoArgsConstructor
public class QuartzProperties {

    private String instanceName;
    private int threadCount;
    private String triggerName;
    private String group;
    private String jobName;
}
