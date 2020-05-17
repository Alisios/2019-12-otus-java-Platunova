package ru.otus.backend.eventApi;

import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.backend.model.User;

import java.util.List;


public interface MonitoringService {

     Boolean checkingTickets(User user);
     String analyzeTicketInfo (ConcertModel concert, List<TicketModel> ticketModelList);
}
