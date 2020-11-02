package ru.otus.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import ru.otus.backend.model.Log;
import ru.otus.db.repository.LogsDao;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Тесты проверяют, что репозиторий логов:")
@DataMongoTest
@ComponentScan({"ru.otus.configuration.configs", "ru.otus.db.repository"})
@EnableConfigurationProperties
class DbServiceMongoTest {

    @Autowired
    private LogsDao logsDao;

    void set() {
        var log = new Log();
        log.setChatId(1L);
        log.setErrorMessage("errormessage#2");
        log.setTypeOfError("GetTicket");
        logsDao.save(log);
        var log2 = new Log();
        log2.setErrorMessage("errormessage#3");
        log2.setChatId(2L);
        log2.setTypeOfError("GetTicket");
        logsDao.save(log2);
        var log3 = new Log();
        log3.setChatId(3L);
        log3.setErrorMessage("errormessage#4");
        log3.setTypeOfError("another");
        logsDao.save(log3);
    }

    @Test
    @DisplayName("корректно вставляет новый лог")
    void correctlyInsertNewLog() {
        var log4 = new Log();
        log4.setErrorMessage("errormessage123");
        log4.setTypeOfError("GetTicket");
        assertThat(logsDao.save(log4))
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("chatId", log4.getChatId())
                .hasFieldOrPropertyWithValue("errorMessage", log4.getErrorMessage())
                .hasFieldOrPropertyWithValue("typeOfError", log4.getTypeOfError());
    }

    @Test
    @DisplayName("корректно находитлог по типу сообщения")
    void correctlyFindLogByType() {
        ;
        logsDao.deleteAll();
        set();
        var logN = logsDao.findByTypeOfError("GetTicket");
        assertThat(logN)
                .isNotNull()
                .hasSize(2)
                .allMatch(s -> s.getTypeOfError().equals("GetTicket"))
                .allMatch(s -> s.getChatId() != 0)
                .allMatch(s -> !s.getErrorMessage().equals(""));
    }

    @Test
    @DisplayName("корректно находит лог по chatId")
    void correctlyFindLogByChatId() {
        logsDao.deleteAll();
        set();
        var logN = logsDao.findByChatId(2L);
        assertThat(logN)
                .isNotNull()
                .hasSize(1)
                .allMatch(s -> s.getTypeOfError().equals("GetTicket"))
                .allMatch(s -> s.getChatId() != 0)
                .allMatch(s -> !s.getErrorMessage().equals(""));
    }

    @Test
    @DisplayName("корректно находит лог по его содержанию")
    void correctlyFindLogByMessage() {
        logsDao.deleteAll();
        set();
        var logN = logsDao.findByErrorMessageContaining("errorme");
        assertThat(logN)
                .isNotNull()
                .hasSize(3)
                .allMatch(s -> s.getChatId() != 0)
                .allMatch(s -> s.getErrorMessage().contains("errorme"));

        var logN2 = logsDao.findByErrorMessageContaining("errormessage#2");
        assertThat(logN2)
                .isNotNull()
                .hasSize(1)
                .allMatch(s -> s.getChatId() != 0)
                .allMatch(s -> s.getErrorMessage().contains("errormessage#2"));

    }

    @Test
    @DisplayName("не находит несуществующий лог")
    void doesNotFindLogByType() {
        assertThat(logsDao.findByTypeOfError("sdFADSF")).isEmpty();
    }
}