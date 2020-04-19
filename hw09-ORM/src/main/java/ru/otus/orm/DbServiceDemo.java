package ru.otus.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
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

//проверка классов в тестах
public class DbServiceDemo {

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
    User user1 = new User(1, "Женя", 29);
    dbServiceUser.saveUser(user1);
    user1.setAge(30);
    dbServiceUser.saveUser(user1);
    dbServiceUser.getUser(1, User.class);

  }

  private void createTable(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement pst = connection.prepareStatement("create table User(id bigint(20) NOT NULL auto_increment, name varchar(255), age int)")) {
       pst.executeUpdate();
    }
  }
}
