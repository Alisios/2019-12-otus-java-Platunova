package ru.otus.backend.handlers;

import org.telegram.telegrambots.api.objects.Message;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;

import java.io.IOException;
import java.util.Optional;

public interface RequestHandler {

    Optional<MessageForFront> getTicketData(MessageModel msg) throws IOException;

    Optional<MessageForFront> getEventData(MessageModel msg) throws IOException;

    Optional<MessageModel> switchingOnMonitoring(Message message);

    Optional<MessageForFront> errorMessageForFront(MessageModel msg);
}
