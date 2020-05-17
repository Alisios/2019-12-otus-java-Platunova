package ru.otus.backend.handlers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты проверяют: ")
class HtmlParserTest {

    private HtmlParser htmlParser  = new HtmlParserKassirRu();

    @DisplayName("корректную загрузку  результатов согласно запросу пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"TWENTY ØNE PILØTS", "Элизиум"})
    void correctlyGetDocumentAccordingToUserSearch(String message)  {
       List<ConcertModel> ConcList = htmlParser.getEventsFromHtml(message);
       assertNotNull(ConcList);
       assertEquals(message, ConcList.get(0).getArtist());
    }

    @DisplayName("корректную загрузку списка результатов согласно запросу пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"beatles", " кис кис"})
    void correctlyGetListDocumentAccordingToUserSearch(String message)  {
        List<ConcertModel> ConcList = htmlParser.getEventsFromHtml(message);
        assertNotNull(ConcList);
        assertThat(ConcList.size()>1).isTrue();
    }

    @ParameterizedTest
    @DisplayName("корректное формирование экзмепляра мероприятия исходя из запроса пользователя")
    @MethodSource("generateData")
    void correctlyGetEventsFromHtmlAccordingToUserSearch(String message, ConcertModel concert) {
        assertEquals(htmlParser.getEventsFromHtml(message).get(0), concert);
    }

    @DisplayName("корректное формирование экзмепляра билета исходя из запроса пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"https://msk.kassir.ru/koncert/twenty-one-pilots#199390", "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19"})
    void correctlyGetTicketsFromHtmlAccordingToUserSearch(String strings) {
      List<TicketModel> elementList =  htmlParser.getTicketInfoFromHtml(strings);
      System.out.println(elementList);
      assertNotNull(elementList);
    }

    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("TWENTY ONE PILOT", new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390")),
                Arguments.of("Aerosmith", new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30")),
                Arguments.of("Элизиум", new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20")),
                Arguments.of("Green day", new ConcertModel("Green Day",
                        "24 Майвс 19:00",
                        "Стадион \"Открытие Арена\"",
                        "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19")));
    }
}