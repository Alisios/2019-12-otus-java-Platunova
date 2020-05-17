package ru.otus;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.MonitoringResultService;
import ru.otus.backend.MonitoringResultsServiceImpl;
import ru.otus.backend.eventApi.ConcertMonitoring;
import ru.otus.backend.eventApi.MonitoringService;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;
import ru.otus.helpers.MessageType;
import ru.otus.scheduler.QuartzScheduler;
import ru.otus.scheduler.SchedulerService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//поправить время тайминга!
public class MonitoringLauncher {
    private static Logger logger = LoggerFactory.getLogger(MonitoringLauncher.class);

    private static final String BACK_PRODUCER_EXCHANGE = "back_producer_exchange";

    private static final String DB_EXCHANGE = "db_exchange";
    private static final String DB_QUEUE = "db_queue";
    private static final String BACK_PRODUCER_QUEUE = "back_producer_queue";
    private static final String MONITORING_EXCHANGE = "monitoring_exchange";
    private static final String MONITORING_QUEUE = "monitoring_queue";

    private static final int MSG_HANDLER_THREAD_LIMIT = 1;

    private Connection connectionMonitoring;
    private Channel channelConsumer = null;

    private MonitoringService monitoringService;
    private MonitoringResultService monitoringResultService;
    private SchedulerService quartzScheduler;
    private ConnectionFactory factory;
    private AtomicBoolean runQueueKey = new AtomicBoolean(false);

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {

        MonitoringLauncher monitoringLauncher = new MonitoringLauncher();
        monitoringLauncher.start();
        Thread.sleep(180_000);
        monitoringLauncher.shutDownMonitoring();
        Thread.sleep(2_000);
    }


    private final ExecutorService backMsgMonitoringHandler = Executors.newFixedThreadPool(MSG_HANDLER_THREAD_LIMIT,
            new ThreadFactory() {
                private final AtomicInteger threadNameSeq = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName("backend-monitoring-thread-" + threadNameSeq.incrementAndGet());
                    return thread;
                }
            });

    private MonitoringLauncher()  {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        HtmlParser htmlParser = new HtmlParserKassirRu();
        monitoringResultService = new MonitoringResultsServiceImpl();
        monitoringService = new ConcertMonitoring(htmlParser);
        quartzScheduler = new QuartzScheduler(DB_EXCHANGE, DB_QUEUE);
        try {
            connectionMonitoring = factory.newConnection();
            channelConsumer = connectionMonitoring.createChannel();
            channelConsumer.exchangeDeclare(MONITORING_EXCHANGE, "direct");
            channelConsumer.queueDeclare(MONITORING_QUEUE, true, false, false, null);
            channelConsumer.queueBind(MONITORING_QUEUE, MONITORING_EXCHANGE, MONITORING_QUEUE);
            channelConsumer.basicQos(1);
            runQueueKey.set(true);
        }
        catch (IOException | TimeoutException ex) {
            logger.error("Error with RabbitMQ connection {}, {}", ex.getMessage(), ex);
        }
    }

    private void start(){
        quartzScheduler.startMonitoring();
        backMsgMonitoringHandler.submit(this::processMsgMonitoring);
    }

    private void processMsgMonitoring() {
        logger.info("processMsgMonitoring started");
        logger.info(" [*] Waiting requests to DB. To exit press CTRL+C");
            try {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    MessageModel message = Serializers.deserialize(delivery.getBody(), MessageModel.class);
                    try {
                        List<User> userList = Serializers.deserialize(message.getPayload(), List.class);
                        if (userList.size() != 0)
                            for (User user : userList) {
                                if (monitoringService.checkingTickets(user)) {
                                    monitoringResultService.getMonitoringResult(user).ifPresentOrElse((messageToFront)->{
                                        if (putToQueue(messageToFront, BACK_PRODUCER_EXCHANGE, BACK_PRODUCER_QUEUE))
                                            putToQueue(new MessageModel(MessageType.DELETE_USER, Serializers.serialize(user)),DB_EXCHANGE, DB_QUEUE);
                                    },
                                            () -> { logger.info("Fail to send monitoring result about User:  {} ", user); });
                                }
                            }
                        //подумать когда лучше отмечать сделанным задание
                        channelConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                    catch (Exception ex){
                        logger.error(ex.getMessage(), ex);
                       // logger.error("Fail with {} message with User {} for DB id redirected to another handler", message.getMessageType().getValue(),  Serializers.deserialize(message.getPayload(), User.class) );
                    }
                };
                channelConsumer.basicConsume(MONITORING_QUEUE, false, deliverCallback, consumerTag -> {
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
    }

    private Boolean putToQueue(MessageModel message, String exchange, String queue) {
        logger.info("put the message of Type {} to rabbitMq exchange: {} queue: {}",message.getMessageType(), exchange, queue);
        if (runQueueKey.get()) {
            try (Channel channelProducer = connectionMonitoring.createChannel()) {
                channelProducer.exchangeDeclare(exchange, "direct");
                channelProducer.basicPublish("", queue,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        Serializers.serialize(message));
                return true;
            } catch (IOException | TimeoutException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return false;
    }

    private void shutDownMonitoring() throws IOException, TimeoutException {
        logger.info("now monitoring server is shutting down");
        runQueueKey.set(false);
        quartzScheduler.stopMonitoring();
        if (channelConsumer != null)
            channelConsumer.close();
        if (connectionMonitoring != null)
            connectionMonitoring.close();
        backMsgMonitoringHandler.shutdown();
    }
}


