package ru.otus.service;

import ru.otus.backend.model.ConcertRestModel;

import java.util.List;
import java.util.Optional;

public interface DbService {

    Optional<ConcertRestModel> getConcertById(long id);

    long saveConcert(ConcertRestModel user);

    List<ConcertRestModel> getAllConcerts();

    void deleteConcertById(long id);

    void deleteConcertByArtist(String artist);

    List<ConcertRestModel> getConcertByArtist(String artist);

    void deleteAllConcerts();

    List<ConcertRestModel> saveAll(List<ConcertRestModel> concerts);

    List<ConcertRestModel> findTickets(String artist, String date, String place);
}
