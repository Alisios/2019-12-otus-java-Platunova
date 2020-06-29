package ru.otus.backend.handlers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.eventApi.parsers.HtmlParser;
import ru.otus.backend.eventApi.parsers.HtmlParserConcertRu;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import java.io.IOException;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для парсера Concert.ru. Тесты проверяют:")
class ConcertRuTest {
    private static Logger logger = LoggerFactory.getLogger(ConcertRuTest.class);
    private HtmlParser htmlParser = new HtmlParserConcertRu();
    private final String URL_KASSIR = "https://www.concert.ru/Search?q=";
    private List<String> artists;

    @DisplayName("корректное формирование информации о событии, билетах и уточнении даты события при поиске билетов")
    @ParameterizedTest
    @ValueSource(strings = {"TWENTY ONE PILOTS", "ария", "мельница", "доброфест"})
//the beatles","Элизиум", "кис-кис",
    void correctlyReactsOnTheOrEventAbsence(String message) throws IOException {
        List<ConcertModel> listConcert = htmlParser.getEventsFromHtml(message);
        assertThat(listConcert).isNotEmpty();
        assertThat(listConcert.get(0))
                .satisfies(s -> {
                    assertThat(s).isNotNull();
                    assertThat(s).hasFieldOrPropertyWithValue("shouldBeMonitored", false);
                    assertThat(s).hasNoNullFieldsOrProperties();
                    assertThat(s.getArtist().toLowerCase()).contains(message.toLowerCase());
                });
        final ConcertModel temp = new ConcertModel(listConcert.get(0).getArtist(),
                listConcert.get(0).getDate(), listConcert.get(0).getPlace(), listConcert.get(0).getConcertUrl());
        List<TicketModel> listTicket = htmlParser.getTicketInfoFromHtml(listConcert.get(0));

        assertThat(listTicket).isNotEmpty();
        assertThat(listTicket.get(0))
                .satisfies(s -> {
                    assertThat(s).isNotNull();
                    assertThat(s).hasNoNullFieldsOrProperties();
                });
        assertAll("concertComparison",
                () -> assertThat(temp.getArtist()).isEqualTo(listConcert.get(0).getArtist()),
                () -> assertThat(temp.getPlace()).isEqualTo(listConcert.get(0).getPlace()),
                () -> assertThat(temp.getDate()).isNotEqualTo(listConcert.get(0).getDate()),
                () -> assertThat(temp.getConcertUrl()).isEqualTo(listConcert.get(0).getConcertUrl()));
        logger.info("listConcert: {}", listConcert.get(0));
        logger.info("TicketConcert: {}", listTicket);
    }

    @DisplayName("корректную реакцию программы на нестандартные события")
    @Test
    void correctlyReactsOnTheUnusualEvents() throws IOException {
        assertThat(htmlParser.getEventsFromHtml("")).isEmpty();
        assertThat(htmlParser.getEventsFromHtml(null)).isEmpty();
        ConcertModel concert = mock(ConcertModel.class);
        when(concert.getConcertUrl()).thenReturn("jsdhanjdknDJS");
        assertThat(htmlParser.getTicketInfoFromHtml(null)).isEmpty();
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            htmlParser.getTicketInfoFromHtml(concert);
        });
        assertThat(exception.getMessage()).contains("The URL ");
        assertThat(htmlParser.getEventsFromHtml("the")).isEmpty();
        assertThat(htmlParser.getEventsFromHtml("aerosmith")).isEmpty();
        assertThat(htmlParser.getEventsFromHtml("the beatles")).size().isGreaterThan(1);
        assertThat(htmlParser.getEventsFromHtml("мельница")).size().isGreaterThan(1);
    }
}