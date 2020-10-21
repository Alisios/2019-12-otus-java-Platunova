package ru.otus.backend.handlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.backend.eventApi.parsers.HtmlParser;
import ru.otus.backend.eventApi.parsers.HtmlParserKassirRu;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Тесты проверяют: ")
class HtmlParserTest {

    private HtmlParser htmlParser  = new HtmlParserKassirRu();


    @DisplayName("корректно реагирует на неправильную ссылку или поменявшуюся разметку")
    @ParameterizedTest
    @ValueSource(strings = {"AEROSMITH", "Элизиум"})
    void correctlyReactOnWrongURL(String message) throws IOException {
        Throwable exception = assertThrows(IOException.class, () -> {
            String mesageUrl;
            mesageUrl = message;
            Document doc =
                    Jsoup.connect("https://ponominalu.ru/"+ mesageUrl)
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get();
            doc.select("body > div.site > main > div.container.page-category.inner-page > div.js-filter-content.js-pager-container > div > div");
        });
        assertThat(exception).isNotInstanceOf(UnknownHostException.class);
        assertThat(exception.getMessage()).contains("HTTP error fetching URL");

        String URL_KASSIR = "https://ponominalu.ru/";//
        ConcertModel concertModel = mock(ConcertModel.class);
        when(concertModel.getConcertUrl()).thenReturn(URL_KASSIR);
        assertThat(htmlParser.getTicketInfoFromHtml(concertModel)).isEmpty();
    }

    @DisplayName("корректную загрузку  результатов согласно запросу пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"TWENTY ØNE PILØTS"})
    void correctlyGetDocumentAccordingToUserSearch(String message) throws IOException {
       List<ConcertModel> ConcList = htmlParser.getEventsFromHtml(message);
       assertNotNull(ConcList);
       assertEquals(message, ConcList.get(0).getArtist());
    }

    @DisplayName("корректную загрузку списка результатов согласно запросу пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"beatles", " кис кис"})
    void correctlyGetListDocumentAccordingToUserSearch(String message) throws IOException {
        List<ConcertModel> ConcList = htmlParser.getEventsFromHtml(message);
        assertNotNull(ConcList);
        assertThat(ConcList.size()>1).isTrue();
    }

    @ParameterizedTest
    @DisplayName("корректное формирование экзмепляра мероприятия исходя из запроса пользователя")
    @MethodSource("generateData")
    void correctlyGetEventsFromHtmlAccordingToUserSearch(String message, ConcertModel concert) throws IOException {
        assertEquals(htmlParser.getEventsFromHtml(message).get(0).getArtist(), concert.getArtist());
    }

    @Test
      void correctlyGetEventsFromHtmlAccordingToUserSearch2() throws IOException {
        ConcertModel c = htmlParser.getEventsFromHtml("twenty").get(0);
        System.out.println(c);
        List<TicketModel> elementList =  htmlParser.getTicketInfoFromHtml(c);
        System.out.println(elementList.toString());
    }

    @DisplayName("корректное формирование экзмепляра билета исходя из запроса пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"https://msk.kassir.ru/koncert/twenty-one-pilots#199390", "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19"})
    void correctlyGetTicketsFromHtmlAccordingToUserSearch(String strings) throws IOException {
        ConcertModel concertModel = mock(ConcertModel.class);
        when(concertModel.getConcertUrl()).thenReturn(strings);
        List<TicketModel> elementList =  htmlParser.getTicketInfoFromHtml(concertModel);
      System.out.println(elementList);
      assertNotNull(elementList);
    }

    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("TWENTY ONE PILOT", new ConcertModel("TWENTY ØNE PILØTS",
                        "11 Июль 2021вс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390")),
                Arguments.of("Aerosmith", new ConcertModel("Aerosmith (Аэросмит)",
                        "29 Май 2021сб 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30")));
//                Arguments.of("Green day", new ConcertModel("Green Day",
//                        "24 Майвс 19:00",
//                        "Стадион \"Открытие Арена\"",
//                        "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19")));
    }
}