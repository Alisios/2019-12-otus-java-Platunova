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
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.Serializers;

import javax.validation.constraints.AssertTrue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Тесты проверяют: ")
class ConcertTest {

    private HtmlParser htmlParser = new HtmlParserKassirRu();
    private Concert concert = new Concert(htmlParser);

    @BeforeEach
    public void set(){}

    @DisplayName("корректное отображение полученной информации о событии, на которое нет возможности мониторинга, в сообщении пользователя, корректную работу очистки кэша")
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
        assertTrue(Serializers.deserialize(res.getPayload(), String.class).contains(strings));
        assertFalse(Serializers.deserialize(res.getPayload(), String.class).contains("Хотите отслеживать появление"));
    }

    @Test
   void concertParser(){
        String [] s ="По Вашему запросу найдена информация: \nИсполнитель: Aerosmith (Аэросмит)\nДата: 30 Июльчт 19:00\nМесто проведения: ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина\nСсылка: https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30".split("\n");
        ConcertModel concertModel = new ConcertModel();
        concertModel.setArtist(s[1].trim().replace("Исполнитель: ", ""));
        concertModel.setDate(s[2].trim().replace("Дата: ", ""));
        concertModel.setPlace(s[3].trim().replace("Место проведения: ", ""));
        concertModel.setConcertUrl((s[4].trim().replace("Ссылка: ", "")));
        concertModel.setShouldBeMonitored(true);
        System.out.println(Arrays.toString(s));
        System.out.println(concertModel);
        assertEquals("Исполнитель: Aerosmith (Аэросмит)\nДата: 30 Июльчт 19:00\nМесто проведения: ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина\nСсылка: https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30", concertModel.toString());
    }


    @DisplayName("корректное отображение полученной информации о событии в сообщении пользователя, которое возможно мониторить")
    @ParameterizedTest
    @MethodSource("generateData3")
    void correctlyShowEventInformationInUserMessage2(String strings, long chatId, int MessageId) {
        Message message = mock(Message.class);
        given(message.getText()).willReturn(strings);
        given( message.getMessageId()).willReturn(MessageId);
        given( message.getChatId()).willReturn(chatId);
        MessageForFront res = concert.getConcertInfo(message);
        System.out.println(Serializers.deserialize(res.getPayload(), String.class));
        assertTrue(Serializers.deserialize(res.getPayload(), String.class).contains("Хотите отслеживать появление"));
    }

    @DisplayName("корректное отображение сообщения пользователю при  отсутсвии события")
    @Test
    void correctlyShowAbsenceOfEventInformationInUserMessage() {
        Message message = mock(Message.class);
        when(message.getText()).thenReturn("сплин");
        when(message.getMessageId()).thenReturn(1);
        when(message.getChatId()).thenReturn(1L);
        MessageForFront res = concert.getConcertInfo(message);
        assertNotNull(Serializers.deserialize(res.getPayload(), String.class));
        assertEquals("Концерты по запрашиваемому исполнителю не найдены", Serializers.deserialize(res.getPayload(), String.class));
    }

    @DisplayName("корректное отображение полученной информации о билетах в сообщении пользователя")
    @ParameterizedTest
    @MethodSource("generateData2")
    void correctlyShowTicketInformationInUserMessage(String strings, long chatId, int MessageId) {
        Message message = mock(Message.class);
        given(message.getText()).willReturn(strings);
        given( message.getMessageId()).willReturn(MessageId);
        given( message.getChatId()).willReturn(chatId);
        MessageModel m = concert.getConcertInfo(message);
        given( message.getMessageId()).willReturn(MessageId+1);
        String res = concert.getTicketInfo(Serializers.deserialize(m.getPayload(), String.class),0);
        System.out.println(res);
        assertNotNull(res);
        assertTrue(res.toLowerCase().contains(strings.toLowerCase()));
        assertTrue(res.contains("Минимальная стоимость билетов в другие зоны составляет:"));
    }

    @DisplayName("корректное отображение об отсутсвии билетов в фанзону в сообщении пользователя, корректную очистку кэша")
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

    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("Aerosmith", 1L, 1),
                Arguments.of("Элизиум", 3L, 3));
    }

    private static Stream<Arguments> generateData2() {
        return Stream.of(
                Arguments.of("ария", 1L, 1),
                Arguments.of("beatles", 2L, 2));
    }

    private static Stream<Arguments> generateData3() {
        return Stream.of(
                Arguments.of("кис-кис", 1L, 1),
                Arguments.of("TWENTY ØNE PILØTS", 2L, 2));
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