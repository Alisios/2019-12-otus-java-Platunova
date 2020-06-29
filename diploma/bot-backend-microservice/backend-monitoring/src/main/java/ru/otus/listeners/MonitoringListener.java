package ru.otus.listeners;

import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.w3c.dom.events.EventException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

@Component
@Slf4j
@AllArgsConstructor
public class MonitoringListener {

    private final RabbitHandler monitoringRabbitHandler;

    private final static String MONITORING_QUEUE = "monitoring_queue";

    @RabbitListener(queues = MONITORING_QUEUE, ackMode = "MANUAL")
    public void monitoringHandler(Message msg, Channel channelConsumer, @Header(AmqpHeaders.REDELIVERED) boolean isRedelivered, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Monitoring worker got the message ");
        channelConsumer.basicQos(1);
        try {
            monitoringRabbitHandler.processMsgFromRabbit(msg);
            channelConsumer.basicAck(tag, false);
        } catch (UnknownHostException ex) {
            log.error("Impossible to handle the message or parse the source url. Sending to to deadLetterQueue {}\n{}", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
            channelConsumer.basicReject(tag, true);
        } catch (EventException | ClassCastException | IllegalArgumentException | IOException ex) {
            log.error("ClassCastException in monitoring : {}, {}, {}", ex.getCause(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
            channelConsumer.basicReject(tag, false);
        } catch (Exception ex) {
            if (isRedelivered) {
                log.error("Fail with handling the message at the second time. Now it is going to deadLetterQueue. {}, {}\n{}", ex.getCause(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                channelConsumer.basicReject(tag, false);
            } else {
                log.error("Fail with handling the message because of: {}. Trying to requeue", ex.getMessage());
                channelConsumer.basicReject(tag, true);
            }
        }
    }
}

