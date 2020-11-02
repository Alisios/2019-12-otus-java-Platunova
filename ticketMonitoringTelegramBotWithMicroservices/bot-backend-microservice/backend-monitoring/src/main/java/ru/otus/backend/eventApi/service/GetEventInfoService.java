package ru.otus.backend.eventApi.service;

import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.util.List;

public interface GetEventInfoService {

    List<ConcertModel> getEventInformation(String message) throws IOException;

    List<TicketModel> getTicketInformation(ConcertModel concertModel) throws IOException;
}
