package ru.otus.listeners;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Arrays;


@Component
@RequiredArgsConstructor
@Slf4j
public class FrontendListener {

    private final FrontendRabbitHandler frontendRabbitHandler;
    private final static String BACK_PRODUCER_QUEUE = "back_producer_queue";

    @RabbitListener(queues = BACK_PRODUCER_QUEUE, ackMode = "MANUAL")
    void frontendHandler(Message message, Channel channelConsumer,
                         @Header(AmqpHeaders.REDELIVERED) boolean isRedelivered,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("frontendHandler started");
        channelConsumer.basicQos(1);
        try {
            frontendRabbitHandler.processMsgFromRabbit(message);
            channelConsumer.basicAck(tag, false);
        } catch (TelegramApiException ex) {
            log.error("It seemed that there is the problem with the Internet : impossible to sent a message to user {}", Arrays.toString(ex.getStackTrace()));
            channelConsumer.basicReject(tag, true);
        } catch (ClassCastException | IllegalArgumentException | IOException ex) {
            log.error("Impossible to handle the message . Sending to to deadLetterQueue {}\n{}", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
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
