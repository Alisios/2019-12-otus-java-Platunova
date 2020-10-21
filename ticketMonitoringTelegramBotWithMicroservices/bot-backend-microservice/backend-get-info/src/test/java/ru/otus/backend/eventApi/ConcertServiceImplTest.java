package ru.otus.backend.eventApi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.service.GetEventInfoServiceParsing;
import ru.otus.backend.eventApi.service.GetEventInfoServiceRestApi;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.CallbackType;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Тесты проверяют: ")
class ConcertServiceImplTest {

    private final GetEventInfoServiceParsing eventService = mock(GetEventInfoServiceParsing.class);
    private final GetEventInfoServiceRestApi eventServiceRest = mock(GetEventInfoServiceRestApi.class);
    private final ConcertService concert = new ConcertServiceImpl(eventService, eventServiceRest);
    private final Message message = mock(Message.class);

    List<ConcertModel> concertList = new ArrayList<>(List.of(
            new ConcertModel("TWENTY ØNE PILØTS",
                    "12 Июльвс 19:00",
                    "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                    "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                    List.of(new TicketModel("Фанзона", "5000руб"), new TicketModel("h6", "1200руб"), new TicketModel("t5", "1200руб"), new TicketModel("С32", "2400руб")))));
    List<ConcertModel> concertListWithoutTickets = new ArrayList<>(List.of(
            new ConcertModel("TWENTY ØNE PILØTS",
                    "28 Июльвс 19:00",
                    "Другое место",
                    "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                    List.of(new TicketModel("C5", "5000руб"), new TicketModel("h6", "1200руб"), new TicketModel("t5", "1200руб"), new TicketModel("С32", "2400руб")))));

    @BeforeEach
    public void set() {
        given(message.getMessageId()).willReturn(2);
        given(message.getChatId()).willReturn(2L);
    }

    @DisplayName("корректно обрабатывает IOException")
    @Test
    void correctlyHandleIoException() throws IOException {
        given(message.getText()).willReturn("abc");
        when(eventService.getEventInformation(message.getText())).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> {
            concert.getConcertInfo(message);
        });
    }

    @Test
    @DisplayName("корректное отображение полученной информации о единственном найденном событии, на которое есть билеты")
    void correctlyShowsInformationAboutEventWithTicketsInUserMessage() throws IOException {
        given(message.getText()).willReturn(concertList.get(0).getArtist());
        given(eventService.getEventInformation(message.getText())).willReturn(concertList);
        given(eventService.getTicketInformation(concertList.get(0))).willReturn(concertList.get(0).getTickets());
        MessageForFront res = concert.getConcertInfo(message);
        assertThat(Serializers.deserialize(res.getPayload(), String.class))
                .isNotNull()
                .contains("По Вашему запросу найдена информация:")
                .contains("TWENTY ØNE PILØTS")
                .contains("12 Июльвс 19:00")
                .contains("ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина")
                .doesNotContain("Хотите отслеживать");
        assertThat(res.getCallbackType()).isNotEqualTo(CallbackType.IF_SHOULD_BE_MONITORED.getValue());

    }

    @Test
    @DisplayName("корректное отображение полученной информации о единственном найденном событии, на которое нет билетов, и установка флага shouldBeMonitoring ")
    void correctlyShowsInformationAboutEventWithoutTicketsInUserMessageAndSetShouldBeMonitoringFlag() throws IOException {
        given(message.getText()).willReturn(concertListWithoutTickets.get(0).getArtist());
        given(eventService.getEventInformation(message.getText())).willReturn(concertListWithoutTickets);
        given(eventService.getTicketInformation(concertListWithoutTickets.get(0))).willReturn(concertListWithoutTickets.get(0).getTickets());
        MessageForFront res = concert.getConcertInfo(message);
        assertThat(Serializers.deserialize(res.getPayload(), String.class))
                .isNotNull()
                .contains("По Вашему запросу найдена информация:")
                .contains("TWENTY ØNE PILØTS")
                .contains("28 Июльвс 19:00")
                .contains("Другое место")
                .contains("Хотите отслеживать");
        assertThat(res.getCallbackType()).isEqualTo(CallbackType.IF_SHOULD_BE_MONITORED.getValue());
    }

    @Test
    @DisplayName("корректное отображение отсуствия информации о событии")
    void correctlyShowsInformationWithoutEvent() throws IOException {
        given(message.getText()).willReturn(concertListWithoutTickets.get(0).getArtist());
        given(eventService.getTicketInformation(concertListWithoutTickets.get(0))).willReturn(concertListWithoutTickets.get(0).getTickets());
        MessageForFront res = concert.getConcertInfo(message);
        assertThat(Serializers.deserialize(res.getPayload(), String.class))
                .isNotNull()
                .isEqualTo("Концерты по запрашиваемому исполнителю не найдены");
        assertThat(res.getCallbackType()).isNotEqualTo(CallbackType.IF_SHOULD_BE_MONITORED.getValue());
    }

    @Test
    @DisplayName("корректное отображение информации о нескольких найденных событиях, " +
            "и установка флага LIST_OF_EVENTS ")
    void correctlyShowsListOfEventsAndSetsListOfEventsFlag() throws IOException {
        concertList.add(concertListWithoutTickets.get(0));
        given(message.getText()).willReturn(concertList.get(0).getArtist());
        given(eventService.getEventInformation(message.getText())).willReturn(concertList);
        given(eventService.getTicketInformation(concertListWithoutTickets.get(0))).willReturn(concertListWithoutTickets.get(0).getTickets());
        MessageForFront res = concert.getConcertInfo(message);
        assertThat(Serializers.deserialize(res.getPayload(), String.class))
                .isNotNull()
                .contains("По Вашему запросу найдены следующие мероприятия")
                .contains("1) Исполнитель: TWENTY ØNE PILØTS")
                .contains("2) Исполнитель: TWENTY ØNE PILØTS")
                .contains("12 Июльвс 19:00")
                .contains("28 Июльвс 19:00")
                .contains("Другое место")
                .contains("ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина")
                .doesNotContain("Хотите отслеживать");
        assertThat(res.getCallbackType()).isEqualTo(CallbackType.LIST_OF_EVENTS.getValue());
        assertThat(res.getNumberOfEvents()).isEqualTo(concertList.size());
    }

    @Test
    @DisplayName("корректное формирование сообщения на выбор пользователем определнного мероприятия из предложенного списка при отсуствии билетов")
    void correctlyShowsInformationAboutEventWhichIsChosenByUserWithoutTickets() throws IOException {
        String str1 = "1) Исполнитель: TwentyOne Pilot\nДата: 12 Майчт 19:00\nМесто проведения: Другое место\nСсылка: https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30;\n";
        String str2 = "2) Исполнитель: Aerosmith (Аэросмит)\nДата: 30 Июльчт 19:00\nМесто проведения: ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина\nСсылка: https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30";
        String res = concert.getTicketInfo(str1 + str2, 1);
        assertThat(res)
                .contains("Aerosmith")
                .doesNotContain("TwentyOne Pilot")
                .contains("30 Июльчт 19:00")
                .contains("Центральный стадион «Динамо»")
                .contains("Билеты на данное мероприятие не найдены")
                .contains("Хотите отслеживать появление");
    }

    @Test
    @DisplayName("корректное формирование сообщения на выбор пользователем определнного мероприятия из предложенного списка при наличии билетов")
    void correctlyShowsInformationAboutEventWhichIsChosenByUserWithTicket() throws IOException {
        String str1 = "1) Исполнитель: TwentyOne Pilot\nДата: 12 Майчт 19:00\nМесто проведения: Другое место\nСсылка: https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30;\n";
        String str2 = "2) Исполнитель: Aerosmith (Аэросмит)\nДата: 30 Июльчт 19:00\nМесто проведения: ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина\nСсылка: https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30";
        given(eventService.getTicketInformation(any())).willReturn(concertList.get(0).getTickets());
        String res = concert.getTicketInfo(str1 + str2, 0);
        assertThat(res)
                .contains("TwentyOne Pilot")
                .doesNotContain("Aerosmith")
                .contains("12 Майчт 19:00")
                .contains("Другое место")
                .contains("Минимальная стоимость билетов")
                .doesNotContain("Хотите отслеживать появление");
    }

    @Test
    @DisplayName("корректную обработку сообщения при ненайденном концерте в getTicketInfo")
    void correctlyHandleMessageWhenConcertIsNull() {
        String str = "";
        assertDoesNotThrow(() -> {
            assertThat(concert.getTicketInfo(str, 0))
                    .contains("Повторите пожалуйта запрос! Напишите какого исполнителя ");
        });
    }

    @Test
    @DisplayName("корректную обработку исключения парсинга концерта в getTicketInfo")
    void correctlyHandleRuntimeExceptionInGetTicketInfo() {
        String str = "Место проведения: skdajafd'";
        Throwable thrown = assertThrows(EventException.class, () -> {
            concert.getTicketInfo(str, 0);
        });
        assertThat(thrown).hasMessageContaining("Error with parsing concert");
    }


    @Test
    @DisplayName("корректно формирует пользователя для мониторинга")
    void correctlyCreateUserForMonitoring() {
        String str = "Исполнитель: TwentyOne Pilot\nДата: 12 Майчт 19:00\nМесто проведения: Другое место\nСсылка: https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30;";
        given(message.getText()).willReturn(str);
        User user = concert.monitorOfEvent(message);
        assertThat(user.getChatId()).isEqualTo(message.getChatId());
        assertThat(user.getConcert().getArtist()).isEqualTo("TwentyOne Pilot");
        assertThat(user.getConcert().getShouldBeMonitored()).isTrue();
        assertThat(user.getConcert().getDate()).isEqualTo("12 Майчт 19:00");
        assertThat(user.getConcert().getPlace()).isEqualTo("Другое место");
        assertThat(user.getDateOfMonitorFinish()).isBeforeOrEqualTo(new GregorianCalendar(2020, Calendar.MAY, 12).getTime());
        assertThat(user.getConcert().getOwner()).isEqualTo(user);
    }

}