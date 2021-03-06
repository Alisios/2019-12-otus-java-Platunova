package ru.otus.db.service;

import ru.otus.backend.model.User;
import java.util.List;
import java.util.Optional;

public interface DBServiceUser   {

  User saveUser(User user);

  Optional<User> getUser(long id);
  Optional<User> findByChatIdAndArtist(User user);
  List<User> getAllUsers();
  List<User> getUsersForNotifying();
  void delete (long id);
  void deleteAllUsers();

}
