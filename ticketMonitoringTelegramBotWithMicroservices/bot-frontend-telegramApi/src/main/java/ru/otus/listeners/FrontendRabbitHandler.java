package ru.otus.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;
import ru.otus.telegramApi.TelegramService;

@Component
@Slf4j
public class FrontendRabbitHandler implements RabbitHandler {

    private final TelegramService telegramService;

    @Autowired
    FrontendRabbitHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void processMsgFromRabbit(Message message) throws TelegramApiException {

        MessageForFront fromBack = Serializers.deserialize(message.getBody(), MessageForFront.class);
        if (fromBack.getMessageType().getValue().equals(MessageType.GET_EVENT_INFO.getValue())) {
            telegramService.sendMsg(fromBack);
        } else if (fromBack.getMessageType().getValue().equals(MessageType.GET_TICKET_INFO.getValue())) {
            telegramService.sendMsgQuery(fromBack);
        } else if (fromBack.getMessageType().getValue().equals(MessageType.NOTIFY.getValue())) {
            telegramService.sendNotifyingMsg(fromBack);
        }
        log.info("The message {} is  sent to User!", Serializers.deserialize(fromBack.getPayload(), String.class));
    }
}
