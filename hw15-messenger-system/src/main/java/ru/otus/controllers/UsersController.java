package ru.otus.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.api.model.User;
import ru.otus.front.FrontendService;


@Controller
public class UsersController {
    private static Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final FrontendService frontendService;
    private final SimpMessagingTemplate template;

    public UsersController(SimpMessagingTemplate template, FrontendService frontendService) {
        this.template = template;
        this.frontendService = frontendService;
    }

    @GetMapping({"/users"})
    public String view() {
        return "users.html";
    }


    @MessageMapping({"/users"})
    public void viewWS() {
        frontendService.getUsers(users -> {
            logger.info("message from DB is received: {}", users);
            template.convertAndSend("/topic/users", users);
        });
    }

    @MessageMapping("/create")
    public void createUser(User user) {
        logger.info("from /create {}", user);
        frontendService.saveUser(user, userSaved -> this.template.convertAndSend("/topic/create", userSaved));
    }
}
