package ru.otus.listeners;

import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.otus.backend.model.User;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.db.service.DBServiceUser;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.util.List;

@Component
@Slf4j
public class DbHandlerImpl implements DbHandler {

    private final DBServiceUser dbService;
    private final AmqpTemplate template;
    private final RabbitMQProperties rabbitMQProperties;

    @Autowired
    DbHandlerImpl(@Qualifier("DBServiceUserJPA") DBServiceUser dbService, AmqpTemplate template, @Qualifier("rabbitMQProperties") RabbitMQProperties rabbitMQProperties) {
        this.dbService = dbService;
        this.template = template;
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void processMsgFromRabbit(Message message) {
        try {
            MessageModel mmsFrom = Serializers.deserialize(message.getBody(), MessageModel.class);
            User user;
            switch (mmsFrom.getMessageType().getValue()) {
                case "deleteUser":
                    user = Serializers.deserialize(mmsFrom.getPayload(), User.class);
                    dbService.delete(user.getId());
                    log.info("user is deleted!");
                    break;

                case "saveUser":
                    user = Serializers.deserialize(mmsFrom.getPayload(), User.class);
                    dbService.saveUser(user);
                    log.info("user is saved!");
                    break;

                case "monitoring":
                    List<User> list = dbService.getAllUsers();
                    sendMessageToRabbit(rabbitMQProperties.getMonitoringExchange(), rabbitMQProperties.getMonitoringQueue(),
                            Serializers.serialize(new MessageModel(MessageType.GET_MONITORING_RESULT, Serializers.serialize(list))));
                    log.info("Sent message with GET_MONITORING_RESULT to monitoring queue");
                    break;

                case "deleteUserByAdmin":
                    long id = Serializers.deserialize(mmsFrom.getPayload(), Long.class);
                    log.info("Message with DELETE_USER_BY_ADMIN is received, id {}", id);
                    dbService.delete(id);
                    log.info("user with id {} is deleted by admin!", id);
                    break;

                case "saveUserByAdmin":
                    user = Serializers.deserialize(mmsFrom.getPayload(), User.class);
                    dbService.saveUser(user);
                    log.info("user {} is saved by admin!", user);
                    break;

                case "admin_get_users":
                    List<User> list2 = dbService.getAllUsers();
                    sendMessageToRabbit(rabbitMQProperties.getBackProducerWebExchange(), rabbitMQProperties.getBackProducerToWebQueue(),
                            Serializers.serialize(new MessageForFront(MessageType.ADMIN_GET_USERS, Serializers.serialize(list2), 0L, 0)));
                    log.info("Sent message with ADMIN_GET_USERS to backProducer queue");
                    break;
            }
        } catch (ClassCastException ex1) {
            log.error("Problems with parsing message {}\n{}", ex1.getMessage(), ex1.getStackTrace());
            throw new ClassCastException("Problems with parsing message: " + ex1.getMessage());
        } catch (AmqpException ex2) {
            log.error("Problems with RabbitMq. Impossible to send message of {} type from Db{}\n{}", Serializers.deserialize(message.getBody(), MessageModel.class).getMessageType().getValue(), ex2.getMessage(), ex2.getStackTrace());
            throw new AmqpException("Problems with RabbitMq. Impossible to send message of " + Serializers.deserialize(message.getBody(), MessageModel.class).getMessageType().getValue() + " type from Db: " + ex2.getMessage());
        } catch (RuntimeException ex3) {
            log.error("Problems with processing the message {}  in DB {}\n{}", Serializers.deserialize(message.getBody(), MessageModel.class).getMessageType().getValue(), ex3.getMessage(), ex3.getStackTrace());
            throw new RuntimeException("Problems with processing the message" + Serializers.deserialize(message.getBody(), MessageModel.class).getMessageType().getValue() + " type from Db: " + ex3.getMessage());
        }
    }

    private void sendMessageToRabbit(String exchange, String queue, byte[] msg) {
        template.convertAndSend(exchange, queue,
                MessageBuilder.withBody(msg)
                        .setContentType(String.valueOf(MessageProperties.PERSISTENT_TEXT_PLAIN))
                        .build());
    }
}

