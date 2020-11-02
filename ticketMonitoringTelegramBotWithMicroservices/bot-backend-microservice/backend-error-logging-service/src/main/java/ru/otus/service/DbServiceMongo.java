package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.backend.model.Log;
import ru.otus.db.repository.LogsDao;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DbServiceMongo implements DbService {

    final private LogsDao logsDao;

    @Override
    public List<Log> getLogsOfType(String type) {
        try {
            return logsDao.findByTypeOfError(type);
        } catch (RuntimeException ex) {
            log.error("Error with finding log by type {}", ex.getMessage());
            throw new DbException("Error with finding log by type", ex);
        }
    }

    @Override
    public List<Log> getAllLogs() {
        try {
            return logsDao.findAll();
        } catch (RuntimeException ex) {
            log.error("Error with finding all logs  {}", ex.getMessage());
            throw new DbException("Error with finding all log by type", ex);
        }
    }

    @Override
    public Optional<Log> save(Log logging) {
        try {
            return Optional.of(logsDao.save(logging));
        } catch (RuntimeException ex) {
            log.error("Error with saving log :  {}", ex.getMessage());
            throw new DbException("Error with saving log by type", ex);
        }
    }

    @Override
    public List<Log> getLogsContainingMessage(String message) {
        try {
            return logsDao.findByErrorMessageContaining(message);
        } catch (RuntimeException ex) {
            log.error("Error with finding log by message:  {}", ex.getMessage());
            throw new DbException("Error with finding log by message", ex);
        }
    }

    @Override
    public List<Log> getLogsByChatId(long chatId) {
        try {
            return logsDao.findByChatId(chatId);
        } catch (RuntimeException ex) {
            log.error("Error with finding log by chatId:  {}", ex.getMessage());
            throw new DbException("Error with finding log by chatId", ex);
        }
    }
}
