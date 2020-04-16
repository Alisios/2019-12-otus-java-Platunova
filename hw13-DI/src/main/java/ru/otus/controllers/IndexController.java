package ru.otus.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {
    @GetMapping({"/"})
    public String viewIndexPage() {
        return "index.html";
    }


//    @PostMapping({"/logout"})
//    public String  viewLogout() {
//        return "index.html";
//    }
}
