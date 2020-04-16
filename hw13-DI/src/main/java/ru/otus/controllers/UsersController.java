package ru.otus.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.api.model.User;
import ru.otus.api.service.DBServiceUser;

import java.util.List;

@Controller
public class UsersController {

    private final DBServiceUser dbServiceUser;

    public UsersController(DBServiceUser dbServiceUser) {
        this.dbServiceUser = dbServiceUser;
    }

    @GetMapping({"/users"})
    public String viewAllUsers(Model model) {
        List<User> users = dbServiceUser.getAllUsers();
        model.addAttribute("tableOfUsers", users);
        return "users.html";
    }

    @PostMapping("/users")
    public RedirectView userSave() {
        return new RedirectView("/create", true);
    }

}
