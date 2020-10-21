package ru.otus.backend.eventApi.parsers;

import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.util.List;

public interface HtmlParser {
    List<ConcertModel> getEventsFromHtml(String message) throws IOException;

    List<TicketModel> getTicketInfoFromHtml(ConcertModel concertModel) throws IOException;
}
