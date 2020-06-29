package ru.otus.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.backend.model.Log;
import ru.otus.db.repository.LogsDao;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DbServiceMongo  implements DbService {

    final private LogsDao logsDao;

    @Override
    public List<Log> getLogsOfType(String type) {
        return logsDao.findByTypeOfError(type);
    }

    @Override
    public List<Log> getAllLogs(){
        return logsDao.findAll();
    }

    @Override
    public Optional<Log> save(Log log){
        return Optional.of(logsDao.save(log));
    }

    @Override
    public List<Log> getLogsContainingMessage(String message){
        return logsDao.findByErrorMessageContaining(message);
    }

    @Override
    public List<Log> getLogsByChatId(long chatId){
        return logsDao.findByChatId(chatId);
    }
}
