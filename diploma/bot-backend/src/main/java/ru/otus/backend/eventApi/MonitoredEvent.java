package ru.otus.backend.eventApi;

import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;


public interface MonitoredEvent {

     MessageForFront getConcertInfo(Message message);
     MessageForFront getTicketInfo(Long chartId, int MessageId, int index, Message message);
     Boolean checkingTickets(User user);
     void endOfWorkForThisEvent(Long chartId, int messageId);
     ConcertModel getModel(Long chartId, int messageId);
}
