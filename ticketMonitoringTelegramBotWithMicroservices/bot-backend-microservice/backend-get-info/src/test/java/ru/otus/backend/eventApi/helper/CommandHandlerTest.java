package ru.otus.backend.eventApi.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест проверяет")
class CommandHandlerTest {
    private final CommandHandler commandHandler = new CommandHandler();
    final private String START_COMMAND = "/start";
    final private String HELP_COMMAND = "/help";

    @DisplayName("корректную работу парсера команд")
    @Test
    void correctWorkOfCommandParser() {
        assertThat(commandHandler.getInfo("/start")).isEqualTo("Привет! Я найду билеты на любой концерт! Введите исполнителя!");
        assertThat(commandHandler.getInfo("/help")).contains("Это бот для поиска билетов на концерт.");
        assertThat(commandHandler.getInfo("/hjlghj")).contains("Неизвестная команада.");
        assertThat(commandHandler.getInfo("/")).contains("Неизвестная команада.");
        assertThat(commandHandler.getInfo("/ Beatles")).contains("Неизвестная команада.");
        assertThat(commandHandler.getInfo("/ Beatles")).contains("Неизвестная команада.");
        assertThrows(Exception.class, () -> {
            commandHandler.getInfo(null);
        });
    }
}