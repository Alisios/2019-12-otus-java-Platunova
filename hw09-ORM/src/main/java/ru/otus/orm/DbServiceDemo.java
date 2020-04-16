package ru.otus.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.orm.jdbc.helpers.CreateSqlStatement;
import ru.otus.orm.jdbc.helpers.JdbcMapper;
import ru.otus.orm.jdbc.dao.UserDaoJdbc;
import ru.otus.orm.api.service.DBServiceUser;
import ru.otus.orm.api.service.DbServiceUserImpl;
import ru.otus.orm.jdbc.DbExecutor;
import ru.otus.orm.h2.DataSourceH2;
import ru.otus.orm.api.model.User;
import ru.otus.orm.jdbc.service.UserDaoJdbcTemplate;
import ru.otus.orm.jdbc.sessionmanager.SessionManagerJdbc;

public class DbServiceDemo {
  private static Logger logger = LoggerFactory.getLogger(DbServiceDemo.class);

  public static void main(String[] args) throws Exception {
    DataSource dataSource = new DataSourceH2();
    var demo = new DbServiceDemo();
    var jdbcMapper = new JdbcMapper();
    var createSqlStatement = new CreateSqlStatement();

    demo.createTable(dataSource);

    SessionManagerJdbc sessionManager = new SessionManagerJdbc(dataSource);
    DbExecutor<User> dbExecutor = new DbExecutor<>();
    UserDaoJdbcTemplate<User> userDaoJdbcTemplate = new UserDaoJdbcTemplate<>(sessionManager, dbExecutor,jdbcMapper, createSqlStatement);
    UserDaoJdbc<User> userDao = new UserDaoJdbc<>(sessionManager, userDaoJdbcTemplate);

    DBServiceUser<User> dbServiceUser = new DbServiceUserImpl<>(userDao);
    User user1 = new User(3, "Женя", 29);
    dbServiceUser.saveUser(new User(1, "Вадим", 27));
    dbServiceUser.saveUser(new User(2, "Костя", 30));
    dbServiceUser.saveUser(user1);
    user1.setAge(30);
    dbServiceUser.saveUser(user1);
    dbServiceUser.saveUser(new User(4, "Джон", 29));
    Optional<User> user = dbServiceUser.getUser(2, User.class);


//    DbExecutor<Account> dbExecutor2 = new DbExecutor<>();
//    UserDaoJdbcTemplate<Account> userDaoJdbcTemplate2 = new UserDaoJdbcTemplate<>(sessionManager, dbExecutor,jdbcMapper, createSqlStatement);
//    UserDaoJdbc<Account> userDao2 = new UserDaoJdbc<>(sessionManager, userDaoJdbcTemplate2);
//
//    DBServiceUser<Account> dbServiceUser2 = new DbServiceUserImpl<>(userDao);
//    Account user12 = new Account(3, "funny", new BigDecimal(29));
//    dbServiceUser2.saveUser(new Account(1, "beautiful", new BigDecimal(27)));
//    dbServiceUser2.saveUser(new Account(2, "clever", new BigDecimal(30)));
//    dbServiceUser2.saveUser(user12);
//    user12.setType("ugly");
//    dbServiceUser2.saveUser(user12);
//    dbServiceUser2.saveUser(new Account(4, "awesome", new BigDecimal(29)));
//
//    Optional<Account> user2 = dbServiceUser2.getUser(2, Account.class);



  }

  private void createTable(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement pst = connection.prepareStatement("create table User(id bigint(20) NOT NULL auto_increment, name varchar(255), age int)")) {
       //  PreparedStatement pst = connection.prepareStatement("create table Account(no bigint(20) NOT NULL auto_increment, type varchar(255), rest number)")) {
      pst.executeUpdate();
    }
    System.out.println("table created");
  }
}
