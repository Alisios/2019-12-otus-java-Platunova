package ru.otus.backend.eventApi.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.configurations.RestProperties;

import java.io.IOException;
import java.util.List;

@Service("eventRestServiceImpl")
@AllArgsConstructor
public class EventRestServiceImpl implements EventRestService {
    private static final Logger logger = LoggerFactory.getLogger(EventRestServiceImpl.class);
    private final RestTemplate restTemplate;
    private final RestProperties restProperties;

    @Override
    public List<ConcertRestModel> getEventByArtist(@JsonProperty("artist") String artist) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(restProperties.getScheme())
                .host(restProperties.getHost())
                .path(restProperties.getPathConcert())
                .query("artist={keyword}").buildAndExpand(artist);
        HttpEntity<?> requestParam = new HttpEntity<>(headers);
        return requestToServer(uriComponents, requestParam);
    }

    @Override
    public List<ConcertRestModel> getTickets(@JsonProperty("concert") String artist,
                                             @JsonProperty("date") String date,
                                             @JsonProperty("place") String place) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(restProperties.getScheme())
                .host(restProperties.getHost())
                .path(restProperties.getPathTicket())
                .queryParam("concert", artist)
                .queryParam("date", date)
                .queryParam("place", place)
                .buildAndExpand(artist, date, place);
        HttpEntity<?> requestParam = new HttpEntity<>(headers);
        return requestToServer(uriComponents, requestParam);
    }

    private List<ConcertRestModel> requestToServer(UriComponents uriComponents, HttpEntity<?> requestParam) throws IOException {
        ResponseEntity<List<ConcertRestModel>> response = restTemplate.exchange(
                uriComponents.toUriString(),
                HttpMethod.GET,
                requestParam,
                new ParameterizedTypeReference<>() {
                });
        logger.info("Response from server: {}, {}", response.getStatusCode(), response.getBody());
        if (response.getStatusCode() == HttpStatus.OK)
            return response.getBody();
        else
            throw new IOException("Impossible to get information. Server error: " + response.getStatusCode());
    }
}
