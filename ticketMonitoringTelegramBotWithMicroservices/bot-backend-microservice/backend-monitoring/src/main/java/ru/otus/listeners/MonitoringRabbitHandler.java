package ru.otus.listeners;

import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.otus.backend.eventApi.MessageFormer;
import ru.otus.backend.MonitoringService;
import ru.otus.backend.model.User;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.List;

/**
 * проверка срабатывания события, если да - уведомление пользователю и удаление из БД
 */

@Component
@Slf4j
public class MonitoringRabbitHandler implements RabbitHandler {

    private final MonitoringService monitoringService;
    private final RabbitMQProperties rabbitMQProperties;
    private final AmqpTemplate template;

    @Autowired
    public MonitoringRabbitHandler(@Qualifier("rabbitMQProperties") RabbitMQProperties rabbitMQProperties, @Qualifier("userMonitoringService") MonitoringService monitoringService, AmqpTemplate template) {
        this.rabbitMQProperties = rabbitMQProperties;
        this.template = template;
        this.monitoringService = monitoringService;
    }

    @Override
    public void processMsgFromRabbit(Message msg) throws IOException {

        MessageModel message = Serializers.deserialize(msg.getBody(), MessageModel.class);
        List<User> userList = Serializers.deserialize(message.getPayload(), List.class);
        if (userList.size() != 0) {
            monitoringService.getMonitoringResult(userList);
            userList.stream().filter(monitoringService::checkIfUserShouldBeNotified).forEach(user -> {

                MessageFormer.formMessageAboutMonitoringResults(user).ifPresentOrElse((messageToFront) -> {
                    sendMessageToRabbit(rabbitMQProperties.getBackProducerExchange(), rabbitMQProperties.getBackProduceQueue(),
                            Serializers.serialize(messageToFront));

                    sendMessageToRabbit(rabbitMQProperties.getDbExchange(), rabbitMQProperties.getDbQueue(),
                            Serializers.serialize(new MessageModel(MessageType.DELETE_USER, Serializers.serialize(user))));

                }, () -> {
                    log.error("Fail to send monitoring result about User:  {} ", user);
                });
            });
        }
    }

    private void sendMessageToRabbit(String exchange, String queue, byte[] msg) {
        log.info("Send message to {} ", queue);
        template.convertAndSend(exchange, queue,
                MessageBuilder.withBody(msg)
                        .setContentType(String.valueOf(MessageProperties.PERSISTENT_TEXT_PLAIN))
                        .build());
    }

}

