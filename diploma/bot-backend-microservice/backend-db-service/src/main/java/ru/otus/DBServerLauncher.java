package ru.otus;
import com.rabbitmq.client.*;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.db.hibernate.HibernateUtils;
import ru.otus.db.hibernate.dao.UserDao;
import ru.otus.db.hibernate.dao.UserDaoHibernate;
import ru.otus.db.hibernate.sessionmanager.SessionManager;
import ru.otus.db.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.db.service.DBServiceUser;
import ru.otus.db.service.DbServiceUserImpl;

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


public class DBServerLauncher {
    private static Logger logger = LoggerFactory.getLogger(DBServerLauncher.class);

    private static final String DB_EXCHANGE = "db_exchange";
    private static final String DB_QUEUE = "db_queue";

    private static final String MONITORING_EXCHANGE = "monitoring_exchange";
    private static final String MONITORING_QUEUE = "monitoring_queue";
    private static final String BACK_PRODUCER_EXCHANGE = "back_producer_exchange";
    private static final String BACK_PRODUCER_QUEUE = "back_producer_queue";

    private Connection connectionDB;
    Channel channelDBConsumer;

    private final AtomicBoolean runFlag = new AtomicBoolean(true);
    private SessionFactory sessionFactory;
    private SessionManager sessionManager;
    private ConnectionFactory factory;
    private DBServiceUser dbService;

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {

        DBServerLauncher backendLauncher = new DBServerLauncher();
        backendLauncher.start();

        Thread.sleep(240_000);
        backendLauncher.shutDownBackend();
        Thread.sleep(2_000);
    }


    private final ExecutorService dbHandlerConsumer = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("DB-handler-thread");
        return thread;
    });

    private DBServerLauncher(){
        factory = new ConnectionFactory();
        factory.setHost("localhost");
         sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
                User.class, ConcertModel.class);
         sessionManager = new SessionManagerHibernate(sessionFactory);
         UserDao userDao = new UserDaoHibernate((SessionManagerHibernate) sessionManager);
         dbService = new DbServiceUserImpl(userDao);
        try {
         connectionDB = factory.newConnection();
         channelDBConsumer= connectionDB.createChannel();
            channelDBConsumer.exchangeDeclare(DB_EXCHANGE, "direct");
            channelDBConsumer.queueDeclare(DB_QUEUE, true, false, false, null);
            channelDBConsumer.queueBind(DB_QUEUE, DB_EXCHANGE, DB_QUEUE);
            channelDBConsumer.basicQos(1);
          } catch (TimeoutException | IOException e) {
        e.printStackTrace();
        }
    initiateForChecking().forEach(dbService::saveUser); //для проверки мониторинга
}

    private void start(){
        dbHandlerConsumer.submit(this::processMsgDbHandler); //принимает работает с БД
    }


    private void processMsgDbHandler(){
        logger.info("DBHandler started");
            logger.info(" [*] Waiting requests to DB. To exit press CTRL+C");
            try {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    MessageModel mmsFrom = Serializers.deserialize(delivery.getBody(), MessageModel.class);
                    User user;
                    try {
                      //  try{lock2.lock();
                        switch (mmsFrom.getMessageType().getValue()) {
                            case "deleteUser":
                                user = Serializers.deserialize(mmsFrom.getPayload(), User.class);
                                dbService.delete(user.getId());break;

                            case "saveUser" :
                                user = Serializers.deserialize(mmsFrom.getPayload(), User.class);
                                dbService.saveUser(user);
                                logger.info("Save in handler!");
                           break;

                            case "monitoring" :
                                List <User> list =  dbService.getAllUsers();
                                putToQueue(new MessageModel(MessageType.GET_MONITORING_RESULT, Serializers.serialize(list)), MONITORING_EXCHANGE, MONITORING_QUEUE);
                                logger.info("Sent message with GET_MONITORING_RESULT to monitoring queue" );
                            break;

                            case "admin_get_users" :
                                List <User> list2 =  dbService.getAllUsers();
                                putToQueue(new MessageForFront(MessageType.ADMIN_GET_USERS, Serializers.serialize(list2),0L,0), BACK_PRODUCER_EXCHANGE, BACK_PRODUCER_QUEUE);
                                logger.info("Sent message with ADMIN_GET_USERS to backProducer queue" );
                                break;
                        }
                    //}
                      //  finally{
                      //  lock2.unlock();}
                        //перепроверить как лучше - отмечать или нет - обработать эксепшны с дб
                        channelDBConsumer.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                    catch (Exception ex){
                        logger.error(ex.getMessage(), ex);
                        logger.error("Fail with {} message with User {} for DB id redirected to another handler",mmsFrom.getMessageType().getValue(),  Serializers.deserialize(mmsFrom.getPayload(), User.class) );
                    }
                };
                channelDBConsumer.basicConsume(DB_QUEUE, false, deliverCallback, consumerTag -> {
                });
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }


    private Boolean putToQueue(MessageModel message, String exchange, String queue) {
        logger.info("put the message of type {}  to rabbitMq exchange: {} queue: {}",message.getMessageType(),exchange, queue);
        if (runFlag.get()) {
            try (Channel channelProducer = connectionDB.createChannel()) {
                channelProducer.exchangeDeclare(exchange, "direct");
                channelProducer.basicPublish("", queue,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        Serializers.serialize(message));
                logger.info("Message have gone to {} ", queue);
                return true;
            } catch (IOException | TimeoutException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return false;
    }

    private void shutDownBackend() throws IOException, TimeoutException {
        logger.info("now dbServer is shutting down messages");
        runFlag.set(false);
        sessionManager.close();
        sessionFactory.close();
        if (channelDBConsumer != null)
            channelDBConsumer.close();
        if (connectionDB != null)
            connectionDB.close();
        dbHandlerConsumer.shutdown();
    }

    private static List<User> initiateForChecking(){
        List<User> userList = new ArrayList<User>(List.of(
                new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, 6,5).getTime()),
                new User(202812830, new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2019, 4,23).getTime()),
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


