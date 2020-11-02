package ru.otus.backend.listeners;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.otus.backend.eventApi.EventException;
import ru.otus.backend.handlers.RequestHandler;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

@Component
@Slf4j
public class BackendListener {

    private final RabbitMQProperties rabbitProperties;
    private final RabbitHandler backendRabbitHandler;
    private final RequestHandler requestHandler;
    private final AmqpTemplate template;

    private final static String FRONT_PRODUCER_QUEUE = "front_producer_queue";

    @Autowired
    public BackendListener(@Qualifier("rabbitMQProperties") RabbitMQProperties rabbitProperties, RabbitHandler backendRabbitHandler, RequestHandler requestHandler, AmqpTemplate template) {
        this.rabbitProperties = rabbitProperties;
        this.backendRabbitHandler = backendRabbitHandler;
        this.requestHandler = requestHandler;
        this.template = template;
    }

    @RabbitListener(queues = FRONT_PRODUCER_QUEUE, ackMode = "MANUAL")//, concurrency = "2")
    public void backendHandler(Message message, Channel channelMessageConsumer, @Header(AmqpHeaders.REDELIVERED) boolean isRedelivered, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Worker of backend got the message ");
        var mmsFrom = Serializers.deserialize(message.getBody(), MessageModel.class);
        channelMessageConsumer.basicQos(1);
        try {
            backendRabbitHandler.processMsgFromRabbit(message);
            channelMessageConsumer.basicAck(tag, false);
        } catch (UnknownHostException ex) {
            log.error("Problems with the Internet or URLs : \n{}", Arrays.toString(ex.getStackTrace()));
            channelMessageConsumer.basicReject(tag, true);
        } catch (EventException | ClassCastException | IllegalArgumentException | IOException ex) {
            log.error("Impossible to handle the message or parse the source url. Sending to to deadLetterQueue {}\n{}", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
            sendMessageToRabbit(rabbitProperties.getBackProducerExchange(), rabbitProperties.getBackProduceQueue(), Serializers.serialize(requestHandler.errorMessageForFront(mmsFrom).get()));
            channelMessageConsumer.basicReject(tag, false);
        } catch (Exception ex) {
            if (isRedelivered) {
                log.error("Fail with handling the message at the second time. Now it is going to deadLetterQueue. {}, {}\n{}", ex.getCause(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
                channelMessageConsumer.basicReject(tag, false);
                try {
                    sendMessageToRabbit(rabbitProperties.getBackProducerExchange(), rabbitProperties.getBackProduceQueue(), Serializers.serialize(requestHandler.errorMessageForFront(mmsFrom).get()));
                } catch (EventException e) {
                    log.error("Impossible to send error message for user: {}", e.getMessage());
                }
            } else {
                log.error("Fail with handling the message because of: {}. Trying to requeue", ex.getMessage());
                channelMessageConsumer.basicReject(tag, true);
            }
        }
    }

    private void sendMessageToRabbit(String exchange, String queue, byte[] msg) {
        template.convertAndSend(exchange, queue,
                MessageBuilder.withBody(msg)
                        .setContentType(String.valueOf(MessageProperties.PERSISTENT_TEXT_PLAIN))
                        .build());
    }
}
