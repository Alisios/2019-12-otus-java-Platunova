package ru.otus.backend.eventApi.rest;

import ru.otus.backend.model.ConcertRestModel;

import java.io.IOException;
import java.util.List;

public interface EventRestService {

    List<ConcertRestModel> getEventByArtist(String string) throws IOException;

    List<ConcertRestModel> getTickets(String artist, String date, String place) throws IOException;
}
