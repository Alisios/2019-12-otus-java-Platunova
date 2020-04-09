package ru.otus.services;

import ru.otus.api.model.User;
import ru.otus.api.service.DBServiceUser;


public class UserAuthServiceImpl implements UserAuthService {
    private final String ADMIN = "admin";

    private final DBServiceUser dbServiceUser;

    public UserAuthServiceImpl(DBServiceUser dbServiceUser) {
        this.dbServiceUser = dbServiceUser;
    }

    @Override
    public boolean authenticate(String login, String password) {
        if (!login.equals(ADMIN)) return false;
        User user = dbServiceUser.getUser(login).orElse(null);
        return user != null && user.getPassword().equals(password);
    }
}
