package ru.otus.db;

import ru.otus.backend.model.User;

import java.util.List;

public interface DBServiceCacheTest {

    List<User> getAllUsers();
    long saveUser(User user);
    List<User>getUsersForNotifying();
    User delete (long id);
}
