package ru.otus.backend.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.BackendService;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;
import ru.otus.helpers.MessageType;

import java.io.IOException;
import java.util.Optional;

@Component
public class RequestHandlerImpl implements RequestHandler  {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerImpl.class);
    private final BackendService backendService;


    public RequestHandlerImpl(BackendService backendService) {
        this.backendService = backendService;
    }

    @Override
    public Optional<MessageForFront> getTicketData(MessageModel msg) throws IOException {
        logger.info("new message in back handler:{}", msg.getMessageType());
        CallbackQuery callbackQuery = Serializers.deserialize(msg.getPayload(), CallbackQuery.class);
        MessageForFront eventInfo = backendService.getTicketData(callbackQuery);
        return Optional.of(eventInfo);
    }

    @Override
    public Optional<MessageForFront> getEventData(MessageModel msg) throws IOException {
        logger.info("new message back handler:{}", msg.getMessageType());
        Message message = Serializers.deserialize(msg.getPayload(), Message.class);
        MessageForFront eventInfo = backendService.getEventData(message);
        return Optional.of(eventInfo);
    }

    @Override
    public Optional<MessageModel> switchingOnMonitoring(Message message) {
        logger.info("new message monitoring back handler:{}", message.getText());
        User newUser = backendService.switchingOnEventMonitoring(message);
        return Optional.of(new MessageModel(MessageType.SAVE_USER, Serializers.serialize(newUser)));
    }

    @Override
    public Optional<MessageForFront> errorMessageForFront(MessageModel msg) {
        logger.info("forming error message for user");
        MessageForFront errorMessage = backendService.errorMessage(msg);
        return Optional.of(errorMessage);
    }

}
