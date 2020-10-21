package ru.otus.listeners;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.otus.db.service.DBException;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class MsgDbListener {
    private final DbHandler dbHandler;
    private static final String DB_QUEUE = "db_queue";

    @Autowired
    MsgDbListener(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @RabbitListener(queues = DB_QUEUE, ackMode = "MANUAL")
    public void dbHandler(Message message, Channel channel,
                          @Header(AmqpHeaders.REDELIVERED) boolean isRedelivered,
                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Worker of db got the message ");
        channel.basicQos(1);
        try {
            dbHandler.processMsgFromRabbit(message);
            channel.basicAck(tag, false);
        } catch (ClassCastException | DBException e) {
            log.error("ClassCastException/DBException in DB:{} \n {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            channel.basicReject(tag, false);
        } catch (Exception ex) {
            if (isRedelivered) {
                log.error("Fail with handling the message in DB listener  at the second time. Now it is going to deadLetterQueue. {}, {}\n{}", ex.getCause(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                channel.basicReject(tag, false);
            } else {
                log.error("Fail with  message in DB listener because of: {}. Trying to requeue. \n{}", ex.getMessage(), ex.getStackTrace());
                channel.basicReject(tag, true);
            }
        }
    }
}
