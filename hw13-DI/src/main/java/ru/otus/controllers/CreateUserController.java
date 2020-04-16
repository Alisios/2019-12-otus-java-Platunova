package ru.otus.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.api.model.User;
import ru.otus.api.service.DBServiceUser;

@Controller
public class CreateUserController {
    private final DBServiceUser dbServiceUser;
    private final PasswordEncoder passwordEncoder;

    public CreateUserController(DBServiceUser dbServiceUser,PasswordEncoder passwordEncoder) {
        this.dbServiceUser = dbServiceUser;
        this.passwordEncoder=passwordEncoder;
    }

    @GetMapping({"/create"})
    public String viewAllUsers(Model model) {
        model.addAttribute("user", new User());
        return "createUser.html";
    }

    @PostMapping("/create")
    public RedirectView userSave(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // to hash the password during the user registration process
        dbServiceUser.saveUser(user);
        return new RedirectView("/users", true);
    }
}
