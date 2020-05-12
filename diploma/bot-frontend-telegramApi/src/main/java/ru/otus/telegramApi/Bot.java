package ru.otus.telegramApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.util.concurrent.BlockingQueue;

public class Bot extends TelegramLongPollingBot {
    private static Logger logger = LoggerFactory.getLogger(Bot.class);

    final private String name;
    final private String token;
    private final int RECONNECT_PAUSE =5000;
    private BlockingQueue<MessageModel> messageQueue;

    public Bot(String name, String token){
        this.name = name;
        this.token = token;
    }

    public void setMessageQueue(BlockingQueue<MessageModel> messageQueue){
        this.messageQueue = messageQueue;
    }


    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            Message message = update.getMessage();
            logger.info("new message in update {}", message.getText());
            if (message.hasText()) {
                messageQueue.add(new MessageModel(MessageType.GET_EVENT_INFO, Serializers.serialize(message)));
            }
        }
        else if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery =  update.getCallbackQuery();
            logger.info("new message in callback {}", callbackQuery.getData());
            messageQueue.add(new MessageModel(MessageType.GET_TICKET_INFO, Serializers.serialize(callbackQuery)));
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
            logger.info("TelegramAPI started. Looking for messages");
        } catch (TelegramApiRequestException e) {
            logger.error("Can't Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }
}