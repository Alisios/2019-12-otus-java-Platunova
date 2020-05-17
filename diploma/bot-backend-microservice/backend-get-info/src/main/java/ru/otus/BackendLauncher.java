package ru.otus;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.BackendService;
import ru.otus.backend.BackendServiceImpl;
import ru.otus.backend.eventApi.Concert;
import ru.otus.backend.eventApi.MonitoredEvent;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
import ru.otus.backend.handlers.RequestHandler;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;
import ru.otus.helpers.MessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BackendLauncher {
    private static Logger logger = LoggerFactory.getLogger(BackendLauncher.class);

    private static final String FRONT_PRODUCER_EXCHANGE = "front_producer_exchange";
    private static final String BACK_PRODUCER_EXCHANGE = "back_producer_exchange";
    private static final String DB_EXCHANGE = "db_exchange";

    private static final String FRONT_PRODUCER_QUEUE = "front_producer_queue";
    private static final String BACK_PRODUCER_QUEUE = "back_producer_queue";
    private static final String DB_QUEUE = "db_queue";

    private static final int MSG_HANDLER_THREAD_LIMIT = 1;
    private static final int MESSAGE_QUEUE_SIZE = 200_000;
    private final BlockingQueue<MessageForFront> messageQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);
    private Connection connectionMessageConsumer;
    private Channel channelMessageConsumer;

    private final AtomicBoolean runFlag = new AtomicBoolean(true);
    private MonitoredEvent monitoredEvent;
    private ConnectionFactory factory;
    private RequestHandler requestHandler ;

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {


        BackendLauncher backendLauncher = new BackendLauncher();
        backendLauncher.start();

        Thread.sleep(240_000);
        backendLauncher.shutDownBackend();
        Thread.sleep(2_000);
    }

    private final ExecutorService backMsgProducer = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("backend-message-producer-thread");
        return thread;
    });

    private final ExecutorService backMsgHandler = Executors.newFixedThreadPool(MSG_HANDLER_THREAD_LIMIT,
            new ThreadFactory() {
                private final AtomicInteger threadNameSeq = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName("backend-message-handler-thread-" + threadNameSeq.incrementAndGet());
                    return thread;
                }
            });

    private BackendLauncher(){
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        runFlag.set(true);
        HtmlParser htmlParser = new HtmlParserKassirRu();
         monitoredEvent = new Concert(htmlParser);
        BackendService backendService = new BackendServiceImpl(monitoredEvent);
        requestHandler = new RequestHandler(backendService);
        try {
        connectionMessageConsumer = factory.newConnection();
            channelMessageConsumer = connectionMessageConsumer.createChannel();
            channelMessageConsumer.exchangeDeclare(FRONT_PRODUCER_EXCHANGE, "direct");
            channelMessageConsumer.queueDeclare(FRONT_PRODUCER_QUEUE, true, false, false, null);
            channelMessageConsumer.queueBind(FRONT_PRODUCER_QUEUE, FRONT_PRODUCER_EXCHANGE, FRONT_PRODUCER_QUEUE);
            channelMessageConsumer.basicQos(1);
        }catch (Exception e) {
            logger.error("Error when initializing RabbitMq connection and channel:  {}", Arrays.toString(e.getStackTrace())) ;
        }
       // initiateForChecking().forEach(dbService::saveUser); //для проверки мониторинга
    }

    private void start(){
        backMsgHandler.submit(this::processMsgFromFront);
        backMsgProducer.submit(this::processMsgToFront);
    }

    private void processMsgFromFront() {
        logger.info("processMsgHandler started");
        logger.info(" [*] Waiting for messages from FrontEnd. To exit press CTRL+C");
                try {
                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                         MessageModel mmsFrom = Serializers.deserialize(delivery.getBody(), MessageModel.class);
                        try {
                            if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_EVENT_INFO.getValue())) {
                                requestHandler.getEventData(mmsFrom).ifPresentOrElse(messageQueue::add, () -> {
                                    logger.error("Fail to process Message {} ", Serializers.deserialize(mmsFrom.getPayload(), Message.class).getText());
                                });}
                            else if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_TICKET_INFO.getValue())) {
                                    CallbackQuery s = Serializers.deserialize(mmsFrom.getPayload(), CallbackQuery.class);
                                    requestHandler.getTicketData(mmsFrom).ifPresentOrElse(messageQueue::add, () -> {
                                    logger.error("Fail to process Message {} ", s.getMessage().getText());
                                });

                                if (s.getData().equals("NOTIFY")) //поменять под новую логику без edit
                                     requestHandler.switchingOnMonitoring(s.getMessage()).ifPresentOrElse((messageToDb) -> {
                                                putToQueue(Serializers.serialize(messageToDb), DB_EXCHANGE, DB_QUEUE); },
                                            () -> {logger.info("Fail to send to DB for saving  {} ", s.getMessage());});
                             }

                        } finally {
                            channelMessageConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }
                    };
                    channelMessageConsumer.basicConsume(FRONT_PRODUCER_QUEUE, false, deliverCallback, consumerTag -> {
                    });
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    logger.info("Connection of processMsgFromFront is closed");
                }
    }

    private Boolean putToQueue(byte [] message, String exchange, String queue) {
        logger.info("put to rabbitMq exchange: {} queue: {}",exchange, queue);
        if (runFlag.get()) {
           // MessageProperties m = m.
            try (Channel channelProducer = connectionMessageConsumer.createChannel()) {
                channelProducer.exchangeDeclare(exchange, "direct");
                channelProducer.basicPublish("", queue,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                       message);
                 return true; }
            catch (IOException | TimeoutException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return false;
    }

    private void processMsgToFront() {
        logger.info("backMsgProducer started");
        while (runFlag.get() || !messageQueue.isEmpty()) {
            try {
               MessageForFront message = messageQueue.take(); //блокируется
                if (message.getMessageType().getValue().equals("shutdown")) {
                    logger.info("received the stop message");
                }else {
                    putToQueue(Serializers.serialize(message), BACK_PRODUCER_EXCHANGE, BACK_PRODUCER_QUEUE);
                }
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void shutDownBackend() throws IOException, TimeoutException {
        logger.info("now backServer is shutting down messages");
       // logger.info("Кэш: {}",monitoredEvent.getCacheMap().toString());
        messageQueue.add(new MessageForFront(MessageType.SHUTDOWN_MESSAGE, null, 0L, 0));
        runFlag.set(false);
        if (connectionMessageConsumer!=null)
            connectionMessageConsumer.close();
        backMsgHandler.shutdown();
        backMsgProducer.shutdown();
    }

    private static List<User> initiateForChecking(){
        List<User> userList = new ArrayList<User>(List.of(
                new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, 6,5).getTime()),
//                new User(202812830, new ConcertModel("Aerosmith (Аэросмит)",
//                        "30 Июльчт 19:00",
//                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
//                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
//                        new GregorianCalendar(2019, 4,23).getTime()),
                new User(202812830, new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20"),
                        new GregorianCalendar(2020, 5,19).getTime())));
       // userList.get(2).setMonitoringSuccessful(true);
        userList.forEach(user->user.getConcert().setOwner(user));
        return userList;
    }
}


