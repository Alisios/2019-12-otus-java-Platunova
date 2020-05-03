package ru.otus.orm.jdbc.service;

import java.util.Optional;

public interface JdbcTemplate <T> {

    void create(T objectData);
    void update(T objectData) throws IllegalAccessException;
    void createOrUpdate(T objectData);
     Optional<T> load(long id, Class<?> clazz);

}
