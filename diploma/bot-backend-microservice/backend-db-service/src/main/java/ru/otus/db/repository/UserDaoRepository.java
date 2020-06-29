package ru.otus.db.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.otus.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDaoRepository extends CrudRepository<User, Long> {

    Optional<User> findById(long id);

    User save(User user);

    @Query("FROM User u WHERE u.chatId=:chatId and u.concert.artist=:artist")
    Optional<User> findByChatIdAndConcertArtist(long chatId, String artist);


    @Query("FROM User AS u LEFT JOIN FETCH u.concert")
    List<User> findAll();

    void deleteById(long id);

    void deleteAll();

     @Query("FROM User u WHERE u.isMonitoringSuccessful = true or u.isDateExpired = true")
    List<User> findByIsMonitoringSuccessfulIsTrueAndIsDateExpiredIsTrue();

}
