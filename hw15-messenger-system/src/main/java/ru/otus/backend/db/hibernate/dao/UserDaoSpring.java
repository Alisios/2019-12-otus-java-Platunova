package ru.otus.backend.db.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.otus.api.dao.UserDao;
import ru.otus.api.model.User;
import java.util.*;

@Repository
public class UserDaoSpring implements UserDao {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public UserDaoSpring() { };

    @Override
    public Optional<User> findById(long id) {
            Optional<User> temp = Optional.ofNullable(sessionFactory.getCurrentSession().get(User.class, id));
            return temp;
    }

    @Override
    public long saveUser(User user) {
        Session hibernateSession = sessionFactory.getCurrentSession();
        if (user.getId() > 0) {
            hibernateSession.merge(user);
        } else {
            hibernateSession.persist(user);
        }
        hibernateSession.flush();
        return user.getId();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        Session hibernateSession = sessionFactory.getCurrentSession();
        String hql = "from User where login = :login";
        Query query = hibernateSession.createQuery(hql);
        query.setParameter("login", login);
        List users = query.list();
        return Optional.ofNullable((User)users.get(0));
    }

    @Override
    public List<User> findAllUser(){
        Session hibernateSession = sessionFactory.getCurrentSession();
        String hql = "From " + User.class.getSimpleName();
        List <User> users = hibernateSession.createQuery(hql, User.class).list();
        return users;
    }
}
