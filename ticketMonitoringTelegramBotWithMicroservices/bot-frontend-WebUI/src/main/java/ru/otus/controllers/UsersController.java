package ru.otus.controllers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.backend.model.User;
import ru.otus.configuration.RabbitMQProperties;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final SimpMessagingTemplate template;
    private static final String BACK_PRODUCER_TO_WEB_QUEUE = "back_producer_to_web_queue";

    private final AmqpTemplate templateRab;

    private final RabbitMQProperties rabbitMQProperties;

    @GetMapping({"/users"})
    public String view() {
        return "users.html";
    }

    @GetMapping({"/create"})
    public String create() {
        return "createUser.html";
    }

    @MessageMapping({"/users"})
    void request() {
        sendMessageToRabbit(MessageType.ADMIN_GET_USERS, null);
        log.info("is going to send from request users");
    }

    @MessageMapping({"/create"})
    RedirectView requestSave(User user) {
        sendMessageToRabbit(MessageType.SAVE_USER_BY_ADMIN, Serializers.serialize(user));
        log.info("create user {}", user);
        this.template.convertAndSend("/topic/create", user);
        return new RedirectView("/users", true);
    }

    @MessageMapping(value = "/delete")
    void requestDelete(Long id) {
        sendMessageToRabbit(MessageType.DELETE_USER_BY_ADMIN, Serializers.serialize(id));
        log.info("is going to send from request delete id:{}", id);
    }

    @RabbitListener(queues = BACK_PRODUCER_TO_WEB_QUEUE, ackMode = "MANUAL")
    public void webHandler(Message msg, Channel channelConsumer, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info(" webHandler got message from DB");
        channelConsumer.basicQos(1);
        try {
            MessageModel message = Serializers.deserialize(msg.getBody(), MessageModel.class);
            List<User> users = Serializers.deserialize(message.getPayload(), List.class);
            this.template.convertAndSend("/topic/users", users);
            channelConsumer.basicAck(tag, false);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            channelConsumer.basicReject(tag, false);
        }
    }

    private void sendMessageToRabbit(MessageType messageType, byte[] payload) {
        templateRab.convertAndSend(rabbitMQProperties.getDbExchange(), rabbitMQProperties.getDbQueue(),
                MessageBuilder.withBody(Serializers.serialize(new MessageModel(messageType, payload)))
                        .setContentType(String.valueOf(MessageProperties.PERSISTENT_TEXT_PLAIN))
                        .build());
    }

}
