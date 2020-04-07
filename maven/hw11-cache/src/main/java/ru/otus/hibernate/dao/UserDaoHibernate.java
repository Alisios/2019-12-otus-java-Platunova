package ru.otus.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.dao.UserDao;
import ru.otus.api.dao.UserDaoException;
import ru.otus.api.model.User;
import ru.otus.api.sessionmanager.SessionManager;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.hibernate.sessionmanager.DatabaseSessionHibernate;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.Optional;

public class UserDaoHibernate implements UserDao {
    private static Logger logger = LoggerFactory.getLogger(UserDaoHibernate.class);

    private final SessionManagerHibernate sessionManager;
    private MyCache<Long, User> cache = new MyCache<>();

    private final HwListener<Long, User> listener = new HwListener<Long, User>() {
        @Override
        public void notify(Long key, User value, String action) {
            logger.info("key:{}, value:{}, action: {}", key, value, action);
        }
    };


    public UserDaoHibernate(SessionManagerHibernate sessionManager) {
        this.sessionManager = sessionManager;
        cache.addListener(listener);
    }

    @Override
    public Optional<User> findById(long id) {
        if (cache.getCache().containsKey(id)){
            return Optional.ofNullable(cache.get(id));
        }
        else{
            DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
            try {
                Optional<User> temp = Optional.ofNullable(currentSession.getHibernateSession().get(User.class, id));
                cache.put(id, temp.get());
                return temp;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return Optional.empty();
        }
    }

    @Override
    public long saveUser(User user) {
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
            Session hibernateSession = currentSession.getHibernateSession();
            if (user.getId() > 0) {
                hibernateSession.merge(user);
            } else {
                hibernateSession.persist(user);
            }
            hibernateSession.save(user);
            cache.put(user.getId(),user);
            return user.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
