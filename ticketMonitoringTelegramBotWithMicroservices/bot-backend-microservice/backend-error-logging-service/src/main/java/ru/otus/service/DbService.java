package ru.otus.service;

import ru.otus.backend.model.Log;

import java.util.List;
import java.util.Optional;

public interface DbService {

    Optional<Log> save(Log log);

    List<Log> getLogsOfType(String type);

    List<Log> getLogsContainingMessage(String message);

    List<Log> getAllLogs();

    List<Log> getLogsByChatId(long chatId);
}
