package ru.otus.telegramApi;
import ru.otus.helpers.MessageForFront;

public interface TelegramService {

    void sendMsg(MessageForFront message);
    void sendMsgQuery(MessageForFront message);
    void sendNotifyingMsg(MessageForFront message);
}
