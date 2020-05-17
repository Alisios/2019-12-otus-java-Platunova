package ru.otus.backend.eventApi;

import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;

import java.util.List;
import java.util.Map;


public interface MonitoredEvent {

     MessageForFront getConcertInfo(Message message);
     String getTicketInfo(String message, int index);
    // String getTicketInfo(Long chartId, int MessageId, int index, int messageIdCurrent);
   //  void endOfWorkForThisEvent(Long chartId, int messageId);
    ConcertModel getModel(String message);
    // Map<Long, Map<Integer, List<ConcertModel>>> getCacheMap();
}
