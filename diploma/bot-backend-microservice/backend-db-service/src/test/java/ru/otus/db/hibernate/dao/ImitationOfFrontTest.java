package ru.otus.db.hibernate.dao;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class ImitationOfFrontTest {
    private static Logger logger = LoggerFactory.getLogger(ImitationOfFrontTest.class);

    private static final String BACK_PRODUCER_EXCHANGE = "back_producer_exchange";
    private static final String BACK_PRODUCER_QUEUE = "back_producer_queue";
    private static final String MONITORING_EXCHANGE = "monitoring_exchange";
    private static final String MONITORING_QUEUE = "monitoring_queue";
    private static final String DB_EXCHANGE = "db_exchange";
    private static final String DB_QUEUE = "db_queue";


    ConnectionFactory factory;
    Connection connection;
    Channel channelConsumer;

    private ImitationOfFrontTest() {
        try {
            factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channelConsumer = connection.createChannel();
            channelConsumer = connection.createChannel();
            channelConsumer.exchangeDeclare(BACK_PRODUCER_EXCHANGE, "direct");
            channelConsumer.queueDeclare(BACK_PRODUCER_QUEUE, true, false, false, null);
            channelConsumer.queueBind(BACK_PRODUCER_QUEUE, BACK_PRODUCER_EXCHANGE, BACK_PRODUCER_QUEUE);
            channelConsumer.basicQos(1);
        } catch (IOException | TimeoutException e){
            logger.error("Error when creating factory and connection in Test {} " , e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {

        ImitationOfFrontTest backendLauncherTest = new ImitationOfFrontTest();
        backendLauncherTest.start();
        backendLauncherTest.sendToQueue();
        Thread.sleep(60_000);
        backendLauncherTest.shutDown();
        Thread.sleep(2_000);
    }

    private final ExecutorService test = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("test-thread");
        return thread;
    });

    private void start() {
        test.submit(this::processTest);
    }

    private void shutDown() throws IOException, TimeoutException {
        channelConsumer.close();
        connection.close();
        test.shutdown();
    }


    private void sendToQueue() {
        String message = "Элизиум";
        MessageForFront messageModel = new MessageForFront(MessageType.ADMIN_GET_USERS, Serializers.serialize(message), 0,0);
        try (Channel channelProducer = connection.createChannel()) {
            channelProducer.exchangeDeclare(DB_EXCHANGE, "direct");
            channelProducer.basicPublish("", DB_QUEUE,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, //сообщения не будут утеряны в случае падения RMQ
                    Serializers.serialize(messageModel));

            //Thread.sleep(3_000);
            logger.info("Message is sent in cosumer queue: № {}", message);
        } catch (IOException | TimeoutException e) {
            logger.error(e.getMessage(), e);
        }
    }
    private void processTest () {
        logger.info(" [*] Waiting results of monitoring in front. To exit press CTRL+C");
        try {
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                MessageModel db = Serializers.deserialize(delivery.getBody(), MessageModel.class);
                try {
                    logger.info("The message {} is  sent to User! Ehuu!", Serializers.deserialize(db.getPayload(), List.class));

                } finally {
                    // logger.info("Message is processed ");
                    channelConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            channelConsumer.basicConsume(BACK_PRODUCER_QUEUE, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
