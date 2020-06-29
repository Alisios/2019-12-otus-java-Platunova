package ru.otus.listeners;

import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.otus.backend.model.Log;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;
import ru.otus.service.DbService;
import ru.otus.service.LoggingService;

import java.io.IOException;


@Component
@AllArgsConstructor
@Slf4j
public class DeadLettersRabbitListener {

    private final RabbitMQProperties rabbitProperties;
    private final LoggingService loggingService;
    private final DbService dbService;

    private final static String DEAD_LETTERS_QUEUE = "dead_letters_queue";

    @RabbitListener(queues = DEAD_LETTERS_QUEUE, ackMode = "MANUAL")
    public void processErrorLetters(Message message, Channel channelMessageConsumer, @Header(AmqpHeaders.REDELIVERED) boolean isRedelivered, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("processErrorLetters got message");
        try {
            if (message.getMessageProperties().getHeaders().values().toString().contains(rabbitProperties.getBackProducerExchange())) {
                MessageForFront mmsFrom = Serializers.deserialize(message.getBody(), MessageForFront.class);
                Log logMy =  dbService.save(loggingService.loggingMessageForFront(mmsFrom)).orElseThrow();
                log.info("{} Headers of dead_letter: {}",logMy, message.getMessageProperties().getHeaders());
            } else {
                MessageModel mmsFrom = Serializers.deserialize(message.getBody(), MessageModel.class);
                Log logMy =  dbService.save(loggingService.loggingMessageModel(mmsFrom)).orElseThrow();
                log.info("{} Headers of dead_letter: {}",logMy, message.getMessageProperties().getHeaders());
            }
        } catch (ClassCastException e){
            log.error("Impossible to deserialize message with headers : {}. \n {}. \n  {}. \n{}. \n", message.getMessageProperties().getHeaders(),e.getMessage(), e.getCause(), e.getStackTrace());

        } catch (Exception ex) {
            log.error("Error with handling of error message with headers : {}. \n {}. \n  {}. \n",
                    message.getMessageProperties().getHeaders(), ex.getMessage(), ex.getStackTrace());
        } finally {
            channelMessageConsumer.basicAck(tag, false);
        }
    }
}
