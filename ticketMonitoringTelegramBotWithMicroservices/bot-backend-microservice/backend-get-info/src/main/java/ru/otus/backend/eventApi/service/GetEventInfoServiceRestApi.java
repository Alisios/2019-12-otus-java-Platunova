package ru.otus.backend.eventApi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpHostConnectException;
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

/**
 * имплементация сервиса  получения информации о
 * событии через stub сайт
 **/

@Component("getEventInfoServiceRestApi")
@Slf4j
@RequiredArgsConstructor
public class GetEventInfoServiceRestApi implements GetEventInfoService {
    private final EventRestService eventRestService;

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
            log.error("Impossible to connect to server or URL is incorrect: {}\n{}\n", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
            throw new UnknownHostException("Impossible to connect to server or URL is incorrect: " + ex.getMessage());
        } catch (IOException ex) {
            log.error("Problems with server {}\n{}\n", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
            throw new IOException("Problems with server: " + ex.getMessage());
        }
    }

    @Override
    public List<TicketModel> getTicketInformation(ConcertModel concert) throws IOException {
        List<ConcertRestModel> list = eventRestService.getTickets(concert.getArtist(), concert.getDate(), concert.getPlace());
        if (list == null || list.isEmpty())
            return Collections.emptyList();
        else return list.get(0).getTickets();
    }
}
