package ru.otus.api.service;

import java.util.Optional;

public interface DBServiceUser <T>  {

  long saveUser(T user);

  Optional<T> getUser(long id);

}
