package ru.otus.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.Concert;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
import ru.otus.helpers.Serializers;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


@DisplayName("Тесты проверяют: ")
class BackendServiceImplTest {
    final private String COMMAND = "/";
    final private String NOTHING = "NOTHING";
    final private String NOTIFY = "NOTIFY";
    final private String NO = "NO";
    private HtmlParser htmlParser = new HtmlParserKassirRu();
    private Concert monitoredEvent = new Concert(htmlParser);
    private BackendServiceImpl backendServiceImpl = new BackendServiceImpl(monitoredEvent);

    @ParameterizedTest
    @DisplayName("корректное отображение информации о событии в BackendService")
    @ValueSource(strings = {"TWENTY ØNE PILØTS", "Элизиум"})
    void correctlyShowEventInformationFromBackendService(String strings) {
        Message message = mock(Message.class);
        given(message.getText()).willReturn(strings);
        given( message.getMessageId()).willReturn(1);
        given( message.getChatId()).willReturn(1L);
        String res = Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class);
        System.out.println(res);
        assertNotNull(res);
        assertTrue(res.contains(strings));
    }

    @Test
    @DisplayName("корректный ответ на команды от пользователя")
    void correctlyShowNoInfoFromBackendService() {
        Message message = mock(Message.class);
        given(message.getText()).willReturn("sdjfbajsbjkg;kjg");
        given( message.getMessageId()).willReturn(1);
        given( message.getChatId()).willReturn(1L);
        assertEquals("Концерты по запрашиваемому исполнителю не найдены",Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
    }

    @Test
    @DisplayName("корректный ответ на неподходящий лист событий")
    void correctlyReactOnCallbackQueryNothing(){
        Message message = mock(Message.class);
        given(message.getText()).willReturn("beatles");
        given( message.getMessageId()).willReturn(1);
        given( message.getChatId()).willReturn(1L);
        backendServiceImpl.getEventData(message);
        CallbackQuery callbackQuery = spy(new CallbackQuery());//Message.class);
        given(callbackQuery.getMessage()).willReturn(message);
        given( message.getMessageId()).willReturn(2);
        given(callbackQuery.getData()).willReturn(NOTHING);
        assertEquals(Serializers.deserialize(backendServiceImpl.getTicketData(callbackQuery).getPayload(), String.class),  "Очень жаль! Обращайтесь еще!");
        }

    @Test
    @DisplayName("корректный ответ на нежелание пользователя на мониторинг")
    void correctlyReactOnCallbackQueryNo(){
        Message message = mock(Message.class);
        given(message.getText()).willReturn("кис кис");
        given( message.getMessageId()).willReturn(1);
        given( message.getChatId()).willReturn(1L);
        backendServiceImpl.getEventData(message);
        CallbackQuery callbackQuery = spy(new CallbackQuery());//Message.class);
        given(callbackQuery.getData()).willReturn("0");
        given(callbackQuery.getMessage()).willReturn(message);
         String s = "1) Исполнитель: кис-кис\n" +
                 "Дата: 24 Июньср 20:00\n" +
                 "Место проведения: ГЛАВCLUB GREEN CONCERT\n" +
                 "Ссылка: https://msk.kassir.ru/koncert/glavclub-green-concert/kis-kis_2020-06-24\n" +
                 "Билетов на танцпол и фанзону нет.\n" +
                 "Минимальная стоимость билетов в другие зоны составляет: 4500, максимальная: 4500." +
                 "2) Исполнитель: кис-кис\n" +
                "Дата: 24 Июньср 20:00\n" +
                "Место проведения: ГЛАВCLUB GREEN CONCERT\n" +
                "Ссылка: https://msk.kassir.ru/koncert/glavclub-green-concert/kis-kis_2020-06-24\n" +
                "Билетов на танцпол и фанзону нет.\n" +
                "Минимальная стоимость билетов в другие зоны составляет: 4500, максимальная: 4500." ;
        given(message.getText()).willReturn(s);
        assertTrue(Serializers.deserialize(backendServiceImpl.getTicketData(callbackQuery).getPayload(), String.class).contains("Хотите отслеживать появление билетов"));
        given(callbackQuery.getData()).willReturn(NOTIFY);
        assertEquals(Serializers.deserialize(backendServiceImpl.getTicketData(callbackQuery).getPayload(), String.class),  "Хорошо! Я сообщу, если появятся билеты в фанзону или танцевальный партер!");
        given(callbackQuery.getData()).willReturn(NO);
        assertEquals(Serializers.deserialize(backendServiceImpl.getTicketData(callbackQuery).getPayload(), String.class),  "Обращайтесь еще!");
    }

//    @Test
//    @DisplayName("корректный ответ на желание пользователя на мониторинг и правильное функционирование всех функций с учетом меняющихся messageId")
//    void correctlyReactOnCallbackQueryNotify(){
//        Message message = mock(Message.class);
//        given(message.getText()).willReturn("кис кис");
//        given( message.getMessageId()).willReturn(1);
//        given( message.getChatId()).willReturn(1L);
//        backendServiceImpl.getEventData(message);
//     //   assertNotNull(monitoredEvent.getCacheMap().get(message.getChatId()));
//     //   assertTrue(monitoredEvent.getCacheMap().get(message.getChatId()).containsKey(1));
//        CallbackQuery callbackQuery = spy(new CallbackQuery());//Message.class);
//        // CallbackQuery callbackQuery = mock(CallbackQuery.class);
//        given( message.getMessageId()).willReturn(2);
//        given(callbackQuery.getData()).willReturn("0");
//        given(callbackQuery.getMessage()).willReturn(message);
//        assertTrue(Serializers.deserialize(backendServiceImpl.getTicketData(callbackQuery).getPayload(), String.class).contains("Хотите отслеживать появление билетов"));
//      //  assertFalse(monitoredEvent.getCacheMap().get(message.getChatId()).containsKey(1));
//      //  assertTrue(monitoredEvent.getCacheMap().get(message.getChatId()).containsKey(2));
//        given(callbackQuery.getData()).willReturn(NOTIFY);
//        given( message.getMessageId()).willReturn(3);
//     ///   assertEquals(Serializers.deserialize(backendServiceImpl.getTicketData(callbackQuery).getPayload(), String.class),  "Хорошо! Я сообщу, если появятся билеты в фанзону или танцевальный партер!");
//       // assertNotNull(monitoredEvent.getCacheMap().get(message.getChatId()));
//    }

    @Test
    @DisplayName("корректный ответ на команды от пользователя")
    void correctlyShowCommandInfoFromBackendService() {
        final String unknown = "Неизвестная команада. Для начала работы введите имя исполнителя. Имя не должно начинаться со знака '/'.";
        Message message = mock(Message.class);
        given(message.getText()).willReturn("/start");
        given( message.getMessageId()).willReturn(1);
        given( message.getChatId()).willReturn(1L);
        assertEquals("Привет! Я найду билеты на любой концерт! Введите исполнителя!",Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
        given(message.getText()).willReturn("/help");
        assertEquals("Это бот для поиска билетов на концерт. Для начала работы введите имя исполнителя. " +
                "Имя не должно начинаться со знака '/'.", Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
        given(message.getText()).willReturn("/shglsahdgf");
        assertEquals( unknown, Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
        given(message.getText()).willReturn("/Элизиум");
        assertEquals(unknown, Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
        given(message.getText()).willReturn("/ Элизиум");
        assertEquals(unknown, Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
        given(message.getText()).willReturn(" / ");
        assertEquals(unknown, Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
        given(message.getText()).willReturn(" / Элизиум");
        assertEquals(unknown, Serializers.deserialize(backendServiceImpl.getEventData(message).getPayload(), String.class));
    }


    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("NOTHING", "Очень жаль! Обращайтесь еще!"),
                Arguments.of("NO", "Обращайтесь еще!"));
    }
}