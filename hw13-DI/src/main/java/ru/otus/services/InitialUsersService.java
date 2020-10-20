package ru.otus.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.api.model.User;
import ru.otus.api.service.DBServiceUser;

public class InitialUsersService {

    @Autowired
    private DBServiceUser dbServiceUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void initiateUsers() {
        dbServiceUser.saveUser(new User("Вася", 27, "vas", passwordEncoder.encode("11111")));
        dbServiceUser.saveUser(new User("Женя", 29, "zhenya", passwordEncoder.encode("11111")));
        dbServiceUser.saveUser(new User("Джон", 30, "admin", passwordEncoder.encode("11111")));
        dbServiceUser.saveUser(new User("Боб", 37, "bob", passwordEncoder.encode("bobpassword")));
        dbServiceUser.saveUser(new User("Энтони", 19, "ent", passwordEncoder.encode("ent123")));
        dbServiceUser.saveUser(new User("Рома", 33, "poma", passwordEncoder.encode("12345")));
        dbServiceUser.saveUser(new User("Саша", 35, "sashka", passwordEncoder.encode("12")));
        dbServiceUser.saveUser(new User("Эл", 28, "el123", passwordEncoder.encode("2222")));
    }
}
