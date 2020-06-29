package ru.otus.helper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.service.DbService;

import java.util.ArrayList;
import java.util.List;

public class InitiateConcerts {

    @Autowired
    private DbService dbService;

    private void initiateForChecking(){
        List<ConcertRestModel> userList = new ArrayList<>(List.of(
                new ConcertRestModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                        List.of(new TicketModel("Фанзона", "5000руб"), new TicketModel("h6", "1200руб"),new TicketModel("t5","1200руб"),new TicketModel("С32", "2400руб"))),
                new ConcertRestModel("TWENTY ØNE PILØTS",
                        "15 Июльвс 19:00",
                        "Другое место",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390",
                        List.of(new TicketModel("h6", "1200руб"),new TicketModel("t5","1200руб"),new TicketModel("С32", "2400руб"))),
                new ConcertRestModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20",
                        List.of(new TicketModel("Фанзона", "1200руб"),new TicketModel("Танцпартер","1200руб"),new TicketModel("С32", "2400руб"))),
                new ConcertRestModel("кис-кис",
                        "31 деВт 20:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20",
                        List.of(new TicketModel("С4", "12000руб"),new TicketModel("С5","1200руб"),new TicketModel("С32", "2400руб"))),
                new ConcertRestModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30",
                        List.of(new TicketModel("Фанзона", "1200руб"),new TicketModel("Танцпартер","1200руб"),new TicketModel("С32", "2400руб")))));

        userList.forEach(user->user.getTickets().forEach((ticket->ticket.setOwner(user))));
        userList.forEach(dbService::saveConcert); //для проверки мониторинга
        // return userList;
    }
}
