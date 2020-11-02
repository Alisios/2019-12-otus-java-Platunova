package ru.otus.configurations;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "bot")
@Component
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramProperties {

    private String name;
    private String tokien;
    private int reconnectPause;
}
