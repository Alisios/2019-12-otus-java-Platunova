package ru.otus.backend.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.BackendService;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestHandlerImpl implements RequestHandler {

    private final BackendService backendService;

    @Override
    public Optional<MessageForFront> getTicketData(MessageModel msg) throws IOException {
        log.info("new message in back handler:{}", msg.getMessageType());
        var eventInfo = backendService.getTicketData(Serializers.deserialize(msg.getPayload(), CallbackQuery.class));
        return Optional.of(eventInfo);
    }

    @Override
    public Optional<MessageForFront> getEventData(MessageModel msg) throws IOException {
        log.info("new message back handler:{}", msg.getMessageType());
        var eventInfo = backendService.getEventData(Serializers.deserialize(msg.getPayload(), Message.class));
        return Optional.of(eventInfo);
    }

    @Override
    public Optional<MessageModel> switchingOnMonitoring(Message message) {
        log.info("new message monitoring back handler:{}", message.getText());
        return Optional.of(new MessageModel(MessageType.SAVE_USER, Serializers.serialize(backendService.switchingOnEventMonitoring(message))));
    }

    @Override
    public Optional<MessageForFront> errorMessageForFront(MessageModel msg) {
        log.info("forming error message for user");
        return Optional.of(backendService.errorMessage(msg));
    }

}
