package ru.otus.db.hibernate.dao;

import ru.otus.db.hibernate.sessionmanager.SessionManager;
import ru.otus.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
  Optional<User> findById(long id);

  long saveUser(User user);

  SessionManager getSessionManager();

//  Optional<User> findByLogin(String login);
  List<User> findAllUserForNotifying();
  Optional <User> delete(long id);

  List<User> findAllUser();
}