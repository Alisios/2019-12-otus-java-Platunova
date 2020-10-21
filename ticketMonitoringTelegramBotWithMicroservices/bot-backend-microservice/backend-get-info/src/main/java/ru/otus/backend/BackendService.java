package ru.otus.backend;

import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;

import java.io.IOException;

public interface BackendService {

    MessageForFront getEventData(Message message) throws IOException;

    MessageForFront getTicketData(CallbackQuery callbackQuery) throws IOException;

    User switchingOnEventMonitoring(Message message);

    MessageForFront errorMessage(MessageModel message);
}
