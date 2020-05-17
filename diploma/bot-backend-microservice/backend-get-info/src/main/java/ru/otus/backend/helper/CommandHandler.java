package ru.otus.backend.helper;

public class CommandHandler {
    final private  String START_COMMAND = "/start";
    final private  String HELP_COMMAND = "/help";

    public String getInfo(String message){
        if (message.trim().equals(START_COMMAND))
            return "Привет! Я найду билеты на любой концерт! Введите исполнителя!";
        else if (message.trim().equals(HELP_COMMAND))
            return "Это бот для поиска билетов на концерт. Для начала работы введите имя исполнителя. " +
                    "Имя не должно начинаться со знака '/'.";
        else
            return "Неизвестная команада. Для начала работы введите имя исполнителя. Имя не должно начинаться со знака '/'.";
    }
}
