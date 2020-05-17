package ru.otus.telegramApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.Serializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class TelegramServiceImpl implements  TelegramService {
    private static Logger logger = LoggerFactory.getLogger(TelegramServiceImpl.class);
    private Bot bot;

    final private Lock lock1 = new ReentrantLock();
    final private Lock lock2 = new ReentrantLock();
    final private Lock lock3 = new ReentrantLock();

    public TelegramServiceImpl(Bot bot){
        this.bot = bot;
    }

    @Override
    public void sendMsg(MessageForFront message){

            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId());//в какой конкрентный чат отпарвить ответ
            sendMessage.setReplyToMessageId(message.getMessageId());//на какое сообщение мы будем овтечать
            sendMessage.setText(Serializers.deserialize(message.getPayload(), String.class));
            sendMessage.setParseMode("HTML");
            try{
                logger.info("TypeOfMessage in sendMsg {}",message.getCallbackType());
                if (message.getCallbackType().equals(CallbackType.LIST_OF_EVENTS.getValue())){
                    setInline(sendMessage, message.getNumberOfEvents());
                    bot.execute(sendMessage);
                }
                else if(message.getCallbackType().equals(CallbackType.IF_SHOULD_BE_MONITORED.getValue())){
                    setInlineNotify(sendMessage);
                    bot.execute(sendMessage);
                }
                else {
                    bot.execute(sendMessage);
                }
            }
            catch (TelegramApiException ex){
                logger.info("TelegramApiException in Send message");
                //добавить обработку слишком длинных сообщений
            }
        }

    @Override
    public void sendMsgQuery(MessageForFront message){
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId());//в какой конкрентный чат отпарвить ответ
            sendMessage.setReplyToMessageId(message.getMessageId());//на какое сообщение мы будем овтечать
            sendMessage.setText(Serializers.deserialize(message.getPayload(), String.class));
            sendMessage.setParseMode("HTML");
            if (message.getCallbackType().equals(CallbackType.IF_SHOULD_BE_MONITORED.getValue()))
                setInlineNotify(sendMessage);
//                EditMessageText new_message = new EditMessageText()
//                        .setChatId(message.getChatId())
//                        .setMessageId(Math.toIntExact(message.getMessageId()))
//                        .setText(Serializers.deserialize(message.getPayload(), String.class));
                bot.execute(sendMessage);//}
            } catch (TelegramApiException e) {
                logger.error("TelegramApiException in SendQuery message");
                e.printStackTrace();
            }
    }

    @Override
    public void sendNotifyingMsg(MessageForFront message){
//        try {
//            lock3.lock();
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId());//в какой конкрентный чат отпарвить ответ
            sendMessage.setText(Serializers.deserialize(message.getPayload(), String.class));
            sendMessage.setParseMode("HTML");
            try {
                logger.info("Event happens success of monitoring. Text: {}", Serializers.deserialize(message.getPayload(), String.class));
                bot.execute(sendMessage);
            } catch (TelegramApiException ex) {
                logger.info("TelegramApiException in Send message");
                //добавить обработку слишком длинных сообщений
            }
        }
//        finally {
//            lock3.unlock();
//        }
//    }

    private void setInline(SendMessage sendMessage, int size) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i =1; i< size+1; i++){
            rowList.add(Collections.singletonList(new InlineKeyboardButton().setText("Событие № "+ i).setCallbackData(String.valueOf(i-1))));
        }
        rowList.add(Collections.singletonList(new InlineKeyboardButton().setText("Ничего не подходит").setCallbackData(CallbackType.NOTHING.getValue())));
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(rowList);
        sendMessage.setReplyMarkup(markupKeyboard);
    }

    private void setInlineNotify(SendMessage sendMessage) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(Collections.singletonList(new InlineKeyboardButton().setText("Да!").setCallbackData((CallbackType.NOTIFY.getValue()))));
        rowList.add(Collections.singletonList(new InlineKeyboardButton().setText("Нет, спасибо!").setCallbackData(CallbackType.NO.getValue())));
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(rowList);
        sendMessage.setReplyMarkup(markupKeyboard);
    }
}
