package ru.otus.telegramApi;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.util.Arrays;

/**
 * реализует telegram api приема сообщений, точка входа в приложение
 */

@Slf4j
@Setter
public class Bot extends TelegramLongPollingBot {
    final private String name;
    final private String token;
    final private TelegramService telegramService;
    final private int reconnectPause;

    public Bot(String name, String token, TelegramService telegramService, int reconnectPause) {
        this.name = name;
        this.token = token;
        this.telegramService = telegramService;
        this.reconnectPause = reconnectPause;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            log.info("new message in update {}", message.getText());
            if (message.hasText()) {
                telegramService.sendMessageToRabbit(new MessageModel(MessageType.GET_EVENT_INFO, Serializers.serialize(message)));
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("new message in callback {}", callbackQuery.getData());
            telegramService.sendMessageToRabbit(new MessageModel(MessageType.GET_TICKET_INFO, Serializers.serialize(callbackQuery)));
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Looking for messages");
        } catch (TelegramApiRequestException e) {
            log.error("Can't Connect. Pause " + reconnectPause / 1000 + "sec and try again. Error: " + e.getMessage() + "\n" + e.getApiResponse() + "\n" + e.getCause() + "\n");
            e.printStackTrace();
            try {
                Thread.sleep(reconnectPause);
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
                log.error("Interrupted Exception exception: {}\n{}", e1.getCause(), e1.getStackTrace());
                return;
            }
            botConnect();
        }
    }
}