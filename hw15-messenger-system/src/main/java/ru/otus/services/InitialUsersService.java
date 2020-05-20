package ru.otus.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.api.model.User;
import ru.otus.backend.db.service.DBServiceUser;

public class InitialUsersService {

    @Autowired
    DBServiceUser dbServiceUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void initiateUsers(){
        dbServiceUser.saveUser(new User( 1L,"Вася",27, "vas",  passwordEncoder.encode("11111")));
        dbServiceUser.saveUser(new User(2L,"Женя", 29, "zhenya",  passwordEncoder.encode("11111")));
        dbServiceUser.saveUser(new User(3L,"Джон",30,  "admin",  passwordEncoder.encode("11111")));
        dbServiceUser.saveUser(new User (4L, "Боб",37, "bob",  passwordEncoder.encode("bobpassword")));
        dbServiceUser.saveUser(new User(5L,"Энтони", 19, "ent",  passwordEncoder.encode("ent123")));
        dbServiceUser.saveUser(new User ( 6L,"Рома",33, "poma",  passwordEncoder.encode("12345")));
        dbServiceUser.saveUser(new User(7L,"Саша", 35, "sashka",  passwordEncoder.encode("12")));
        dbServiceUser.saveUser(new User(8L,"Эл", 28, "el123",  passwordEncoder.encode("2222")));
    }
}
