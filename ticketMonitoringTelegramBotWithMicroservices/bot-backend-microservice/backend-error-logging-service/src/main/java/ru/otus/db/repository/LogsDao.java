package ru.otus.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.backend.model.Log;

import java.util.List;

public interface LogsDao extends MongoRepository<Log, String> {

    List<Log> findByTypeOfError(String type);

    Log insert(Log log);

    List<Log> findAll();

    List<Log> findByErrorMessageContaining(String message);

    List<Log> findByChatId(long chatId);

}
