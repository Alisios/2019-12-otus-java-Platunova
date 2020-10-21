package ru.otus.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.backend.eventApi.EventInformationService;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.backend.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Тесты проверяют: ")
class ConcertMonitoringServiceTest {
    EventInformationService eventInformationService = mock(EventInformationService.class);
    MonitoringService monitoringService = new UserMonitoringService(eventInformationService);
    private List<User> userList;

    @BeforeEach
    void initiate()  {
        userList =  new ArrayList<>(List.of(
                new User(202812831, new ConcertModel("TWENTY ØNE PILØTS",
                        "11 Июль 2021вс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2021, 6, 5).getTime()),

                new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "11 Июль 2021вс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2021, 6, 5).getTime()),

                new User(202812837, new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2021, 4, 23).getTime()),
                new User(202812835, new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2021, 4, 23).getTime()),
                new User(202812833, new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2021, 4, 23).getTime()),
                new User(202812832, new ConcertModel("Aerosmith (Аэросмит)",
                        "30 Июльчт 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2021, 4, 23).getTime()),
                new User(202812832, new ConcertModel("Кис-кис",
                        "19 Нояб.чт 20:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/adrenaline-stadium/kis-kis_2020-11-19"),
                        new GregorianCalendar(2021, 6, 5).getTime()),

                new User(202812835, new ConcertModel("Кис-кис",
                        "19 Нояб.чт 20:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/adrenaline-stadium/kis-kis_2020-11-19"),
                        new GregorianCalendar(2021, 6, 5).getTime()),

                new User(202812830, new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20",
                        List.of(new TicketModel("Фанзона", "1200руб"),new TicketModel("Танцпартер","1200руб"),new TicketModel("С32", "2400руб"))),
                        new GregorianCalendar(2019, 5, 19).getTime())));
        userList.forEach(user -> user.getConcert().setShouldBeMonitored(true));
        userList.forEach(user -> user.getConcert().setOwner(user));
    }

    @Test
    @DisplayName("корректное определение должен ли пользователь быть уведомлен")
    void correctlyDefineIfUserShouldBeNotified() throws IOException {
        when(eventInformationService.getTicketInformation(any())).thenReturn(any());
        userList.get(2).getConcert().setShouldBeMonitored(false);
        monitoringService.getMonitoringResult(userList);
        assertThat(monitoringService.checkIfUserShouldBeNotified(userList.get(2))).isTrue();
        assertThat(monitoringService.checkIfUserShouldBeNotified(userList.get(3))).isTrue();
        assertThat(monitoringService.checkIfUserShouldBeNotified(userList.get(4))).isTrue();
        assertThat(monitoringService.checkIfUserShouldBeNotified(userList.get(8))).isTrue();
        assertThat(monitoringService.checkIfUserShouldBeNotified(userList.get(0))).isFalse();
        assertThat(monitoringService.checkIfUserShouldBeNotified(userList.get(1))).isFalse();
        assertThat(monitoringService.checkIfUserShouldBeNotified(userList.get(7))).isFalse();
    }

    @Test
    @DisplayName("корректное объединение пользователей по концертам и переключение флагов мониторинга для пользователя")
    void correctlyUnionUsersByConcertAndCorrectlySwitchingOfUserFlags() throws IOException {
        when(eventInformationService.getTicketInformation(any())).thenReturn(any());
        userList.get(2).getConcert().setShouldBeMonitored(false);
        monitoringService.getMonitoringResult(userList);
        assertThat(userList.get(2).getIsMonitoringSuccessful()).isTrue();
        assertThat(userList.get(3).getIsMonitoringSuccessful()).isTrue();
        assertThat(userList.get(4).getIsMonitoringSuccessful()).isTrue();
        assertThat(userList.get(5).getIsMonitoringSuccessful()).isTrue();
        assertThat(userList.get(2).getIsDateExpired()).isFalse();
        assertThat(userList.get(0).getIsMonitoringSuccessful()).isFalse();
        assertThat(userList.get(1).getIsMonitoringSuccessful()).isFalse();
        assertThat(userList.get(7).getIsMonitoringSuccessful()).isFalse();
        assertThat(userList.get(8).getIsMonitoringSuccessful()).isFalse();
        assertThat(userList.get(8).getIsDateExpired()).isTrue();
    }

}