package ru.otus.backend.eventApi.helpers;

import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.util.List;

public interface HtmlParser{
     List<ConcertModel> getEventsFromHtml(String message);
     List<TicketModel>  getTicketInfoFromHtml(String urlFinal);
}
