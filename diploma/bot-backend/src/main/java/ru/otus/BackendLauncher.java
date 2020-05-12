package ru.otus;

import com.rabbitmq.client.*;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.BackendService;
import ru.otus.backend.BackendServiceImpl;
import ru.otus.db.hibernate.HibernateUtils;
import ru.otus.db.hibernate.dao.UserDao;
import ru.otus.db.hibernate.dao.UserDaoHibernate;
import ru.otus.db.hibernate.sessionmanager.SessionManager;
import ru.otus.db.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.db.service.DBServiceUser;
import ru.otus.db.service.DbServiceUserImpl;
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//расставить bind!!!!!!!!!
// не забыть перенести мониторинг
//исправить тесты и комментарии!!!!
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
    private final BlockingQueue<MessageModel> messageQueueDB = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);
    private final BlockingQueue <MessageModel> usersMonitoringQueue = new ArrayBlockingQueue<>(MESSAGE_QUEUE_SIZE);

    private Connection connectionMessageConsumer;
    private Connection connectionDBConsumer;

    private final AtomicBoolean runFlag = new AtomicBoolean(true);
    private MonitoredEvent monitoredEvent;
    private SessionFactory sessionFactory;
    private SessionManager sessionManager;
    private ConnectionFactory factory;
    private RequestHandler requestHandler ;
    private DBServiceUser dbService;
    final private Lock lock2 = new ReentrantLock();

    public static void main(String[] args) throws IOException, InterruptedException {


        BackendLauncher backendLauncher = new BackendLauncher();
        backendLauncher.start();

        Thread.sleep(120_000);
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


    private final ExecutorService backMsgMonitoringHandler = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("backend-monitoring-handler-thread");
        return thread;
    });

    private final ExecutorService dbHandlerConsumer = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("DB-handler-thread");
        return thread;
    });

    private final ExecutorService dbProducer = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("DB-producer-thread");
        return thread;
    });

    private BackendLauncher(){
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        HtmlParser htmlParser = new HtmlParserKassirRu();
         monitoredEvent = new Concert(htmlParser);
         sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
                User.class, ConcertModel.class);
         sessionManager = new SessionManagerHibernate(sessionFactory);
         UserDao userDao = new UserDaoHibernate((SessionManagerHibernate) sessionManager);
         dbService = new DbServiceUserImpl(userDao);
        BackendService backendService = new BackendServiceImpl(monitoredEvent);
        requestHandler = new RequestHandler(backendService);

       // initiateForChecking().forEach(dbService::saveUser); //для проверки мониторинга
    }

    private void start(){
        backMsgHandler.submit(this::processMsgFromFront); //принимает и обрабатывает запросы с фронта
        backMsgProducer.submit(this::processMsgToFront);  //посылает обработанные сообщения на фронт
        backMsgMonitoringHandler.submit(this::processMsgMonitoring);//поток ждет срабатывания таймера и при успехе мониторинга формирует сообщение нужным пользователям
        dbProducer.submit(this::processMsgToDb); //формирует запросы к БД
        dbHandlerConsumer.submit(this::processMsgDbHandler); //принимает работает с БД
    }

    private void processMsgFromFront() {
        logger.info("processMsgHandler started");
        try {
            connectionMessageConsumer = factory.newConnection();
            Channel channelMessageConsumer = connectionMessageConsumer.createChannel();
            try {
                channelMessageConsumer.exchangeDeclare(FRONT_PRODUCER_EXCHANGE, "direct");
                channelMessageConsumer.queueDeclare(FRONT_PRODUCER_QUEUE, true, false, false, null);
                channelMessageConsumer.queueBind(FRONT_PRODUCER_QUEUE, FRONT_PRODUCER_EXCHANGE, FRONT_PRODUCER_QUEUE);
                logger.info(" [*] Waiting for messages from FrontEnd. To exit press CTRL+C");
                channelMessageConsumer.basicQos(1);
                try {
                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                       // User mmsFrom2 = Serializers.deserialize(delivery.getBody(), User.class);
                      //  MessageModel mmsFrom = new MessageModel(MessageType.TEST, Serializers.serialize(mmsFrom2));
                         MessageModel mmsFrom = Serializers.deserialize(delivery.getBody(), MessageModel.class);

                        try {
                            if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_EVENT_INFO.getValue())) {
                                requestHandler.getEventData(mmsFrom).ifPresentOrElse(messageQueue::add, () -> {
                                    logger.error("Fail to process Message {} ", Serializers.deserialize(mmsFrom.getPayload(), Message.class).getText());
                                });
                            } else if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_TICKET_INFO.getValue())) {
                                CallbackQuery s = Serializers.deserialize(mmsFrom.getPayload(), CallbackQuery.class);
                                    requestHandler.getTicketData(mmsFrom).ifPresentOrElse(messageQueue::add, () -> {
                                        logger.error("Fail to process Message {} ", s.getMessage().getText());
                                    });
                                if (s.getData().equals("NOTIFY")) //поменять под новую логику без edit
                                    requestHandler.switchingOnMonitoring(s.getMessage()).ifPresentOrElse(messageQueueDB::add, () -> {
                                    logger.error("Fail to create User for monitoring for Message {} ", s.getMessage().getText());
                                });
                            }
                               // mmsFrom.setMessageType(MessageType.GET_USERS);
 //                               messageQueueDB.add(new MessageModel(MessageType.GET_USERS, null));
//                                logger.info("Get all users request");

                        } finally {
                            //logger.info("Message is processed ");
                            channelMessageConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }
                    };
                    channelMessageConsumer.basicConsume(FRONT_PRODUCER_QUEUE, false, deliverCallback, consumerTag -> {
                    });
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    logger.info("Connection of processMsgFromFront is closed");
                }
            } catch (Exception ex ) {//catch (IOException | TimeoutException ex ) {
                logger.error(ex.getMessage(), ex);
                logger.info("Connection of processMsgFromFront is closed");
                channelMessageConsumer.close();

            }
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    private void processMsgMonitoring() {
        logger.info("processMsgMonitoring started");
        while (runFlag.get() || !usersMonitoringQueue.isEmpty()) {
            try {
                MessageModel message = usersMonitoringQueue.take(); //блокируется
                if (message.getMessageType().getValue().equals("shutdown")) {
                    logger.info("received the stop message");
                }
                else{
                    List<User> userList = Serializers.deserialize(message.getPayload(), List.class);
                    if (userList.size() != 0)
                        for (User user : userList) {
                            if (monitoredEvent.checkingTickets(user)) {
                                requestHandler.getMonitoringResult(user).ifPresentOrElse(messageQueue::add, () -> {
                                    logger.info("Fail to send monitoring result about User:  {} ",
                                            user); });
                                messageQueueDB.add(new MessageModel(MessageType.DELETE_USER, Serializers.serialize(user)));
                            }
                        }
                }
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    private void processMsgToFront() {
        logger.info("backMsgProducer started");
        while (runFlag.get() || !messageQueue.isEmpty()) {
            try {
               MessageForFront message = messageQueue.take(); //блокируется
                if (message.getMessageType().getValue().equals("shutdown")) {
                    logger.info("received the stop message");
                }else{
                try (Connection connection = factory.newConnection();
                     Channel channelProducer = connection.createChannel()) {
                    channelProducer.exchangeDeclare(BACK_PRODUCER_EXCHANGE, "direct");
                    channelProducer.basicPublish("", BACK_PRODUCER_QUEUE,
                            MessageProperties.PERSISTENT_TEXT_PLAIN, //сообщения не будут утеряны в случае падения RMQ
                            Serializers.serialize(message));
                    logger.info("Message  <{}> have gone to front ", Serializers.deserialize(message.getPayload(), String.class));
                } catch (IOException | TimeoutException e) {
                    logger.error(e.getMessage(), e);
                }}
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processMsgToDb(){
        logger.info("DBProducer started");
        while (runFlag.get() || !messageQueueDB.isEmpty()) {
            try {
                MessageModel message = messageQueueDB.take(); //блокируется
                if (message.getMessageType().getValue().equals("shutdown")) {
                    logger.info("messageQueueDB received the stop message");
                }else{
                    try (Connection connection = factory.newConnection();
                         Channel channelProducer = connection.createChannel()) {
                        channelProducer.exchangeDeclare(DB_EXCHANGE, "direct");
                        channelProducer.basicPublish("", DB_QUEUE,
                                MessageProperties.PERSISTENT_TEXT_PLAIN, //сообщения не будут утеряны в случае падения RMQ
                                Serializers.serialize(message));
                        logger.info("Message   have gone to DB for  {}",message.getMessageType());//, Serializers.deserialize(message.getPayload(), User.class), message.getMessageType());
                    } catch (IOException | TimeoutException e) {
                        logger.error(e.getMessage(), e);
                    }}
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processMsgDbHandler(){
        logger.info("DBHandler started");
        try {
            connectionDBConsumer = factory.newConnection();
            Channel channelConsumer = connectionDBConsumer.createChannel();
            channelConsumer.exchangeDeclare(DB_EXCHANGE, "direct");
            channelConsumer.queueDeclare(DB_QUEUE, true, false, false, null);
            channelConsumer.queueBind(DB_QUEUE, DB_EXCHANGE, DB_QUEUE);
            logger.info(" [*] Waiting requests to DB. To exit press CTRL+C");
            channelConsumer.basicQos(1);
            try {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    MessageModel mmsFrom = Serializers.deserialize(delivery.getBody(), MessageModel.class);
                    User user;
                    try {
                      //  try{lock2.lock();
                        switch(mmsFrom.getMessageType().getValue()) {
                            case "deleteUser":
                                user = Serializers.deserialize(mmsFrom.getPayload(), User.class);
                                dbService.delete(user.getId());
                                break;
                            case "saveUser":
                                user = Serializers.deserialize(mmsFrom.getPayload(), User.class);
                                dbService.saveUser(user);
                                logger.info("Save in handler!");
                                break;
                            case "getAllUsers":
                                List <User> list = dbService.getAllUsers();
                                usersMonitoringQueue.add(new MessageModel(MessageType.NOTIFY, Serializers.serialize(list)));
                                break;
                        }
                    //}
                      //  finally{
                      //  lock2.unlock();}
                        //перепроверить как лучше - отмечать или нет - обработать эксепшны с дб
                        channelConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                    catch (Exception ex){
                        logger.error(ex.getMessage(), ex);
                        logger.error("Fail with {} message with User {} for DB id redirected to another handler",mmsFrom.getMessageType().getValue(),  Serializers.deserialize(mmsFrom.getPayload(), User.class) );
                    }
                };
                channelConsumer.basicConsume(DB_QUEUE, false, deliverCallback, consumerTag -> {
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                connectionDBConsumer.close();
            }
        }
        catch (IOException | TimeoutException ex){
            logger.error(ex.getMessage(), ex);
        }
    }

    private void shutDownBackend() throws IOException {
        logger.info("now backServer is shutting down messages");
        messageQueue.add(new MessageForFront(MessageType.SHUTDOWN_MESSAGE, null, 0L, 0));
        messageQueueDB.add(new MessageModel(MessageType.SHUTDOWN_MESSAGE, null));
        usersMonitoringQueue.add(new MessageModel(MessageType.SHUTDOWN_MESSAGE, null));
        runFlag.set(false);
        sessionManager.close();
        sessionFactory.close();
        if (connectionMessageConsumer!=null)
            connectionMessageConsumer.close();
        if (connectionDBConsumer!=null)
            connectionDBConsumer.close();
        backMsgHandler.shutdown();
        backMsgMonitoringHandler.shutdown();
        backMsgProducer.shutdown();
        dbProducer.shutdown();
        dbHandlerConsumer.shutdown();
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


