package ru.otus.api.dao;

import ru.otus.api.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
  Optional<User> findById(long id);

  long saveUser(User user);

  Optional<User> findByLogin(String login);

  List<User> findAllUser();
}