package ru.otus.backend.eventApi;

import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * общий интерефейс для всех мероприятий
 * вид мероприятия может меняться(Концерт, балет, цирковое представление и тп)
 */
public interface MonitoredEvent {

    MessageForFront getConcertInfo(Message message) throws IOException;

    String getTicketInfo(String message, int index) throws IOException;

    User monitorOfEvent(Message message);
}
