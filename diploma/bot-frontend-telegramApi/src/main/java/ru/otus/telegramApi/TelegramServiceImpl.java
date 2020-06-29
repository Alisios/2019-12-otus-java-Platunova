package ru.otus.telegramApi;

import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *  формирование конечного собщения пользователю через telegram api
 */

@Service
@Slf4j
public class TelegramServiceImpl implements  TelegramService {
    private Bot bot;
    private final AmqpTemplate template;
    private final RabbitMQProperties rabbitProperties;

    @Autowired
    public TelegramServiceImpl(AmqpTemplate template, RabbitMQProperties rabbitProperties) {
        this.template = template;
        this.rabbitProperties = rabbitProperties;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }


    @Override
    public void sendMsg(MessageForFront message) throws TelegramApiException{
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId());
            sendMessage.setReplyToMessageId(message.getMessageId());
            sendMessage.setText(Serializers.deserialize(message.getPayload(), String.class));
            sendMessage.setParseMode("HTML");
            try{
                log.info("TypeOfMessage in sendMsg {}",message.getCallbackType());
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
                log.info("TelegramApiException in Send message {}\t {}\n {}",ex.getCause(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                throw new TelegramApiException ("The message from sendMsg is not sent to user"+ex.getCause()+". "+ ex.getMessage());
                //добавить обработку слишком длинных сообщений
            }
        }

    @Override
    public void sendMsgQuery(MessageForFront message) throws TelegramApiException{
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId());
            sendMessage.setReplyToMessageId(message.getMessageId());
            sendMessage.setText(Serializers.deserialize(message.getPayload(), String.class));
            sendMessage.setParseMode("HTML");
            if (message.getCallbackType().equals(CallbackType.IF_SHOULD_BE_MONITORED.getValue()))
                setInlineNotify(sendMessage);
                bot.execute(sendMessage);//}
            } catch (TelegramApiException ex) {
                log.error("TelegramApiException in SendQuery message {}\n {}", ex.getCause(), ex.getMessage());
                throw new TelegramApiException ("The message from sendMsgQuery is not sent to user"+ex.getCause()+". "+ ex.getMessage());
            }
    }

    @Override
    public void sendNotifyingMsg(MessageForFront message) throws TelegramApiException {
//        try {
//            lock3.lock();
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId());//в какой конкрентный чат отпарвить ответ
            sendMessage.setText(Serializers.deserialize(message.getPayload(), String.class));
            sendMessage.setParseMode("HTML");
            try {
                log.info("Success of monitoring. Text: {}", Serializers.deserialize(message.getPayload(), String.class));
                bot.execute(sendMessage);
            } catch (TelegramApiException ex) {
                log.error("TelegramApiException in Send Notify message {}", Arrays.toString(ex.getStackTrace()));
                throw new TelegramApiException ("The message from sendNotifyingMsg is not sent to user"+ Arrays.toString(ex.getStackTrace()));
            }
        }

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

    @Override
    public void sendMessageToRabbit(MessageModel msg){
        template.convertAndSend(rabbitProperties.getFrontProducerExchange(),
                rabbitProperties.getFrontProducerQueue(),
                MessageBuilder.withBody(Serializers.serialize(msg))
                        .setContentType(String.valueOf(MessageProperties.PERSISTENT_TEXT_PLAIN))
                        .build());
    }
}
