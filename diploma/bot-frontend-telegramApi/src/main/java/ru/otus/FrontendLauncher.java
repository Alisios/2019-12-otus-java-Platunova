package ru.otus;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;
import ru.otus.telegramApi.Bot;
import ru.otus.telegramApi.TelegramService;
import ru.otus.telegramApi.TelegramServiceImpl;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FrontendLauncher {
    private static Logger logger = LoggerFactory.getLogger(FrontendLauncher.class);
    private static final String FRONT_PRODUCER_EXCHANGE = "front_producer_exchange";
    private static final String BACK_PRODUCER_EXCHANGE = "back_producer_exchange";
    private static final String FRONT_PRODUCER_QUEUE = "front_producer_queue";
    private static final String BACK_PRODUCER_QUEUE = "back_producer_queue";

    private final AtomicBoolean runFlag = new AtomicBoolean(true);
    private static final int MSG_HANDLER_THREAD_LIMIT = 1;
    private static final int MESSAGE_QUEUE_SIZE = 200_000;
    private final BlockingQueue<MessageModel> messageQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);

    private ConnectionFactory factory;
    private Connection connectionMessageConsumer;
    private TelegramService telegramService;

    private final ExecutorService frontMsgProducer = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("frontend-message-producer-thread");
        return thread;
    });

    private final ExecutorService frontMsgHandler = Executors.newFixedThreadPool(MSG_HANDLER_THREAD_LIMIT,
            new ThreadFactory() {
                private final AtomicInteger threadNameSeq = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName("frontend-message-handler-thread-" + threadNameSeq.incrementAndGet());
                    return thread;
                }
            });

    public static void main(String[] args) throws InterruptedException, IOException {
        ApiContextInitializer.init();
        Bot ticket_bot = new Bot("TicketMonitoringBot", "1187602924:AAEB5MoSsUNDmbaGXWAZAdAG2Ka5qe6f59A");
        TelegramService telegramService = new TelegramServiceImpl(ticket_bot);
        FrontendLauncher frontendLauncher = new FrontendLauncher(telegramService);
        ticket_bot.setMessageQueue(frontendLauncher.getMessageQueue());

        frontendLauncher.start();

        System.getProperties().put( "proxySet", "true" );
        System.getProperties().put( "socksProxyHost", "127.0.0.1" );
        System.getProperties().put( "socksProxyPort", "9150" );

        ticket_bot.botConnect();

        Thread.sleep(240_000);
        frontendLauncher.shutDownFrontend();
        Thread.sleep(2_000);
    }

    private FrontendLauncher(TelegramService telegramService){
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.telegramService =telegramService;
    }

    private void start(){
        frontMsgHandler.submit(this::processMsgFromBack); //принимает обработанные сообщения с бека и посылает пользователю
        frontMsgProducer.submit(this::processMsgToBack);  //посылает присланные пользователем запросы в бек
    }

    private void processMsgFromBack(){
        logger.info("frontMsgHandler started");
        try {
            connectionMessageConsumer = factory.newConnection();
            Channel channelConsumer = connectionMessageConsumer.createChannel();
            channelConsumer.exchangeDeclare(BACK_PRODUCER_EXCHANGE, "direct");
            channelConsumer.queueDeclare(BACK_PRODUCER_QUEUE, true, false, false, null);
            channelConsumer.queueBind(BACK_PRODUCER_QUEUE, BACK_PRODUCER_EXCHANGE, BACK_PRODUCER_QUEUE);
            logger.info(" [*] Waiting results from back. To exit press CTRL+C");
            channelConsumer.basicQos(1);


            try {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                    MessageForFront fromBack = Serializers.deserialize(delivery.getBody(), MessageForFront.class);
                    try {
                        if (fromBack.getMessageType().getValue().equals(MessageType.GET_EVENT_INFO.getValue())) {
                            telegramService.sendMsg(fromBack);
                        }
                        else if (fromBack.getMessageType().getValue().equals(MessageType.GET_TICKET_INFO.getValue())) {
                            telegramService.sendMsgQuery(fromBack);
                        }
                        else if (fromBack.getMessageType().getValue().equals(MessageType.NOTIFY.getValue())){
                            telegramService.sendNotifyingMsg(fromBack);
                        }
                        logger.info("The message {} is  sent to User!", Serializers.deserialize(fromBack.getPayload(), String.class));

                    } finally {
                        channelConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                };
                channelConsumer.basicConsume(BACK_PRODUCER_QUEUE, false, deliverCallback, consumerTag -> {
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                connectionMessageConsumer.close();
            }
        }
        catch (IOException | TimeoutException ex){
            logger.error(ex.getMessage(), ex);
        }

    }
    private void processMsgToBack()  {
        logger.info("FrontendProducer started");
        while (runFlag.get() || !messageQueue.isEmpty()) {
        try{
            MessageModel message = messageQueue.take(); //блокируется
            if (message.getMessageType().getValue().equals("shutdown")) {
                logger.info("messageQueueDB received the stop message");
            }else{
                try (Connection connection = factory.newConnection();
                Channel channelProducer = connection.createChannel()) {
                channelProducer.exchangeDeclare(FRONT_PRODUCER_EXCHANGE, "direct");
                channelProducer.basicPublish("", FRONT_PRODUCER_QUEUE,
                        MessageProperties.PERSISTENT_TEXT_PLAIN, //сообщения не будут утеряны в случае падения RMQ
                        Serializers.serialize(message));
                logger.info("Message {} is sent in back. ", message.getMessageType());
                } catch (IOException | TimeoutException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
     }
    }

    private void shutDownFrontend() throws IOException {
        logger.info("now frontendServer is shutting down");
        messageQueue.add(new MessageModel(MessageType.SHUTDOWN_MESSAGE, null));
        runFlag.set(false);
        if (connectionMessageConsumer!=null)
            connectionMessageConsumer.close();
        frontMsgHandler.shutdown();
        frontMsgProducer.shutdown();

    }

    private BlockingQueue<MessageModel> getMessageQueue(){
        return messageQueue;
    }
}

