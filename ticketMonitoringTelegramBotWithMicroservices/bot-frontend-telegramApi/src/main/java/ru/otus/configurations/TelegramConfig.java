package ru.otus.configurations;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.otus.telegramApi.Bot;
import ru.otus.telegramApi.TelegramService;

@Configuration
@ComponentScan("ru.otus")
@EnableAutoConfiguration
public class TelegramConfig {

    TelegramProperties telegramProperties;

    TelegramConfig(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
    }

    @Bean
    Bot ticketBot(TelegramService telegramService) {
        ApiContextInitializer.init();
        Bot ticket_bot = new Bot(telegramProperties.getName(), telegramProperties.getTokien(), telegramService, telegramProperties.getReconnectPause());
        telegramService.setBot(ticket_bot);
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");
        ticket_bot.botConnect();
        return ticket_bot;
    }
}
