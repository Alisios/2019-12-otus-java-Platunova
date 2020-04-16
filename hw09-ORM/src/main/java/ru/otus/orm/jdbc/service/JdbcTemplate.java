package ru.otus.orm.jdbc.service;

public interface JdbcTemplate <T> {

    void create(T objectData);
    void update(T objectData) throws IllegalAccessException;
    void createOrUpdate(T objectData);
    <T> T load(long id, Class<T> clazz);

}
