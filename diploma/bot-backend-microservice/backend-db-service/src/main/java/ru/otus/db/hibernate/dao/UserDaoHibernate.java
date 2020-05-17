package ru.otus.db.hibernate.dao;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.backend.model.User;
import ru.otus.db.hibernate.cachehw.HwListener;
import ru.otus.db.hibernate.cachehw.MyCache;
import ru.otus.db.hibernate.sessionmanager.DatabaseSessionHibernate;
import ru.otus.db.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.db.hibernate.sessionmanager.SessionManager;
import org.hibernate.query.Query;
import java.util.*;


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
                Optional<User> temp = Optional.ofNullable(currentSession.getHibernateSession().get(User.class, id));//find
                temp.ifPresent(user -> cache.put(Long.toString(id), user));
                return temp;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return Optional.empty();
        }
    }

    @Override
    public long saveUser(User user) {
        if (user==null) throw new IllegalArgumentException("User is null");
        Optional <User> u = findByChatIdAndArtist(user);
        if (u.isPresent())
            return u.get().getId();
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
            Session hibernateSession = currentSession.getHibernateSession();
            if (user.getId() > 0) {
                hibernateSession.merge(user);
            } else {
                hibernateSession.persist(user);
            }
            hibernateSession.flush();
           // hibernateSession.saveOrUpdate(user);//OrUpdate(user);
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
    public List<User> findAllUser(){
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
            String hql = "From User";
            List <User> users = currentSession.getHibernateSession().createQuery(hql, User.class).list();
            users.forEach(user -> cache.put(Long.toString(user.getId()), user));
            return users;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<User> findByChatIdAndArtist(User user) {
//        for (User u :cache.getCache().values()){
//            if ((u.getChatId()==user.getChatId()) && u.getConcert().getArtist().equals(user.getConcert().getArtist()))
//                return true;
//        }
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
            String hql = "from User where concert.artist = :artist and chatId = :chatId";
            Query query = currentSession.getHibernateSession().createQuery(hql);
            query.setParameter("artist", user.getConcert().getArtist());
            query.setParameter("chatId", user.getChatId());
            List<User> users = query.list();
            return users.size() != 0 ? Optional.ofNullable(users.get(0)) : Optional.empty();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAllUserForNotifying(){
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
          //  String hql = "from User where concert.shouldBeMonitored=true";//User where isMonitoringSuccessful = true or isDateExpired = true";
            String hql = "from User where isMonitoringSuccessful = true or isDateExpired = true";
            List <User> users = currentSession.getHibernateSession().createQuery(hql, User.class).list();
            users.forEach(user -> cache.put(Long.toString(user.getId()), user));
            return users;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public Optional <User> delete(long id){
            DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
            try {
                User user;
                if (cache.getCache().containsKey(Long.toString(id)))
                    user = cache.getCache().remove(Long.toString(id));
                else
                    user= currentSession.getHibernateSession().get(User.class, id);
                currentSession.getHibernateSession().delete(user);
//                currentSession.getHibernateSession().flush();
//                currentSession.getHibernateSession().clear();
                return Optional.ofNullable(user);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return Optional.empty();

    }
}
