package ru.otus.backend.listeners;


import com.rabbitmq.client.MessageProperties;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import ru.otus.backend.handlers.RequestHandler;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;


@Component
public class BackendRabbitHandler implements RabbitHandler {

    private final RequestHandler requestHandler;
    private final AmqpTemplate template;
    private final RabbitMQProperties rabbitProperties;

    @Autowired
    public BackendRabbitHandler(RequestHandler requestHandler, AmqpTemplate template, @Qualifier("rabbitMQProperties") RabbitMQProperties rabbitProperties) {
        this.requestHandler = requestHandler;
        this.template = template;
        this.rabbitProperties = rabbitProperties;
    }

    @Override
    public void processMsgFromRabbit(Message message) throws IOException {
        var mmsFrom = Serializers.deserialize(message.getBody(), MessageModel.class);
        if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_EVENT_INFO.getValue())) {
            sendMessageToRabbit(rabbitProperties.getBackProducerExchange(), rabbitProperties.getBackProduceQueue(),
                    Serializers.serialize(requestHandler.getEventData(mmsFrom).orElseThrow()));
        } else if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_TICKET_INFO.getValue())) {
            CallbackQuery s = Serializers.deserialize(mmsFrom.getPayload(), CallbackQuery.class);
            sendMessageToRabbit(rabbitProperties.getBackProducerExchange(), rabbitProperties.getBackProduceQueue(),
                    Serializers.serialize(requestHandler.getTicketData(mmsFrom).orElseThrow()));

            if (s.getData().equals("NOTIFY"))
                sendMessageToRabbit(rabbitProperties.getDbExchange(), rabbitProperties.getDbQueue(),
                        Serializers.serialize(requestHandler.switchingOnMonitoring(s.getMessage()).orElseThrow()));
        }
    }

    private void sendMessageToRabbit(String exchange, String queue, byte[] msg) {
        template.convertAndSend(exchange, queue,
                MessageBuilder.withBody(msg)
                        .setContentType(String.valueOf(MessageProperties.PERSISTENT_TEXT_PLAIN))
                        .build());
    }
}
