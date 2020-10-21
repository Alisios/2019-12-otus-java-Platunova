package ru.otus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.otus.backend.model.ConcertRestModel;

import java.util.List;
import java.util.Optional;

public interface DaoConcertRepository extends CrudRepository<ConcertRestModel, Long> {

    Optional<ConcertRestModel> findById(long id);

    ConcertRestModel save(ConcertRestModel concert);


    @Query("FROM ConcertRestModel c WHERE c.artist=:artist")
    List<ConcertRestModel> findByArtist(String artist);

    List<ConcertRestModel> findByArtistContainingIgnoreCase(String artist);

    @Query("FROM ConcertRestModel c WHERE (c.artist=:artist and c.date=:date and c.place=:place)")
    List<ConcertRestModel> findTickets(String artist, String date, String place);


    @Query("FROM ConcertRestModel AS c LEFT JOIN FETCH c.tickets")
    List<ConcertRestModel> findAll();

    void deleteAll();

    void deleteByArtist(String artist);

    void deleteById(long id);
}
