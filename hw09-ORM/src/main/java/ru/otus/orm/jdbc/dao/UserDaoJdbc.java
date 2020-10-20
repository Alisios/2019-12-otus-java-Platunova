package ru.otus.orm.jdbc.dao;

import java.sql.Connection;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.orm.api.dao.UserDao;
import ru.otus.orm.api.dao.UserDaoException;
import ru.otus.orm.api.sessionmanager.SessionManager;
import ru.otus.orm.jdbc.service.UserDaoJdbcTemplate;
import ru.otus.orm.jdbc.sessionmanager.SessionManagerJdbc;

public class UserDaoJdbc<T> implements UserDao<T> {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoJdbc.class);

    private final SessionManagerJdbc sessionManager;
    private final UserDaoJdbcTemplate<T> userDaoJdbcTemplate;

    public UserDaoJdbc(SessionManagerJdbc sessionManager, UserDaoJdbcTemplate<T> userDaoJdbcTemplate) {
        this.sessionManager = sessionManager;
        this.userDaoJdbcTemplate = userDaoJdbcTemplate;
    }

    @Override
    public Optional<T> findById(long id, Class clazz) {
        try {
            return userDaoJdbcTemplate.load(id, clazz);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void saveUser(T user) {
        try {
            userDaoJdbcTemplate.createOrUpdate(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    private Connection getConnection() {
        return sessionManager.getCurrentSession().getConnection();
    }
}
