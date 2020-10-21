package ru.otus.backend.handlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.eventApi.parsers.HtmlParser;
import ru.otus.backend.eventApi.parsers.HtmlParserRedKassaRu;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResKassaTest {
    private static Logger logger = LoggerFactory.getLogger(ResKassaTest.class);
    private HtmlParser htmlParser = new HtmlParserRedKassaRu();
    private final String URL_KASSIR = "https://redkassa.ru/events?q="; //"https://ponominalu.ru/";//
    private List<String> artists;

    @DisplayName("находит требуемое событие")
    @ParameterizedTest
    @ValueSource(strings = {"TWENTY ØNE PILØTS", "кис-кис ", " the beatles", "сплин", "the killers"})
    void correctlyFindTheInformation(String message) throws IOException {
        ConcertModel concertModel = mock(ConcertModel.class);
        when(concertModel.getConcertUrl()).thenReturn("https://redkassa.ru/events/bilety_na_concert_elizium_glavclub/20-06-2020/19-00");
        htmlParser.getTicketInfoFromHtml(concertModel);
        ConcertModel conc = htmlParser.getEventsFromHtml(message).get(0);
        logger.info("{}", conc);
        assertThat(conc.getArtist().toLowerCase().contains(message.toLowerCase()));

    }


    @DisplayName("корректно реагирует на the или на отсутсвие события")
    @Test
    void correctlyReactsOnTheOrEventAbsence() throws IOException {
        assertThat(htmlParser.getEventsFromHtml("the")).isEmpty();
        assertThat(htmlParser.getEventsFromHtml("aerosmith")).isEmpty();
        assertThat(htmlParser.getEventsFromHtml("the beatles").get(0).getArtist().toLowerCase()).contains("beatles");
    }


    private final String URL_KASSIR2 = "https://ponominalu.ru/";//
    private List<String> artists2;


    public Elements getDocument(String message) throws IOException {
        String mesageUrl = "";
        artists2 = Arrays.asList(message.trim().split(" "));
        if (artists.size() == 0) {
            return null;
        } else if (artists2.size() == 1)
            mesageUrl = message;
        else {
            if (artists.get(0).toLowerCase().equals("the")) {
                if (artists2.size() == 2)
                    mesageUrl = artists2.get(1);
                else {
                    artists2.remove(0);
                    mesageUrl = String.join("%20", artists);
                }
            } else
                mesageUrl = String.join("%20", artists);
        }
        try {
            Document doc =
                    Jsoup.connect(URL_KASSIR2 + mesageUrl)
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get();

            return doc.select("#catalog-items-js");
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException("Problems with parsing URL , URL may be incorrect" + ex.getMessage());
        }
    }

}
