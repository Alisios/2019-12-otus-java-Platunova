package ru.otus;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.dao.UserDao;
import ru.otus.api.model.AddressDataSet;
import ru.otus.api.model.PhoneDataSet;
import ru.otus.hibernate.dao.UserDaoHibernate;
import ru.otus.api.service.DBServiceUser;
import ru.otus.api.service.DbServiceUserImpl;
import ru.otus.api.model.User;
import ru.otus.hibernate.HibernateUtils;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**этот класс демонстрирует работу MyCache**/
public class DbServiceDemo {
  private static Logger logger = LoggerFactory.getLogger(DbServiceDemo.class);
  public static void main(String[] args) {

     SessionFactory sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
            User.class, AddressDataSet.class, PhoneDataSet.class);

    SessionManagerHibernate sessionManager = new SessionManagerHibernate(sessionFactory);
    UserDao userDao = new UserDaoHibernate(sessionManager);
    DBServiceUser dbServiceUser = new DbServiceUserImpl(userDao);

    List<User> users = new ArrayList<>();
    for (int idx = 0; idx < 160; idx++){
      users.add(new User ( "Джон", idx));
      users.get(idx).setAddress(new AddressDataSet("Москва", users.get(idx)));
      users.get(idx).setPhones(Collections.singletonList(new PhoneDataSet("+79157383393",users.get(idx))));
      dbServiceUser.saveUser(users.get(idx));
    }
    for (int idx = 1; idx < 161; idx++){
      outputUserOptional("Created user ",dbServiceUser.getUser(idx));
    }
    sessionManager.close();
    sessionFactory.close();
  }

  private static void outputUserOptional(String header, Optional<User> mayBeUser) {
    System.out.println("-----------------------------------------------------------");
    System.out.println(header);
    mayBeUser.ifPresentOrElse(System.out::println, () -> logger.info("User not found"));
  }

}
