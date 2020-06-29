package ru.otus.controllers;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.helpers.TicketParser;
import ru.otus.service.DbService;

import java.util.List;


@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
public class EventController {
    private final DbService dbService;

    @GetMapping
    List<ConcertRestModel> getAllConcerts() {
        return dbService.getAllConcerts();
    }

    @PostMapping("/concerts")
    ResponseEntity<List<ConcertRestModel>> postConcertByArtist(@RequestBody String artist) {
        log.info("Artist from client: {}", artist);
        List<ConcertRestModel> concert = dbService.getConcertByArtist(artist);
        return ResponseEntity.ok().body(concert);
    }

    @GetMapping(value = "/concerts")
    ResponseEntity<List<ConcertRestModel>> getConcert(@RequestParam(name = "artist", defaultValue = "") String artist) {
        log.info("Artist from client: {}", artist);
        List<ConcertRestModel> concert = dbService.getConcertByArtist(artist);
        log.info("Concert from db: {}", concert);
        return ResponseEntity.ok().body(concert);
    }

    @GetMapping(value = "/tickets")
    ResponseEntity<List<ConcertRestModel>> getTicket(@RequestParam(name = "concert", defaultValue = "") String artist,
                                                     @RequestParam(name = "date", defaultValue = "") String date,
                                                     @RequestParam(name = "place", defaultValue = "") String place) {
        log.info("Artist from client: {} {} {}", artist, date, place);
        List<ConcertRestModel> concert2 = dbService.findTickets(artist, date, place);
        concert2.forEach(conc-> {
            if (conc.getTickets().size()==0)
                conc.setTickets(TicketParser.parseTheTickets(conc.getTicketsToString()));
        });
        log.info("Concert from db: {}", concert2);
        return ResponseEntity.ok().body(concert2);
    }

}
