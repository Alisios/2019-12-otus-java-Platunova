package ru.otus.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.db.service.DBServiceUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class InitialUsersService {

    @Qualifier("DBServiceUserJPA")
    @Autowired
    private DBServiceUser dbService;

    private void initiateForChecking(){
        List<User> userList = new ArrayList<User>(List.of(
                new User(202812830, new ConcertModel("Guns and Roses",
                        "29 Май 20121 cб, 19:00",
                        "где-то далеко",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2020, 10,23).getTime()),

                new User(202812830, new ConcertModel("TWENTY ONE PILOT",
                        "11 Июль 2021вс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, Calendar.JULY,9).getTime()),
                new User(202812830, new ConcertModel("Aerosmith (Аэросмит)",
                        "29 Май 2021сб 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                        new GregorianCalendar(2020, Calendar.MAY,27).getTime()),
                new User(202812830, new ConcertModel("Green Day",
                        "28 Май 2021сб 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                        new GregorianCalendar(2019, Calendar.MAY,26).getTime()),
                new User(202812830, new ConcertModel("кис-кис",
                        "19 Нояб.чт 20:00",
                        "Adrenaline Stadium",
                        "https://msk.kassir.ru/koncert/adrenaline-stadium/kis-kis_2020-11-19"),
                        new GregorianCalendar(2019, Calendar.JULY,10).getTime()),
                new User(202812830, new ConcertModel("OneRepublic",
                        "20 Нояб.пт 20:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/onerepublic_2020-11-20"),
                        new GregorianCalendar(2020, Calendar.NOVEMBER,18).getTime()),
                new User(202812830, new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20"),
                        new GregorianCalendar(2020, Calendar.JUNE,19).getTime())));

        //userList.get(0).setIsMonitoringSuccessful(true);
      //  userList.get(6).setIsMonitoringSuccessful(true);
        //userList.forEach(user->user.setIsMonitoringSuccessful(true));
        userList.forEach(user->user.getConcert().setShouldBeMonitored(true));
        userList.forEach(user->user.getConcert().setOwner(user));
        userList.forEach(dbService::saveUser); //для проверки мониторинга
       // return userList;
    }
}
