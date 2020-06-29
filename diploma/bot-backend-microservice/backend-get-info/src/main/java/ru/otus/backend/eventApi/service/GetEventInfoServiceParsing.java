package ru.otus.backend.eventApi.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.otus.backend.eventApi.parsers.HtmlParser;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.util.List;

/**имплементация сервиса  получения информации о
 * событии путем парсинга агрегаторов билетов
 * (по умолчанию kassir.ru есть возможность выбрать другой)
 **/

@Service
@Qualifier("getEventInfoServiceParsing")
public class GetEventInfoServiceParsing implements GetEventInfoService {

    private final HtmlParser htmlParser;

    public GetEventInfoServiceParsing(@Qualifier("htmlParserKassirRu") HtmlParser htmlParser) {
        this.htmlParser = htmlParser;
    }

    @Override
    public List<ConcertModel> getEventInformation(String message) throws IOException {
        return htmlParser.getEventsFromHtml(message);
    }

    @Override
    public List<TicketModel> getTicketInformation(ConcertModel concertModel) throws IOException {
        return htmlParser.getTicketInfoFromHtml(concertModel);
    }
}
