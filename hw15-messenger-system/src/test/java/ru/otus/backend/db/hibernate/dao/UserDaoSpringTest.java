package ru.otus.backend.db.hibernate.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.otus.api.cachehw.HwCache;
import ru.otus.api.dao.UserDao;

import ru.otus.api.model.User;
import ru.otus.backend.db.service.DBServiceUser;
import ru.otus.backend.db.service.DBServiceUserSpring;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@DisplayName("Тест проверяет ")
class UserDaoSpringTest {

    @Autowired
    UserDao userDao;

    @Autowired
    HwCache<String, User> cache;

    @TestConfiguration
    static class UserDaoSpringTestConfig {
        @Bean
        public DBServiceUser dbServiceUser(UserDao userDao, HwCache<String, User> cache) {
            return new DBServiceUserSpring(userDao,cache);
        }
    }
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private
    DBServiceUser dbServiceUser;

    private User user = new User("Федя", 34, "login5", "123");


    @DisplayName("корректную работу с БД после подключения Transactional")
    @Test
    void checkIfUserIsSavedInDBCorrectly(){
         List<User> listOfUser2 =  new ArrayList<>(List.of(new User( 1L,"Вася",27, "vas",passwordEncoder.encode("11111")),
                new User(2L,"Женя", 29, "zhenya",  passwordEncoder.encode("11111"))));
        long id = dbServiceUser.saveUser(user);
        assertEquals(dbServiceUser.getUser(id).get(), user);
        user.setAge(546327);
        dbServiceUser.saveUser(user);
        dbServiceUser.saveUser(user);
        assertEquals(dbServiceUser.getUser(id).get(), user);
        user.setLogin("login233");
        dbServiceUser.saveUser(user);
        assertThat(dbServiceUser.getUser(id).get()).isEqualTo(user);
        List <User> users = dbServiceUser.getAllUsers();
        assertThat(users.get(0).getLogin()).isEqualTo(listOfUser2.get(0).getLogin());
        assertThat(dbServiceUser.getUser(listOfUser2.get(1).getLogin()).get().getId()).isEqualTo(listOfUser2.get(1).getId());

        Throwable exception = assertThrows(NullPointerException.class, () -> {
            dbServiceUser.saveUser(null);
            });
        Throwable exception2 = assertThrows(RuntimeException.class, () -> {
            dbServiceUser.getUser(-1213L);
        });
        Throwable exception3 = assertThrows(RuntimeException.class, () -> {
            dbServiceUser.getUser(25);
        });
    }

}