package ru.otus.backend.eventApi.service;

import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.backend.eventApi.rest.EventRestService;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**имплементация сервиса  получения информации о
 * событии через stub сайт
 **/

@Component("getEventInfoServiceRestApi")
public class GetEventInfoServiceRestApi implements GetEventInfoService {
    private static final Logger logger = LoggerFactory.getLogger(GetEventInfoServiceRestApi.class);
    private final EventRestService eventRestService;

    GetEventInfoServiceRestApi(EventRestService eventRestService) {
        this.eventRestService = eventRestService;
    }

    @Override
    public List<ConcertModel> getEventInformation(String message) throws IOException {
        try {
            List<ConcertModel> concertList = new ArrayList<>();
            eventRestService.getEventByArtist(message).forEach((concert) -> {
                concertList.add(new ConcertModel(concert.getArtist(),
                        concert.getDate(), concert.getPlace(), concert.getConcertUrl(), concert.getTickets()));
            });
            return concertList;
        } catch (UnknownHostException | HttpHostConnectException ex) {
            logger.error("Impossible to connect to server or URL is incorrect: {}\n{}\n", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
            throw new UnknownHostException("Impossible to connect to server or URL is incorrect: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("Problems with server {}\n{}\n", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
            throw new IOException("Problems with server: " + ex.getMessage());
        }
    }

    @Override
    public List<TicketModel> getTicketInformation(ConcertModel concert) throws IOException {
        List <ConcertRestModel> list = eventRestService.getTickets(concert.getArtist(),concert.getDate(),concert.getPlace());
        if (list==null || list.isEmpty() )
            return Collections.emptyList();
        else return list.get(0).getTickets();
    }
}
