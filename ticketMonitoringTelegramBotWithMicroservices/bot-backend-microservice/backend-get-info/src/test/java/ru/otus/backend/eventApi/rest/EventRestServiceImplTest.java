package ru.otus.backend.eventApi.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест проверяет: ")
class EventRestServiceImplTest {

    @Test
    @DisplayName("корректное создание ссылки при поиске по исполнителю")
    public void constructArtistUri() {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost:8080")
                .path("/events/concert/")
                .query("artist={keyword}").buildAndExpand("Beatles");
        assertEquals("http://localhost:8080/events/concert/?artist=Beatles", uriComponents.toUriString());
    }

    @Test
    @DisplayName("корректное создание ссылки при поиске по исполнителю дате и месту")
    public void constructClarifyingUri() {
        String artist = "artist";
        String date = "date";
        String place = "place";
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost:8080")
                .path("/events/tickets/")
                .queryParam(artist, "artist")
                .queryParam(date, "date")
                .queryParam(place, "place")
                .buildAndExpand(artist, date, place);
        assertEquals("http://localhost:8080/events/tickets/?artist=artist&date=date&place=place", uriComponents.toUriString());
    }
}