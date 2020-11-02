package ru.otus.telegramApi;

import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;

public interface TelegramService {

    void sendMsg(MessageForFront message) throws TelegramApiException;

    void sendMsgQuery(MessageForFront message) throws TelegramApiException;

    void sendNotifyingMsg(MessageForFront message) throws TelegramApiException;

    void sendMessageToRabbit(MessageModel msg);

    void setBot(Bot bot);
}
