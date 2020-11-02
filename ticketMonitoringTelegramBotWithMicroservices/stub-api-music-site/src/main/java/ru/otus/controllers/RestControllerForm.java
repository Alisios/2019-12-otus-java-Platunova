package ru.otus.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.helpers.TicketParser;
import ru.otus.service.ConcertService;

import java.util.List;

import static ru.otus.helpers.TicketParser.parseTheTickets;

@Controller
@RequiredArgsConstructor
public class RestControllerForm {

    private final ConcertService dbService;

    @RequestMapping("concert/new")
    public String newProduct(Model model) {
        model.addAttribute("concert", new ConcertRestModel());
        return "concertform";
    }

    @RequestMapping(value = {"concert"}, method = RequestMethod.POST)
    public String saveProduct(ConcertRestModel concert) {
        concert.setTickets(parseTheTickets(concert.getTicketsToString()));
        dbService.saveConcert(concert);
        return "redirect:/concert/" + concert.getId();
    }

    @RequestMapping("concert/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        model.addAttribute("concert", dbService.getConcertById(id).get());
        return "concertshow";
    }

    @RequestMapping(value = "/concerts", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("concerts", dbService.getAllConcerts());
        return "concerts";
    }

    //необязательный метод, в принципе таблица с tickets вообще не особо нужна
    @RequestMapping(value = "/initiate", method = RequestMethod.GET)
    public String initiate(Model model) {
        List<ConcertRestModel> list = dbService.getAllConcerts();
        list.forEach(conc -> {
            conc.addTickets(TicketParser.parseTheTickets(conc.getTicketsToString()));
        });
        dbService.saveAll(list);
        model.addAttribute("concerts", list);
        return "concerts";
    }

    @RequestMapping("concert/delete/{id}")
    public String delete(@PathVariable Long id) {
        dbService.deleteConcertById(id);
        return "redirect:/concerts";
    }

    @RequestMapping("concert/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("concert", dbService.getConcertById(id).get());
        return "concertform";
    }


}
