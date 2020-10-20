package ru.otus.orm.api.service;

import java.util.Optional;

public interface DBServiceUser<T> {

    void saveUser(T user);

    Optional<T> getUser(long id, Class<T> clazz);

}
