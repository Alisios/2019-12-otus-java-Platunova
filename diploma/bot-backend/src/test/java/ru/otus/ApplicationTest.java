//package ru.otus;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.RepeatedTest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.telegram.telegrambots.api.objects.Message;
//import ru.otus.backend.BackendService;
//import ru.otus.backend.BackendServiceImpl;
//import ru.otus.db.DBService;
//import ru.otus.db.DBServiceImpl;
//import ru.otus.backend.eventApi.Concert;
//import ru.otus.backend.eventApi.MonitoredEvent;
//import ru.otus.backend.eventApi.helpers.HtmlParser;
//import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
//import ru.otus.backend.handlers.GetEventDataRequestHandler;
//import ru.otus.helpers.Serializers;
//import ru.otus.messagesystem.*;
//import ru.otus.telegramApi.TelegramService;
//import ru.otus.telegramApi.TelegramServiceImpl;
//import ru.otus.telegramApi.handlers.GetEventResponseHandler;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.stream.IntStream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.spy;
//
//class IntegrationTest {
//    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
//
//    private static final String TELEGRAM_SERVICE_NAME = "frontendService";
//    private static final String BACKEND_SERVICE_NAME = "databaseService";
//
//    private MessageSystem messageSystem;
//    private TelegramService telegramService;
//    private MsClient databaseMsClient;
//    private MsClient frontendMsClient;
//
//    final String res = "По Вашему запросу найдена информация: Исполнитель: Элизиум\n Дата: 20 Июньсб 19:00\n Место проведения: ГЛАВCLUB GREEN CONCERT \nСсылка: https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20" +
//            "\nТанцевальный партер: 1 200 Р. \nМинимальная стоимость билетов в другие зоны составляет: 3000, максимальная: 125000.";
//
//    @DisplayName("Базовый сценарий получения данных")
//    @RepeatedTest(1)
//    public void getDataById() throws Exception {
//    //    createMessageSystem(true);
////        int counter = 1;
////        CountDownLatch waitLatch = new CountDownLatch(counter);
////      //  Message message = new Message();
////        Message message = mock(Message.class);
////        given(message.getText()).willReturn("Элизиум");
////        given( message.getMessageId()).willReturn(1);
////        given( message.getChatId()).willReturn(1L);
////       // Serializers serializers =mock(Serializers.class);
//////        given(Serializers.serialize(message)).willReturn( Serializers.serialize("Элизиум"));
////
////
////      //  IntStream.range(0, counter).forEach(() ->
////                telegramService.getEventInfo(message, data -> {
////                    assertThat(data).isEqualTo((res));
////                    waitLatch.countDown();
////                });
////
////        waitLatch.await();
//        messageSystem.dispose();
//        logger.info("done");
//    }
//    @DisplayName("Выполнение запроса после остановки сервиса")
//    @RepeatedTest(1)
//    public void getDataAfterShutdown2() throws Exception {
//        messageSystem = new MessageSystemImpl(true);
//        messageSystem.dispose();
//    }
//
//    @DisplayName("Выполнение запроса после остановки сервиса")
//    @RepeatedTest(1)
//    public void getDataAfterShutdown() throws Exception {
//        createMessageSystem(true);
//        messageSystem.dispose();
//        Message message = new Message();//mock(Message.class);
////        given(message.getText()).willReturn("Элизиум");
////        given( message.getMessageId()).willReturn(1);
////        given( message.getChatId()).willReturn(1L);
//
//        CountDownLatch waitLatchShutdown = new CountDownLatch(1);
//
//        when(frontendMsClient.sendMessage(any(MessageModel.class))).
//                thenAnswer(invocation -> {
//                    waitLatchShutdown.countDown();
//                    return null;
//                });
//        telegramService.getEventInfo(message, data -> logger.info("data:{}", data));
//        waitLatchShutdown.await();
//        boolean result = verify(frontendMsClient).sendMessage(any(MessageModel.class));
//        assertThat(result).isFalse();
//
//        logger.info("done");
//    }
//
////    @DisplayName("Тестируем остановку работы MessageSystem")
////    @RepeatedTest(1000)
////    public void stopMessageSystem() throws Exception {
////        createMessageSystem(false);
////        int counter = 100;
////        CountDownLatch messagesSentLatch = new CountDownLatch(counter);
////        CountDownLatch messageSystemDisposed = new CountDownLatch(1);
////        Message message = mock(Message.class);
////        given(message.getText()).willReturn("Элизиум");
////        given( message.getMessageId()).willReturn(1);
////        given( message.getChatId()).willReturn(1L);
////
////        IntStream.range(0, counter).forEach(id -> {
////                    telegramService.getEventInfo(message, data -> {
////                    });
////                    messagesSentLatch.countDown();
////                }
////        );
////        messagesSentLatch.await();
////        assertThat(messageSystem.currentQueueSize()).isEqualTo(counter);
////
////        messageSystem.start();
////        disposeMessageSystem(messageSystemDisposed::countDown);
////
////        messageSystemDisposed.await();
////        assertThat(messageSystem.currentQueueSize()).isEqualTo(0);
////
////        logger.info("done");
////    }
//
//    private void createMessageSystem(boolean startProcessing) {
//        logger.info("setup");
//
//
//        HtmlParser htmlParser = new HtmlParserKassirRu();
//        MonitoredEvent monitoredEvent = new Concert(htmlParser);
//        DBService dbService = new DBServiceImpl();
//
//        MessageSystem messageSystem = new MessageSystemImpl();
//        MsClient telegramMsClient = new MsClientImpl(TELEGRAM_SERVICE_NAME, messageSystem);
//        TelegramService telegramService = new TelegramServiceImpl(telegramMsClient, BACKEND_SERVICE_NAME);
//        telegramMsClient.addHandler(MessageType.GET_EVENT_INFO, new GetEventResponseHandler(telegramService));
//        // telegramMsClient.addHandler(MessageType.NOTIFY, new EventMonitoringResponseHandler(telegramService));
//        messageSystem.addClient(telegramMsClient);
//
//        MsClient databaseMsClient = new MsClientImpl(BACKEND_SERVICE_NAME, messageSystem);
//        BackendService backendService = new BackendServiceImpl(monitoredEvent, dbService);
//        databaseMsClient.addHandler(MessageType.GET_EVENT_INFO, new GetEventDataRequestHandler(backendService));
//        // databaseMsClient.addHandler(MessageType.GET_EVENT_INFO, new EventMonitoringRequestHandler(dbService));
//        messageSystem.addClient(databaseMsClient);
////        messageSystem = new MessageSystemImpl(startProcessing);
////
////        databaseMsClient = spy(new MsClientImpl(BACKEND_SERVICE_NAME, messageSystem));
////        Message message =new Message();//mock(Message.class);
//////        given(message.getText()).willReturn("Элизиум");
//////        given( message.getMessageId()).willReturn(1);
//////        given( message.getChatId()).willReturn(1L);
////        BackendService backendService = mock(BackendService.class);
////        given(backendService.getEventData(message)).willReturn(res);
////        MsClient databaseMsClient = new MsClientImpl(BACKEND_SERVICE_NAME, messageSystem);
////        databaseMsClient.addHandler(MessageType.GET_EVENT_INFO, new GetEventDataRequestHandler(backendService));
////
////        frontendMsClient = spy(new MsClientImpl(FRONTEND_SERVICE_CLIENT_NAME, messageSystem));
////        telegramService = new TelegramServiceImpl(frontendMsClient, BACKEND_SERVICE_NAME);
////        frontendMsClient.addHandler(MessageType.GET_EVENT_INFO, new GetEventResponseHandler(telegramService));
////        messageSystem.addClient(frontendMsClient);
//        logger.info("setup done");
//    }
//
//    private void disposeMessageSystem(Runnable callback) {
//        try {
//            messageSystem.dispose(callback);
//        } catch (InterruptedException ex) {
//            logger.error(ex.getMessage(), ex);
//        }
//    }
//
//}