package ru.otus.db.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.model.User;
import ru.otus.db.hibernate.dao.UserDao;
import ru.otus.db.hibernate.sessionmanager.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

//@Service
public class DbServiceUserImpl implements DBServiceUser {
  private static Logger logger = LoggerFactory.getLogger(DbServiceUserImpl.class);

  private final UserDao userDao ;

  public DbServiceUserImpl(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public long saveUser(User user) {
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        long userId = userDao.saveUser(user);
        sessionManager.commitSession();
        logger.info("created user: {}", userId);
        return userId;
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        sessionManager.rollbackSession();
        throw new DbServiceException(e);
      }
    }
  }


  @Override
  public Optional<User> getUser(long id) {
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        Optional <User> userOptional = userDao.findById(id);
        logger.info("user: {}", userOptional.orElse(null));
        return userOptional;
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        sessionManager.rollbackSession();
      }
      return Optional.empty();
    }
  }

  @Override
  public List<User> getUsersForNotifying(){
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        List<User> users = userDao.findAllUserForNotifying();
        logger.info("users for notifying: {}", users.toString());
        return users;
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        sessionManager.rollbackSession();
      }
      return Collections.emptyList();
    }
  }

  @Override
  public Optional <User> delete(long id){
        try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        Optional<User> userOptional = userDao.delete(id);
        logger.info("user deleted: {}", userOptional.orElse(null));
        return userOptional;
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        sessionManager.rollbackSession();
      }
      return Optional.empty();
    }

  }
  @Override
  public Optional<User> findByChatIdAndArtist(User user){
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        logger.info("if User {} is already in the Table", user);
        return  userDao.findByChatIdAndArtist(user);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        sessionManager.rollbackSession();
      }
      return Optional.empty();
    }
  }



  @Override
  public List<User> getAllUsers(){
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
          List<User> users = userDao.findAllUser();
          logger.info("All users: {}", users.toString());
          return users;
      } catch (Exception e) {
          logger.error(e.getMessage(), e);
          sessionManager.rollbackSession();
      }
      return Collections.emptyList();
    }
  }

}
