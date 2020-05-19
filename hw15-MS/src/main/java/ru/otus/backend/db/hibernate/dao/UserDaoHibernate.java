package ru.otus.backend.db.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.otus.api.dao.UserDao;
import ru.otus.api.dao.UserDaoException;
import ru.otus.api.model.User;
import ru.otus.api.cachehw.HwListener;
import ru.otus.api.cachehw.MyCache;
import ru.otus.backend.db.hibernate.sessionmanager.DatabaseSessionHibernate;
import ru.otus.backend.db.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.backend.db.sessionmanager.SessionManager;


import java.util.*;

@Repository
public class UserDaoHibernate implements UserDao {
    private static Logger logger = LoggerFactory.getLogger(UserDaoHibernate.class);

    private final SessionManagerHibernate sessionManager;

    private MyCache<String, User> cache = new MyCache<>();

    private final HwListener<String, User> listener = new HwListener<String, User>() {
        @Override
        public void notify(String key, User value, String action) {
            logger.info("key:{}, value:{}, action: {}", key, value, action);
        }
    };


    public UserDaoHibernate(SessionManagerHibernate sessionManager) {
        this.sessionManager = sessionManager;
        cache.addListener(listener);
    }

    @Override
    public Optional<User> findById(long id) {
        if (cache.getCache().containsKey(Long.toString(id))){
            return Optional.ofNullable(cache.get(Long.toString(id)));
        }
        else{
            DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
            try {
                Optional<User> temp = Optional.ofNullable(currentSession.getHibernateSession().get(User.class, id));
                cache.put(Long.toString(id), temp.get());
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
            hibernateSession.flush();
            //hibernateSession.save(user);
            cache.put(Long.toString(user.getId()),user);
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


    @Override
    public Optional<User> findByLogin(String login) {
        for (User user :cache.getCache().values()){
            if (user.getLogin().equals(login))
                return Optional.ofNullable(cache.get(Long.toString(user.getId())));
        }
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
            String hql = "from User where login = :login";
            Query query = currentSession.getHibernateSession().createQuery(hql);
            query.setParameter("login", login);
            List users = query.list();
            return Optional.ofNullable((User)users.get(0));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAllUser(){
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
            String hql = "From " + User.class.getSimpleName();
            List <User> users = currentSession.getHibernateSession().createQuery(hql, User.class).list();
            users.forEach(user -> cache.put(Long.toString(user.getId()), user));
            return users;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}
