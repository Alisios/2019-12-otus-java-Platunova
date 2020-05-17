package ru.otus.db;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class DBServiceImplCacheTest implements DBServiceCacheTest {
    private List<User> userList = new ArrayList<User>();
    private int i = 0;

    public DBServiceImplCacheTest()
    {
        this.initiateUsers();
    }
    void initiateUsers(){
        userList.add(new User(1L, new ConcertModel("Green Day",
                "24 Майвс 19:00",
                "Стадион \"Открытие Арена\"",
                "https://msk.kassir.ru/koncert/stadion-otkryitie-arena-5001/green-day_2020-05-24_19"),
                        new GregorianCalendar(2019, 4,23).getTime()));
        userList.add(new User(202812830, new ConcertModel("Элизиум",
                        "20 Июньсб 19:00",
                        "ГЛАВCLUB GREEN CONCERT",
                        "https://msk.kassir.ru/koncert/glavclub-green-concert/elizium_2020-06-20"),
                new GregorianCalendar(2020, 5,19).getTime()));
        userList.add( new User(202812830, new ConcertModel("TWENTY ØNE PILØTS",
                        "12 Июльвс 19:00",
                        "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                        "https://msk.kassir.ru/koncert/twenty-one-pilots#199390"),
                new GregorianCalendar(2020, 3,5).getTime()));
    }

    @Override
    public List<User> getAllUsers(){
        return userList;
    }

    @Override
    public long saveUser(User user){
        if (!userList.contains(user)){
            this.userList.add(user);
            return i++;
        } else userList.set(userList.indexOf(user), user);
        return 0;
    }
    @Override
    public List<User>getUsersForNotifying(){
        return userList.stream().filter(user->user.getMonitoringSuccessful()||user.getDateExpired()).collect(Collectors.toList());
    }
    @Override
    public User delete (long id) {
        System.out.println("in deleting");
        if (userList.size()==0)
                return null;
        else
            return userList.remove(0);
    }
}
