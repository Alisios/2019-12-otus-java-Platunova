package ru.otus.backend.db.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.api.cachehw.HwCache;
import ru.otus.api.cachehw.HwListener;
import ru.otus.api.dao.UserDao;
import ru.otus.api.dao.UserDaoException;
import ru.otus.api.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DBServiceUserSpring implements DBServiceUser  {
    private static Logger logger = LoggerFactory.getLogger(DBServiceUserSpring.class);

    private final UserDao userDao ;

    private final HwCache<String, User> cache;

    private final HwListener<String, User> listener = new HwListener<String, User>() {
        @Override
        public void notify(String key, User value, String action) {
            logger.info("key:{}, value:{}, action: {}", key, value, action);
        }
    };

    public DBServiceUserSpring(UserDao userDao, HwCache<String, User> cache) {
        this.userDao = userDao;
        this.cache = cache;
        cache.addListener(listener);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ,
            rollbackFor = { DbServiceException.class, UserDaoException.class})
    public long saveUser(User user) {
        long userId = userDao.saveUser(user);
        cache.put(Long.toString(user.getId()),user);
        logger.info("created user: {}", userId);
        return userId;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, readOnly = true,
            rollbackFor ={ DbServiceException.class, UserDaoException.class})
    public Optional<User> getUser(long id) {
        if (cache.getCache().containsKey(Long.toString(id))){
          return Optional.ofNullable(cache.get(Long.toString(id)));
        }else {
            Optional<User> userOptional = userDao.findById(id);
            cache.put(Long.toString(id), userOptional.get());
            logger.info("user: {}", userOptional.orElse(null));
            return userOptional;
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ,readOnly = true,
            rollbackFor = { DbServiceException.class, UserDaoException.class})
    public Optional<User> getUser(String login) {
        for (User user :cache.getCache().values()){
            if (user.getLogin().equals(login))
            return Optional.ofNullable(cache.get(Long.toString(user.getId())));
        }
        Optional userOptional = userDao.findByLogin(login);
        logger.info("user: {}", userOptional.orElse(null));
        return userOptional;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ,readOnly = true,
            rollbackFor = { DbServiceException.class, UserDaoException.class})
    public List <User> getAllUsers(){
        List<User> users = userDao.findAllUser();
        users.forEach(user -> cache.put(Long.toString(user.getId()), user));
        logger.info("user: {}", users.toString());
        return users;
    }
}
