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

/**этот класс только для демонстраыции работы Hibernate c User**/
public class DbServiceDemo {
  private static Logger logger = LoggerFactory.getLogger(DbServiceDemo.class);
  public static void main(String[] args) {
     SessionFactory sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
            User.class, AddressDataSet.class, PhoneDataSet.class);

    SessionManagerHibernate sessionManager = new SessionManagerHibernate(sessionFactory);
    UserDao userDao = new UserDaoHibernate(sessionManager);
    DBServiceUser dbServiceUser = new DbServiceUserImpl(userDao);

    var userV = new User ( "Вася", 31);
    List<PhoneDataSet> listPhone = new ArrayList<>();
    for (int idx = 0; idx < 5; idx++) {
      listPhone.add(new PhoneDataSet("+792167887" + idx, userV));
    }
    userV.setPhones(listPhone);
    var adrV = new AddressDataSet("Санкт-Петербург", userV);
    userV.setAddress(adrV);
    long id = dbServiceUser.saveUser(userV);
    Optional<User> mayBeCreatedUser = dbServiceUser.getUser(id);

    var userG = new User("Женя", 29);
    userG.setAddress(new AddressDataSet("Москва", userG));
    userG.setPhones(Collections.singletonList(new PhoneDataSet("+79157383393",userG)));
    id = dbServiceUser.saveUser(userG);
    Optional<User> mayBeUpdatedUser = dbServiceUser.getUser(id);

    outputUserOptional("Created user", mayBeCreatedUser);
    outputUserOptional("Updated user", mayBeUpdatedUser);
  }

  private static void outputUserOptional(String header, Optional<User> mayBeUser) {
    System.out.println("-----------------------------------------------------------");
    System.out.println(header);
    mayBeUser.ifPresentOrElse(System.out::println, () -> logger.info("User not found"));
  }
}
