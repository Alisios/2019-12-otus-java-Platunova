package ru.otus;

import com.rabbitmq.client.*;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.eventApi.Concert;
import ru.otus.backend.eventApi.MonitoredEvent;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("имитирует сообщения от фронтенда (для проверки rabbitMq")
class BackendLauncherTest {
    private static Logger logger = LoggerFactory.getLogger(BackendLauncherTest.class);

    private static final String FRONT_PRODUCER_EXCHANGE = "front_producer_exchange";
    private static final String BACK_PRODUCER_EXCHANGE = "back_producer_exchange";
    private static final String DB_EXCHANGE = "db_exchange";

    private static final String FRONT_PRODUCER_QUEUE = "front_producer_queue";
    private static final String PRODUCER_MONITORING_QUEUE = "monitoring_queue";

    private static final String BACK_PRODUCER_QUEUE = "back_producer_queue";
    public static void main(String[] args) throws IOException, InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String message = "hello from test";
        List<User> users = new ArrayList<>();
        User user2 = new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                "12 Июльвс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                new GregorianCalendar(2019, 6,5).getTime());
        User user3= new User(202812830, new ConcertModel("Aerosmith (Аэросмит)",
                "30 Июльчт 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                new GregorianCalendar(2019, 4,23).getTime());
        users.add(user2);
        users.add(user3);

        try (Connection connection = factory.newConnection();
             Channel channelProducer = connection.createChannel()) {
            channelProducer.exchangeDeclare(FRONT_PRODUCER_EXCHANGE, "direct");
            for (User u : users) {
                channelProducer.basicPublish("", FRONT_PRODUCER_QUEUE,
                        MessageProperties.PERSISTENT_TEXT_PLAIN, //сообщения не будут утеряны в случае падения RMQ
                        Serializers.serialize(u));
                Thread.sleep(3_000);
                logger.info("Message is sent in cosumer queue: № {}", u);
            }
        }
         catch (IOException | TimeoutException e) {
            logger.error(e.getMessage(), e);
        }

//
//        try {
//            Connection connectionMonitoringConsumer;
//            connectionMonitoringConsumer = factory.newConnection();
//            Channel channelConsumer = connectionMonitoringConsumer.createChannel();
//            channelConsumer.exchangeDeclare(BACK_PRODUCER_EXCHANGE, "direct");
//            channelConsumer.queueDeclare(BACK_PRODUCER_QUEUE, true, false, false, null);
//            channelConsumer.queueBind(BACK_PRODUCER_QUEUE, BACK_PRODUCER_EXCHANGE, BACK_PRODUCER_QUEUE);
//            logger.info(" [*] Waiting results of monitoring in front. To exit press CTRL+C");
//            channelConsumer.basicQos(1);
//            try {
//                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                    MessageForFront fromBack = Serializers.deserialize(delivery.getBody(), MessageForFront.class);
//                    try {
//                        logger.info("The message {} is  sent to User! Ehuu!", Serializers.deserialize(fromBack.getPayload(), String.class));
//
//                    } finally {
//                        // logger.info("Message is processed ");
//                        channelConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                    }
//                };
//                channelConsumer.basicConsume(BACK_PRODUCER_QUEUE, false, deliverCallback, consumerTag -> {
//                });
//            } catch (Exception ex) {
//                logger.error(ex.getMessage(), ex);
//                connectionMonitoringConsumer.close();
//            }
//        }
//        catch (IOException | TimeoutException ex){
//            logger.error(ex.getMessage(), ex);
//        }
    }

}

