//package ru.otus.backend;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.telegram.telegrambots.api.objects.Message;
//
//import ru.otus.db.service.DBServiceUser;
//import ru.otus.backend.eventApi.Concert;
//import ru.otus.backend.eventApi.MonitoredEvent;
//import ru.otus.backend.eventApi.helpers.HtmlParser;
//import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mock;
//
//@DisplayName("Тесты проверяют: ")
//class BackendServiceImplTest {
//    private HtmlParser htmlParser = new HtmlParserKassirRu();
//    private MonitoredEvent monitoredEvent = new Concert(htmlParser);
//    private DBServiceUser dbService = mock(DBServiceUser.class);
//    private BackendServiceImpl backendServiceImpl = new BackendServiceImpl(monitoredEvent);
//
//    @ParameterizedTest
//    @DisplayName("корректное отображение информации о событии в BackendService")
//    @ValueSource(strings = {"TWENTY ØNE PILØTS", "Элизиум"})
//    void correctlyShowEventInformationFromBackendService(String strings) {
//        Message message = mock(Message.class);
//        given(message.getText()).willReturn(strings);
//        given( message.getMessageId()).willReturn(1);
//        given( message.getChatId()).willReturn(1L);
//        String res = backendServiceImpl.getEventData(message);
//        System.out.println(res);
//        assertNotNull(res);
//        assertTrue(res.contains(strings));
//    }
//
//    @Test
//    @DisplayName("корректный ответ на команды от пользователя")
//    void correctlyShowNoInfoFromBackendService() {
//        Message message = mock(Message.class);
//        given(message.getText()).willReturn("sdjfbajsbjkg;kjg");
//        given( message.getMessageId()).willReturn(1);
//        given( message.getChatId()).willReturn(1L);
//        assertEquals("Концерты по запрашиваемому исполнителю не найдены",backendServiceImpl.getEventData(message));
//    }
//
//
//        @Test
//    @DisplayName("корректный ответ на команды от пользователя")
//    void correctlyShowCommandInfoFromBackendService() {
//        final String unknown = "Неизвестная команада. Для начала работы введите имя исполнителя. Имя не должно начинаться со знака '/'.";
//        Message message = mock(Message.class);
//        given(message.getText()).willReturn("/start");
//        given( message.getMessageId()).willReturn(1);
//        given( message.getChatId()).willReturn(1L);
//        assertEquals("Привет! Я найду билеты на любой концерт! Введите исполнителя!",backendServiceImpl.getEventData(message));
//        given(message.getText()).willReturn("/help");
//        assertEquals("Это бот для поиска билетов на концерт. Для начала работы введите имя исполнителя. " +
//                "Имя не должно начинаться со знака '/'.", backendServiceImpl.getEventData(message));
//        given(message.getText()).willReturn("/shglsahdgf");
//        assertEquals( unknown, backendServiceImpl.getEventData(message));
//        given(message.getText()).willReturn("/Элизиум");
//        assertEquals(unknown, backendServiceImpl.getEventData(message));
//        given(message.getText()).willReturn("/ Элизиум");
//        assertEquals(unknown, backendServiceImpl.getEventData(message));
//        given(message.getText()).willReturn(" / ");
//        assertEquals(unknown, backendServiceImpl.getEventData(message));
//        given(message.getText()).willReturn(" / Элизиум");
//        assertEquals(unknown, backendServiceImpl.getEventData(message));
//    }
//}