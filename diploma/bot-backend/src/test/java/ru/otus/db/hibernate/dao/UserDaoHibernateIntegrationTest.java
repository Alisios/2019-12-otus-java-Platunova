package ru.otus.db.hibernate.dao;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.db.hibernate.HibernateUtils;
import ru.otus.db.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.db.service.DBServiceUser;
import ru.otus.db.service.DbServiceUserImpl;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Тесты проверяют: ")
 public class UserDaoHibernateIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(UserDaoHibernateIntegrationTest.class);
    private DBServiceUser dbServiceUser;
    private SessionManagerHibernate sessionManager;
    private SessionFactory sessionFactory;

    @BeforeEach
    void set(){
        sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
                User.class, ConcertModel.class);

        sessionManager = new SessionManagerHibernate(sessionFactory);
        UserDao userDao = new UserDaoHibernate(sessionManager);
        dbServiceUser = new DbServiceUserImpl(userDao);
    }

    @AfterEach
    void closeAfter(){
        sessionManager.close();
        sessionFactory.close();
    }


    @DisplayName("корректное сохранение пользователя и связанных сущностей в базу")
    @ParameterizedTest
    @MethodSource("generateUsers")
    void checkIfUserIsSavedInDBCorrectly(User user){
        long id = dbServiceUser.saveUser(user);
        assertEquals(dbServiceUser.getUser(id).get(), user);
        assertEquals(dbServiceUser.getUser(id).get().getConcert(), user.getConcert());
        user.setChatId(2314L);
        sessionManager.close();
        sessionManager = new SessionManagerHibernate(sessionFactory);
        UserDao userDao = new UserDaoHibernate(sessionManager);
        dbServiceUser = new DbServiceUserImpl(userDao);
        assertNotEquals(dbServiceUser.getUser(id).get(), user);
        dbServiceUser.saveUser(user);
        assertEquals(dbServiceUser.getUser(id).get(), user);
    }

    @Test
    @DisplayName("корректную загрузку списка пользователей, которым нужно послать уведомление о событии")
    void checkIfUserListForNotifyingIsGotCorrectly() {
        List<User> userList = initiateUsersForTest();
        userList.forEach(dbServiceUser::saveUser);
        assertThat(dbServiceUser.getUsersForNotifying()).isNullOrEmpty();
        userList.get(0).setMonitoringSuccessful(true);
        assertThat(dbServiceUser.getUsersForNotifying()).isNullOrEmpty();
        dbServiceUser.saveUser(userList.get(0));
        assertThat(dbServiceUser.getUsersForNotifying().get(0)).isEqualTo(userList.get(0));
        userList.get(0).setMonitoringSuccessful(false);
        userList.get(1).setDateExpired(true);
        dbServiceUser.saveUser(userList.get(0));
        dbServiceUser.saveUser(userList.get(1));
        List<User> userListDb =  dbServiceUser.getUsersForNotifying();
        assertThat(userListDb.size()).isEqualTo(1);
        assertThat(userListDb.get(0)).isEqualTo(userList.get(1));
    }

    @Test
    @DisplayName("корректную загрузку списка пользователей и коррекное обновление их полей")
    void checkIfUserListIsGotCorrectlyAndUploadedCorrectly(){
    List<User> userList = initiateUsersForTest();
        userList.forEach(dbServiceUser::saveUser);
        List<User> userListDB = dbServiceUser.getAllUsers();
        assertNotNull(userListDB);
        assertThat(userListDB).isEqualTo(userList);
        userList.get(0).setMonitoringSuccessful(true);
        assertThat(userListDB).isNotEqualTo(userList);

        userList.get(0).setMonitoringSuccessful(false);
        assertThat(userListDB).isEqualTo(userList);

        userList.get(0).getConcert().setArtist("Beatles");
        assertThat(userListDB).isNotEqualTo(userList);

        long id3 =  dbServiceUser.saveUser(userList.get(0));
        userListDB = dbServiceUser.getAllUsers();
        assertEquals(userListDB, userList);
        userList.get(0).setChatId(231111L);
        long id1 =  dbServiceUser.saveUser(userList.get(0));
        long id2 =  dbServiceUser.saveUser(userList.get(1));
        long id4 =  dbServiceUser.saveUser(userList.get(1));
        assertThat(id3).isEqualTo(id1);
        assertThat(id2).isEqualTo(id4);

        assertThat(dbServiceUser.delete(id1)).get().isEqualTo(userList.get(0));
        userListDB = dbServiceUser.getAllUsers();
        assertThat(userListDB.size()).isEqualTo(userList.size()-1);
}

    private static Stream<Arguments> generateUsers() {
        return Stream.of(
                Arguments.of((new User(1L, new ConcertModel("Green Day",
                                "24 Майвс 19:00",
                                "Стадион \"Открытие Арена\"",
                                "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19"),
                                new GregorianCalendar(2019, 4,23).getTime())),
                new GregorianCalendar(2020, 3,5).getTime()),
                Arguments.of( new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2020, 3,5).getTime())),
                Arguments.of(new User(202812830, new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20"),
                        new GregorianCalendar(2020, 5,19).getTime())));
    }

    public static List<User> initiateUsersForTest(){
        List<User> userList = new ArrayList<User>(List.of(
        new User(1L, new ConcertModel("Green Day",
                "24 Майвс 19:00",
                "Стадион \"Открытие Арена\"",
                "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19"),
                new GregorianCalendar(2019, 4,23).getTime()),
        new User(202812830, new ConcertModel("Элизиум",
                "20 Июньсб 19:00",
                "ГЛАВCLUB GREEN CONCERT",
                "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20"),
                new GregorianCalendar(2020, 5,19).getTime()),
        new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                "12 Июльвс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                new GregorianCalendar(2020, 6,5).getTime())));
        userList.forEach(user->user.getConcert().setOwner(user));
        return userList;
    }
}