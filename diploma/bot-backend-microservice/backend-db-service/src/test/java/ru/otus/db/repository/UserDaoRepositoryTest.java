package ru.otus.db.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase
@DisplayName("Тест проверяет ")
class UserDaoRepositoryTest {

    @Autowired
    private UserDaoRepository userDaoRepository;

    private List<User> userList;

    @BeforeEach
    void initiate() {
        userList = new ArrayList<>(List.of(
                new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, 6, 5).getTime()),
                new User(202812830, new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2020, 4, 23).getTime()),
                new User(202812830, new ConcertModel("ААААА",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2021, 6, 5).getTime()),
                new User(202812830, new ConcertModel("БББББ",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2020, 4, 23).getTime()),
                new User(202812830, new ConcertModel("Кис-кис",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, 6, 5).getTime()),
                new User(202812830, new ConcertModel("ВВВВВ",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2020, 4, 23).getTime()),
                new User(202812830, new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20"),
                        new GregorianCalendar(2020, 5, 19).getTime())));
        userList.get(1).setIsMonitoringSuccessful(true);
        //userList.forEach(user->user.setIsMonitoringSuccessful(true));
        userList.forEach(user -> user.getConcert().setShouldBeMonitored(true));
        userList.forEach(user -> user.getConcert().setOwner(user));

    }

    @DisplayName("корректную работу c состояниями сущностей обекта и связанными сущностями при сохранениии и изменении")
    @Test
    void correctlySaveAndUpdateStateOfEntities() {
        userList.forEach(userDaoRepository::save);
        List<User> users = userDaoRepository.findAll();
        User user = userDaoRepository.save(users.get(0));
        userList.get(0).setId(user.getId());
        assertThat(userDaoRepository.count()).isEqualTo(userList.size());
        assertThat(userDaoRepository.findAll()).isEqualTo(userList).isEqualTo(users);
        user.setChatId(7777777);
        assertThat(userDaoRepository.findAll()).containsAll(userList);
        userDaoRepository.save(user);
        //   assertThat(userDaoRepository.findAll()).isNotEqualTo(userList); - для проверки postgeSQL
        user.getConcert().setArtist("Фейковый артист");
        //  assertThat(userDaoRepository.findById(user.getId()).get()).isNotEqualTo(user);
        userDaoRepository.save(user);
        assertThat(userDaoRepository.findById(user.getId()).get()).isEqualTo(user);
    }

    @DisplayName("корректное обновление объекта без изменения id")
    @Test
    void correctlyUpdateObjectNotChangingItsId() {
        userDaoRepository.deleteAll();
        User us = userDaoRepository.save(userList.get(0));
        User us2 = userDaoRepository.save(userList.get(0));
        User us3 = userDaoRepository.save(userList.get(0));
        assertThat(userDaoRepository.count()).isEqualTo(1);
        assertThat(userDaoRepository.findById(us2.getId()))
                .isEqualTo(userDaoRepository.findById(us.getId()))
                .isEqualTo(userDaoRepository.findById(us3.getId()));
        assertThat(userDaoRepository.findById(userList.get(0).getId()).get().getConcert().getId()).isEqualTo(us.getConcert().getId());
    }


    @DisplayName("корректное удаление объекта")
    @Test
    void correctlyDeleteObject() {
        User us1 = userDaoRepository.save(userList.get(1));
        User us2 = userDaoRepository.save(userList.get(2));
        User us3 = userDaoRepository.save(userList.get(3));
        userDaoRepository.deleteById(us1.getId());
        assertThat(userDaoRepository.findById(us1.getId())).isEmpty();
        userDaoRepository.deleteById(us2.getId());
        assertThat(userDaoRepository.count()).isEqualTo(1);
    }


    @DisplayName("корректное нахождение объекта по имени артиста и chaId")
    @Test
    void correctlyFindObjectByChatIdAndConcertArtist() {
        User us1 = userDaoRepository.save(userList.get(0));
        User us2 = userDaoRepository.save(userList.get(1));
        User us3 = userDaoRepository.save(userList.get(2));
        userList.get(0).setId(us1.getId());
        userList.get(1).setId(us2.getId());
        userList.get(2).setId(us3.getId());
        assertThat(userDaoRepository.findByChatIdAndConcertArtist(202812830, "TWENTY ØNE PILØTS").get())
                .isEqualTo(userList.get(0));
        assertThat(userDaoRepository.findByChatIdAndConcertArtist(202812830, "ААААА").get())
                .isEqualTo(userList.get(2));
    }

    @DisplayName("корректное нахождение Users для оповещения")
    @Test
    void correctlyFindUsersForNotifying() {
        userList.get(0).setIsDateExpired(true);
        userList.get(1).setIsMonitoringSuccessful(true);
        User usr1 = userDaoRepository.save(userList.get(0));
        User usr2 = userDaoRepository.save(userList.get(1));
        User usr3 = userDaoRepository.save(userList.get(2));
        userList.get(0).setId(usr1.getId());
        userList.get(1).setId(usr2.getId());
        userList.get(2).setId(usr3.getId());
        List<User> list = userDaoRepository.findByIsMonitoringSuccessfulIsTrueAndIsDateExpiredIsTrue();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list).containsSequence(userList.get(0), userList.get(1)).doesNotContain(userList.get(2));
    }

    @DisplayName("корректную работу метода findAll")
    @Test
    void correctlyWorksOfGettingAllUsers() {
        userList.forEach(userDaoRepository::save);
        List<User> users = userDaoRepository.findAll();
        assertThat(users).isNotEmpty().containsAll(userList);
        assertThat(userDaoRepository.count()).isEqualTo(userList.size());
        User us = new User(202812830, new ConcertModel("Guns and Roses",
                "20 Деквс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/gyns-and-roses"),
                new GregorianCalendar(2019, 6, 5).getTime());
        us.getConcert().setOwner(us);
        userDaoRepository.save(us);
        assertThat(userList).containsAll(users);
        assertThat(userList).isNotSameAs(userDaoRepository.findAll());
    }

    @DisplayName("корректное поведение при бросании исключений (dao)")
    @Test
    void correctlyWorksWithExceptionsDao() {
        assertDoesNotThrow(() -> userDaoRepository.findById(-1213L));
        assertDoesNotThrow(() -> userDaoRepository.findById(255L));
        assertThat(userDaoRepository.findById(255L)).isEmpty();
        assertThat(userDaoRepository.findById(-1213L)).isEmpty();
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userDaoRepository.deleteById(255L);
        });
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userDaoRepository.deleteById(-1213L);
        });
        assertThrows(RuntimeException.class, () -> {
            userDaoRepository.save(null);
        });
    }

    @AfterEach
    void deleteUsersFromDB() {
        userDaoRepository.deleteAll();
        userList = null;
    }
}