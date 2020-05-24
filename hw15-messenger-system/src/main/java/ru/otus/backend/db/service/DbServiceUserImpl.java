package ru.otus.backend.db.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.api.cachehw.HwCache;
import ru.otus.api.cachehw.HwListener;
import ru.otus.api.dao.UserDao;
import ru.otus.api.model.User;
import ru.otus.backend.db.sessionmanager.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DbServiceUserImpl implements DBServiceUser {
  private static Logger logger = LoggerFactory.getLogger(DbServiceUserImpl.class);

  private final UserDao userDao ;

  private final HwCache<String, User> cache;

  private final HwListener<String, User> listener = new HwListener<String, User>() {
    @Override
    public void notify(String key, User value, String action) {
      logger.info("key:{}, value:{}, action: {}", key, value, action);
    }
  };

  public DbServiceUserImpl(UserDao userDao, HwCache<String, User> cache) {
    this.userDao = userDao;
    this.cache = cache;
    cache.addListener(listener);
  }

  @Override
  public long saveUser(User user) {
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        long userId = userDao.saveUser(user);
        sessionManager.commitSession();
        logger.info("created user: {}", userId);
        cache.put(Long.toString(user.getId()),user);
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
    if (cache.getCache().containsKey(Long.toString(id))){
      return Optional.ofNullable(cache.get(Long.toString(id)));
    }else {
      try (SessionManager sessionManager = userDao.getSessionManager()) {
        sessionManager.beginSession();
        try {
          Optional <User> userOptional = userDao.findById(id);
          logger.info("user: {}", userOptional.orElse(null));
          cache.put(Long.toString(id), userOptional.get());
          return userOptional;
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
          sessionManager.rollbackSession();
        }
        return Optional.empty();
      }
    }
  }


  @Override
  public Optional<User> getUser(String login) {
    for (User user :cache.getCache().values()){
      if (user.getLogin().equals(login))
        return Optional.ofNullable(cache.get(Long.toString(user.getId())));
    }
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
        Optional userOptional = userDao.findByLogin(login);
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
  public List <User> getAllUsers(){
    try (SessionManager sessionManager = userDao.getSessionManager()) {
      sessionManager.beginSession();
      try {
          List<User> users = userDao.findAllUser();
          logger.info("user: {}", users.toString());
          users.forEach(user -> cache.put(Long.toString(user.getId()), user));
          return users;
      } catch (Exception e) {
          logger.error(e.getMessage(), e);
          sessionManager.rollbackSession();
      }
      return Collections.emptyList();
    }
  }

}
