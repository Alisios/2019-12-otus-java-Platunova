package ru.otus.helpers;

import ru.otus.backend.model.TicketModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TicketParser {

    public static List<TicketModel> parseTheTickets(String ticketsToString) {
        List<String> list = Arrays.asList(
                ticketsToString
                        .replaceAll("\\[", "")
                        .replaceAll("\\]", "")
                        .split("\\., "));
        List<TicketModel> listModel = new ArrayList<>();
        list.forEach((ticket -> {
            listModel.add(new TicketModel(ticket.trim().split(": ")[0], ticket.split(": ")[1]));
        }));
        return listModel;
    }
}
