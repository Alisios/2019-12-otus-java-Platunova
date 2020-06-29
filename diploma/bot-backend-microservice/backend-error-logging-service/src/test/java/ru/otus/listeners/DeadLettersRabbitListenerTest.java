package ru.otus.listeners;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.backend.model.Log;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.db.repository.LogsDao;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class DeadLettersRabbitListenerTest {
    private static final Logger logger = LoggerFactory.getLogger(DeadLettersRabbitListenerTest.class);

    @MockBean
    RabbitMQProperties rabbitProperties;

    @MockBean
    ConnectionFactory connectionFactory;

    @MockBean
    public AmqpAdmin amqpAdmin;

    @MockBean
    public RabbitTemplate rabbitTemplate;

    @MockBean
    FanoutExchange deadLetterExchange;

    @MockBean
    Queue deadLetterQueue;

    @MockBean
    Binding deadLetterBinding;

    @Autowired
    private LogsDao logsDao;

    @Autowired
    org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;


    @Test
    void testOfMongoRepository(){
        mongoTemplate.getDb().drop();
        Log log = new Log();
        log.setErrorMessage("errormessage");
        log.setTypeOfError("GetTicket");
        logsDao.save(log);
        Log log1 = new Log();
        log1.setErrorMessage("Сообщение которое нужно найти");
        log1.setTypeOfError("GetInfo");
        logsDao.save(log1);
        Log log2 = new Log();
        log2.setErrorMessage("Сообщение которое не нужно");
        log2.setTypeOfError("ForDelete");
        logsDao.save(log2);
        assertThat(logsDao.findAll().size()).isEqualTo(3);
        assertThat(logsDao.findByTypeOfError("GetTicket").get(0)).isEqualTo(log);
        assertThat(logsDao.findByErrorMessageContaining("нужно найти").get(0)).isEqualTo(log1);
        logsDao.delete(log2);
        assertThat(logsDao.findAll().size()).isEqualTo(2);
        log.setErrorMessage("Другое сообщение");
        assertThat(logsDao.findByTypeOfError("GetTicket").get(0)).isNotEqualTo(log);
        logsDao.save(log);
        assertThat(logsDao.findByTypeOfError("GetTicket").get(0)).isEqualTo(log);

        logger.info("{}", logsDao.findAll());
        mongoTemplate.getDb().drop();

    }
}