package ru.otus.orm.jdbc.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.orm.api.model.Account;
import ru.otus.orm.api.model.User;
import ru.otus.orm.api.service.DBServiceUser;
import ru.otus.orm.api.service.DbServiceUserImpl;
import ru.otus.orm.h2.DataSourceH2;
import ru.otus.orm.jdbc.DbExecutor;
import ru.otus.orm.jdbc.dao.UserDaoJdbc;
import ru.otus.orm.jdbc.helpers.CreateSqlStatement;
import ru.otus.orm.jdbc.helpers.JdbcMapper;
import ru.otus.orm.jdbc.sessionmanager.SessionManagerJdbc;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест должен ")
class UserDaoJdbcTemplateTest {
    private JdbcMapper jdbcMapper;
    private CreateSqlStatement createSqlStatement;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        jdbcMapper = new JdbcMapper();
        createSqlStatement = new CreateSqlStatement();
        dataSource = new DataSourceH2();
    }

    @Test
    @DisplayName("корректно создавать, загружать и обновлять User")
    void createLoadAndUpdateUsersCorrectly() throws Exception {

        createTableUser(dataSource);
        SessionManagerJdbc sessionManager = new SessionManagerJdbc(dataSource);
        DbExecutor<User> dbExecutor = new DbExecutor<>();
        UserDaoJdbcTemplate<User> userDaoJdbcTemplate = new UserDaoJdbcTemplate<>(sessionManager, dbExecutor,jdbcMapper, createSqlStatement);
        UserDaoJdbc<User> userDao = new UserDaoJdbc<>(sessionManager, userDaoJdbcTemplate);

        DBServiceUser<User> dbServiceUser = new DbServiceUserImpl<>(userDao);
        User user1 = new User(1, "Женя", 29);
        User user2 = new User(2, "Вадим", 37);
        User user3 =  new User(3, "Костя", 30);
        User user4 = new User(4, "Джон", 43);

        dbServiceUser.saveUser(user1);
        dbServiceUser.saveUser(user2);
        dbServiceUser.saveUser(user3);
        dbServiceUser.saveUser(user4);

        assertEquals(user1,  dbServiceUser.getUser(1, User.class).get());
        assertEquals(user2,  dbServiceUser.getUser(2, User.class).get());
        assertEquals(user3,  dbServiceUser.getUser(3, User.class).get());
        assertEquals(user4,  dbServiceUser.getUser(4, User.class).get());
        user1.setAge(30);
        dbServiceUser.saveUser(user1);
        assertEquals(user1,  dbServiceUser.getUser(1, User.class).get());
    }

    @Test
    @DisplayName("корректно создавать, загружать и обновлять Account")
    void createLoadAndUpdateAccountsCorrectly() throws Exception {
        createTableAccount(dataSource);
        SessionManagerJdbc sessionManager = new SessionManagerJdbc(dataSource);
        DbExecutor<Account> dbExecutor = new DbExecutor<>();
        UserDaoJdbcTemplate<Account> userDaoJdbcTemplate = new UserDaoJdbcTemplate<>(sessionManager, dbExecutor,jdbcMapper, createSqlStatement);
        UserDaoJdbc<Account> userDao = new UserDaoJdbc<>(sessionManager, userDaoJdbcTemplate);

        DBServiceUser<Account> dbServiceUser = new DbServiceUserImpl<>(userDao);
        var user1 = new Account(1, "Тип1", new BigDecimal(11));
        var user2 = new Account(2, "Тип2", new BigDecimal(22));
        var user3 =  new Account(3, "Тип3", new BigDecimal(33));
        var user4 = new Account(4, "Тип4", new BigDecimal(44));

        dbServiceUser.saveUser(user1);
        dbServiceUser.saveUser(user2);
        dbServiceUser.saveUser(user3);
        dbServiceUser.saveUser(user4);

        assertEquals(user1,  dbServiceUser.getUser(1, Account.class).get());
        assertEquals(user2,  dbServiceUser.getUser(2, Account.class).get());
        assertEquals(user3,  dbServiceUser.getUser(3, Account.class).get());
        assertEquals(user4,  dbServiceUser.getUser(4, Account.class).get());
        user1.setRest(new BigDecimal(30));
        dbServiceUser.saveUser(user1);
        assertEquals(user1,  dbServiceUser.getUser(1, Account.class).get());
    }

    private static void createTableUser(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pst = connection.prepareStatement("create table User(id bigint(20) NOT NULL auto_increment, name varchar(255), age int)")) {
            pst.executeUpdate();
        }
    }
    private void createTableAccount(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pst = connection.prepareStatement("create table Account(no bigint(20) NOT NULL auto_increment, type varchar(255), rest number)")) {
            pst.executeUpdate();
        }
    }
}