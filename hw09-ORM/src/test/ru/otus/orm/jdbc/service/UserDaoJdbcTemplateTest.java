package ru.otus.orm.jdbc.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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

    @BeforeAll
    static void setUp2() throws SQLException {
        DataSource dataSource = new DataSourceH2();
        createTableUser(dataSource);
        createTableAccount(dataSource);
    }

    @DisplayName("корректно создавать, загружать и обновлять User")
    @ParameterizedTest
    @MethodSource("generateData")
    void createLoadAndUpdateUsersCorrectly(User user, int id) throws Exception {
        SessionManagerJdbc sessionManager = new SessionManagerJdbc(dataSource);
        DbExecutor<User> dbExecutor = new DbExecutor<>();
        UserDaoJdbcTemplate<User> userDaoJdbcTemplate = new UserDaoJdbcTemplate<>(sessionManager, dbExecutor, jdbcMapper, createSqlStatement);
        UserDaoJdbc<User> userDao = new UserDaoJdbc<>(sessionManager, userDaoJdbcTemplate);
        DBServiceUser<User> dbServiceUser = new DbServiceUserImpl<>(userDao);
        dbServiceUser.saveUser(user);
        assertEquals(user, dbServiceUser.getUser(id, User.class).get());
        user.setAge(33);
        dbServiceUser.saveUser(user);
        assertEquals(user, dbServiceUser.getUser(id, User.class).get());
    }

    @ParameterizedTest
    @MethodSource("generateDataAccount")
    @DisplayName("корректно создавать, загружать и обновлять Account")
    void createLoadAndUpdateAccountsCorrectly(Account account, int id) throws Exception {
        SessionManagerJdbc sessionManager = new SessionManagerJdbc(dataSource);
        DbExecutor<Account> dbExecutor = new DbExecutor<>();
        UserDaoJdbcTemplate<Account> userDaoJdbcTemplate = new UserDaoJdbcTemplate<>(sessionManager, dbExecutor, jdbcMapper, createSqlStatement);
        UserDaoJdbc<Account> userDao = new UserDaoJdbc<>(sessionManager, userDaoJdbcTemplate);

        DBServiceUser<Account> dbServiceUser = new DbServiceUserImpl<>(userDao);
        dbServiceUser.saveUser(account);

        assertEquals(account, dbServiceUser.getUser(id, Account.class).get());
        account.setRest(new BigDecimal(30));
        dbServiceUser.saveUser(account);
        assertEquals(account, dbServiceUser.getUser(id, Account.class).get());
    }

    private static void createTableUser(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pst = connection.prepareStatement("create table User(id bigint(20) NOT NULL auto_increment, name varchar(255), age int)")) {
            pst.executeUpdate();
        }
    }

    private static void createTableAccount(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pst = connection.prepareStatement("create table Account(no bigint(20) NOT NULL auto_increment, type varchar(255), rest number)")) {
            pst.executeUpdate();
        }
    }

    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of(new User(1, "Женя", 29), 1),
                Arguments.of(new User(2, "Вадим", 37), 2),
                Arguments.of(new User(3, "Джон", 43), 3),
                Arguments.of(new User(4, "Костя", 30), 4));
    }

    private static Stream<Arguments> generateDataAccount() {
        return Stream.of(
                Arguments.of(new Account(1, "Тип1", new BigDecimal(11)), 1),
                Arguments.of(new Account(2, "Тип2", new BigDecimal(22)), 2),
                Arguments.of(new Account(3, "Тип3", new BigDecimal(33)), 3),
                Arguments.of(new Account(4, "Тип4", new BigDecimal(44)), 4));
    }
}