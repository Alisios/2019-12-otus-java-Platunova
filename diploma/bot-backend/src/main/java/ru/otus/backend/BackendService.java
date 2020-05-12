package ru.otus.backend;

import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.model.User;
import ru.otus.helpers.CallbackType;
import ru.otus.helpers.MessageForFront;

public interface BackendService {

    MessageForFront getEventData (Message message);
    MessageForFront getTicketData(CallbackQuery callbackQuery);
    MessageForFront getMonitoringResult(User user);
    User switchingOnEventMonitoring(Message message);
}
