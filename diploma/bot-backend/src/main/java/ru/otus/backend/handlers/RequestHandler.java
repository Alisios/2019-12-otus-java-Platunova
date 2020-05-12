package ru.otus.backend.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.BackendService;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;
import ru.otus.helpers.MessageType;

import java.util.Optional;

public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final BackendService backendService;

    public RequestHandler(BackendService backendService) {
        this.backendService = backendService;
    }


    public Optional<MessageForFront> getTicketData(MessageModel msg) {
        logger.info("new message cb back handler:{}", msg.getMessageType());
        CallbackQuery callbackQuery = Serializers.deserialize(msg.getPayload(), CallbackQuery.class);
        MessageForFront eventInfo = backendService.getTicketData(callbackQuery);
        return Optional.of(eventInfo);
    }

    public Optional<MessageForFront> getEventData(MessageModel msg) {
        logger.info("new message back handler:{}", msg.getMessageType());
        Message message = Serializers.deserialize(msg.getPayload(), Message.class);
        MessageForFront eventInfo = backendService.getEventData(message);
        return Optional.of(eventInfo);
    }

    public Optional<MessageForFront> getMonitoringResult(User user) {
        logger.info("new message monitoring back handler:{}", user);
        MessageForFront eventInfo = backendService.getMonitoringResult(user);
        return Optional.of(eventInfo);
    }
    public Optional<MessageForFront> switchingOnMonitoring(Message message) {
        logger.info("new message monitoring back handler:{}", message.getText());
        User newUser = backendService.switchingOnEventMonitoring(message);
        return Optional.of(new MessageForFront(MessageType.SAVE_USER, Serializers.serialize(newUser), message.getChatId(), message.getMessageId()));
    }

}
