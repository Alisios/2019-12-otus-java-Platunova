package ru.otus.orm.api.dao;

import java.util.Optional;

import ru.otus.orm.api.sessionmanager.SessionManager;

public interface UserDao<T> {
    Optional<T> findById(long id, Class clazz);

    void saveUser(T user);

    SessionManager getSessionManager();
}
