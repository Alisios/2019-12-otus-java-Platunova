package ru.otus.backend.eventApi.service;

import org.apache.http.conn.HttpHostConnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.backend.eventApi.rest.EventRestService;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@DisplayName("Тесты проверяют: ")
class GetEventInfoServiceRestApiTest {

    private final EventRestService eventRestService = mock(EventRestService.class);
    private final GetEventInfoServiceRestApi getEventInfoServiceRestApi = new GetEventInfoServiceRestApi(eventRestService);
    private List<ConcertRestModel> userList;

    @BeforeEach
    void set() {
        initiateForChecking();
    }

    @Test
    @DisplayName("корректную обработку полученного сообщения и создание сущности ConcertModel")
    void correctlyHandlingOfReceivedInformationAndCorrectlyCreatingOfConcertEntity() throws IOException {
        when(eventRestService.getEventByArtist(any())).thenReturn(userList);
        List<ConcertModel> list = getEventInfoServiceRestApi.getEventInformation("Элизиум");
        assertThat(list).isNotEmpty();
        assertThat(list.size()).isEqualTo(userList.size());
        assertThat(list.get(0).getArtist()).isEqualTo(userList.get(0).getArtist());
        assertThat(list.get(0).getDate()).isEqualTo(userList.get(0).getDate());
        assertThat(list.get(0).getConcertUrl()).isEqualTo(userList.get(0).getConcertUrl());
        assertThat(list.get(0).getTickets()).isEqualTo(userList.get(0).getTickets());
    }

    @DisplayName("корректное поведение при бросании исключений ")
    @Test
    void correctlyWorksWithExceptions() throws IOException {
        when(eventRestService.getEventByArtist("Metallica")).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> getEventInfoServiceRestApi.getEventInformation("Metallica"));
        when(eventRestService.getEventByArtist("Элизиум")).thenThrow(HttpHostConnectException.class);
        assertThrows(UnknownHostException.class, () -> getEventInfoServiceRestApi.getEventInformation("Элизиум"));
        when(eventRestService.getEventByArtist("Beatles")).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> getEventInfoServiceRestApi.getEventInformation("Beatles"));
    }

    @Test
    @DisplayName("корректную выдачу списка билетов")
    void correctlyFormListOfTicket() throws IOException {
        ConcertModel conc = new ConcertModel("TWENTY ØNE PILØTS",
                "12 Июльвс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                List.of(new TicketModel("Фанзона", "1200руб"), new TicketModel("Танцпартер", "1200руб"), new TicketModel("С32", "2400руб")));
        ConcertRestModel conc2 = new ConcertRestModel("TWENTY ØNE PILØTS",
                "12 Июльвс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                List.of(new TicketModel("Фанзона", "1200руб"), new TicketModel("Танцпартер", "1200руб"), new TicketModel("С32", "2400руб")));

        when(eventRestService.getTickets(anyString(), anyString(), anyString())).thenReturn(List.of(conc2));
        assertThat(getEventInfoServiceRestApi.getTicketInformation(conc))
                .isNotEmpty()
                .isEqualTo(conc.getTickets());
    }

    @Test
    @DisplayName("корректно уточняет информацию о билетах по выбранному исполнителю")
    void correctlyClarifyInformationOfTickets() throws IOException {
        ConcertModel conc = new ConcertModel("TWENTY ØNE PILØTS",
                "12 Июльвс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                List.of(new TicketModel("Фанзона", "1200руб"), new TicketModel("Танцпартер", "1200руб"), new TicketModel("С32", "2400руб")));
        ConcertRestModel conc2 = new ConcertRestModel("TWENTY ØNE PILØTS",
                "12 Июльвс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                List.of(new TicketModel("Фанзона", "1200руб"), new TicketModel("Танцпартер", "1200руб"), new TicketModel("С32", "2400руб")));

        when(eventRestService.getTickets(anyString(), anyString(), anyString())).thenReturn(List.of(conc2));
        assertThat(getEventInfoServiceRestApi.getTicketInformation(conc)).isNotEmpty().isEqualTo(conc.getTickets());
    }


    private void initiateForChecking() {
        userList = new ArrayList<>(List.of(
                new ConcertRestModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                        List.of(new TicketModel("Фанзона", "1200руб"), new TicketModel("Танцпартер", "1200руб"), new TicketModel("С32", "2400руб"))),
                new ConcertRestModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20",
                        List.of(new TicketModel("Фанзона", "1200руб"), new TicketModel("Танцпартер", "1200руб"), new TicketModel("С32", "2400руб"))),
                new ConcertRestModel("кис-кис",
                        "31 деВт 20:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20",
                        List.of(new TicketModel("С4", "12000руб"), new TicketModel("С5", "1200руб"), new TicketModel("С32", "2400руб"))),
                new ConcertRestModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30",
                        List.of(new TicketModel("Фанзона", "1200руб"), new TicketModel("Танцпартер", "1200руб"), new TicketModel("С32", "2400руб")))));

        userList.forEach(user -> user.getTickets().forEach((ticket -> ticket.setOwner(user))));
    }

}