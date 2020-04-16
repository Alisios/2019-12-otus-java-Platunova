package ru.otus.orm.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.orm.api.dao.UserDao;
import ru.otus.orm.api.sessionmanager.SessionManager;

import java.util.Optional;

public class DbServiceUserImpl <T> implements DBServiceUser <T> {
  private static Logger logger = LoggerFactory.getLogger(DbServiceUserImpl.class);

  private final UserDao userDao ;

  public DbServiceUserImpl(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public void saveUser(T user) {
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        userDao.saveUser(user);
        sessionManager.commitSession();
        logger.info("user is created");
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        sessionManager.rollbackSession();
        throw new DbServiceException(e);
      }
    }
  }

  @Override
  public Optional<T> getUser(long id, Class clazz) {
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        Optional<T> userOptional = userDao.findById(id, clazz);
        logger.info("user: {}", userOptional.orElse(null));
        return userOptional;
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        sessionManager.rollbackSession();
      }
      return Optional.empty();
    }
  }

}
