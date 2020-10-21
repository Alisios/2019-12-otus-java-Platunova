package ru.otus.db.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.db.repository.UserDaoRepository;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Тест проверяет ")
@AutoConfigureTestDatabase
class DBServiceUserJPATest {

    @MockBean
    private UserDaoRepository userDaoRepository;

    @Configuration
    @Import(DBServiceUserJPA.class)
    static class NestedConfiguration {}

    @Autowired
    private DBServiceUser dbServiceUser;

    private List<User> userList;

    private User us;

    @BeforeEach
    void initiate() {
        us = new User(1L, 202812830, new ConcertModel("Guns and Roses",
                "20 Деквс 19:00",
                "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                "https://msk.kassir.ru/koncert/gyns-and-roses"),
                new GregorianCalendar(2019, 6, 5).getTime());
        us.getConcert().setOwner(us);
        userList = new ArrayList<>(List.of(
                new User(1L, 202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, 6, 5).getTime()),
                new User(2L, 202812830, new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2020, 4, 23).getTime()),
                new User(3L, 202812830, new ConcertModel("ААААА",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2021, 6, 5).getTime()),
                new User(4L, 202812830, new ConcertModel("БББББ",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2020, 4, 23).getTime()),
                new User(5L, 202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, 6, 5).getTime()),
                new User(6L, 202812830, new ConcertModel("ВВВВВ",
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

    @DisplayName("корректное взаимодействие с userDaoRepository при вызове get")
    @Test
    void correctlyWorksWithUserDaoRepositoryGet() {
        when(userDaoRepository.findById(1L)).thenReturn(Optional.of(us));
        assertThat(dbServiceUser.getUser(1L).get()).isEqualTo(us);
        when(userDaoRepository.findById(-1213L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> dbServiceUser.getUser(-1213L));
        assertThat(dbServiceUser.getUser(-1213L)).isEmpty();
    }

    @DisplayName("корректное взаимодействие с userDaoRepository при вызове findByChatIdAndConcertArtist")
    @Test
    void correctlyWorksWithUserDaoRepositoryFindByChatIdAndConcertArtist() {
        when(userDaoRepository
                .findByChatIdAndConcertArtist(us.getChatId(), us.getConcert().getArtist()))
                .thenReturn(Optional.of(us));
        assertThat(dbServiceUser.findByChatIdAndArtist(us).get()).isEqualTo(us);
    }

    @DisplayName("корректное взаимодействие с userDaoRepository при вызове getAll")
    @Test
    void correctlyWorksWithUserDaoRepositoryGetAll() {
        when(userDaoRepository.findAll()).thenReturn(userList);
        assertThat(dbServiceUser.getAllUsers()).isEqualTo(userList);
    }

    @DisplayName("корректное взаимодействие с userDaoRepository при вызове getUsersForNotifying")
    @Test
    void correctlyWorksWithUserDaoRepositoryForNotifying() {
        List<User> users = userList.stream().filter((user) -> user.getIsMonitoringSuccessful() || user.getIsDateExpired()).collect(Collectors.toList());
        when(userDaoRepository.findByIsMonitoringSuccessfulIsTrueAndIsDateExpiredIsTrue())
                .thenReturn(users);
        assertThat(dbServiceUser.getUsersForNotifying()).isEqualTo(users);
    }

    @DisplayName(" что сервис бросает DBException  при попытке удаления пользователя c некорректным id")
    @Test
    void correctlyHandleDeleteException() {
        doThrow(new EmptyResultDataAccessException(2)).when(userDaoRepository).deleteById(255L);
        assertThrows(DBException.class, () -> {
            dbServiceUser.delete(255L);
        });
    }

}