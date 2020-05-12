package ru.otus.backend.eventApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
import ru.otus.backend.helper.DateParser;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.Serializers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Тесты проверяют: ")
class ConcertTest {

    private HtmlParser htmlParser = new HtmlParserKassirRu();
    private MonitoredEvent concert = new Concert(htmlParser);

    @BeforeEach
    public void set(){}

    @DisplayName("корректное отображение полученной информации о событии в сообщении пользователя")
    @ParameterizedTest
    @MethodSource("generateData")
    void correctlyShowEventInformationInUserMessage(String strings, long chatId, int MessageId) {
        Message message = mock(Message.class);
        given(message.getText()).willReturn(strings);
        given( message.getMessageId()).willReturn(MessageId);
        given( message.getChatId()).willReturn(chatId);
        MessageForFront res = concert.getConcertInfo(message);
        System.out.println(Serializers.deserialize(res.getPayload(), String.class));
        assertNotNull(Serializers.deserialize(res.getPayload(), String.class));
        assertTrue( Serializers.deserialize(res.getPayload(), String.class).contains(strings));
    }

    @DisplayName("корректное отображение об отсутсвии события в сообщении пользователя")
    @Test
    void correctlyShowAbsenceOfEventInformationInUserMessage() {
        Message message = spy(new Message());//Message.class);
        when(message.getText()).thenReturn("сплин");
        when(message.getMessageId()).thenReturn(1);
        when(message.getChatId()).thenReturn(1L);
        MessageForFront res = concert.getConcertInfo(message);
        assertNotNull(Serializers.deserialize(res.getPayload(), String.class));
        assertEquals("Концерты по запрашиваемому исполнителю не найдены", Serializers.deserialize(res.getPayload(), String.class));
    }

    @DisplayName("корректное отображение полученной информации о билетах в сообщении пользователя")
    @ParameterizedTest
    @MethodSource("generateData")
    void correctlyShowTicketInformationInUserMessage(String strings, long chatId, int MessageId) {
        Message message = mock(Message.class);
        given(message.getText()).willReturn(strings);
        given( message.getMessageId()).willReturn(MessageId);
        given( message.getChatId()).willReturn(chatId);
        MessageForFront res = concert.getTicketInfo(chatId, MessageId,0, message);
        System.out.println(res);
        assertNotNull(res);
        assertTrue(Serializers.deserialize(res.getPayload(), String.class).contains(strings));
        assertTrue(Serializers.deserialize(res.getPayload(), String.class).contains("Минимальная стоимость билетов в другие зоны составляет:"));
    }

    @DisplayName("корректное отображение об отсутсвии события в сообщении пользователя")
    @Test
    void correctlyShowAbsenceOfEventInformationInUserMessage2() {
        Message message = mock(Message.class);
        given(message.getText()).willReturn("Twenty one pilot");
        given( message.getMessageId()).willReturn(1);
        given( message.getChatId()).willReturn(1L);
        MessageForFront res = concert.getConcertInfo(message);
        assertNotNull(Serializers.deserialize(res.getPayload(), String.class));
        assertTrue(Serializers.deserialize(res.getPayload(), String.class).contains("Билетов на танцпол и фанзону нет."));
        assertEquals(res, concert.getConcertInfo(message));
        System.out.println(Serializers.deserialize(res.getPayload(), String.class));
    }

    @DisplayName("корректное переключение флагов необходимости мониторинга события")
    @ParameterizedTest
    @MethodSource("generateUsers")
    void shouldEventBeMonitor(User users){
        assertTrue(concert.checkingTickets(users));
        assertFalse(users.getConcert().getShouldBeMonitored());
        assertNotEquals(users.getMessageText(), "");
    }



    @DisplayName("корректное переключение флагов необходимости мониторинга события")
    @Test
    void shouldEventBeMonitor(){
        User user =new User( 2L, new ConcertModel("TWENTY ØNE PILØTS",
                "12 Июльвс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"), new Date());
        assertFalse(user.getConcert().getShouldBeMonitored());
        assertTrue(concert.checkingTickets(user));
        assertTrue(user.getConcert().getShouldBeMonitored());
        assertEquals(user.getMessageText(), "");
    }

    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("Aerosmith", 1L, 1),
                Arguments.of("TWENTY ØNE PILØTS", 2L, 2),
                Arguments.of("Элизиум", 3L, 3));
    }

    private static Stream<Arguments> generateUsers() {
        return Stream.of(
//                Arguments.of(new User(1L, new ConcertModel("Green Day",
//                        "24 Майвс 19:00",
//                        "Стадион \"Открытие Арена\"",
//                        "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19"), new Date())),
                Arguments.of(new User( 3L, new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20"),new Date())));
    }
}