package ru.otus;

import com.rabbitmq.client.*;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.otus.backend.model.User;

import ru.otus.helpers.MessageModel;

import ru.otus.helpers.Serializers;

import java.io.IOException;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("имитирует прием сообщений в бд (для проверки rabbitMq")
class DBimitation {
    private static Logger logger = LoggerFactory.getLogger(DBimitation.class);

    private static final String DB_EXCHANGE = "db_exchange";
    private static final String DB_QUEUE = "db_queue";


    public static void main(String[] args)  {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            Connection connectionMonitoringConsumer;
            connectionMonitoringConsumer = factory.newConnection();
            Channel channelConsumer = connectionMonitoringConsumer.createChannel();
            channelConsumer.exchangeDeclare(DB_EXCHANGE, "direct");
            channelConsumer.queueDeclare(DB_QUEUE, true, false, false, null);
            channelConsumer.queueBind(DB_QUEUE, DB_EXCHANGE, DB_QUEUE);
            logger.info(" [*] Waiting results of monitoring in db. To exit press CTRL+C");
            channelConsumer.basicQos(1);
            try {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    MessageModel fromBack = Serializers.deserialize(delivery.getBody(), MessageModel.class);
                    try {
                        logger.info("The user {} is  deleted in base!", Serializers.deserialize(fromBack.getPayload(), User.class));

                    } finally {
                        logger.info("Message is processed ");
                        channelConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                };
                channelConsumer.basicConsume(DB_QUEUE, false, deliverCallback, consumerTag -> {
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                connectionMonitoringConsumer.close();
            }
        }
        catch (IOException | TimeoutException ex){
            logger.error(ex.getMessage(), ex);
        }

    }

}

