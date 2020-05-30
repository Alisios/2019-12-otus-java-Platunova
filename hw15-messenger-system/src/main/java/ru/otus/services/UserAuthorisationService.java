package ru.otus.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.backend.db.service.DBServiceUser;


@Service
public class UserAuthorisationService implements UserDetailsService
{
    private final DBServiceUser dbServiceUser;

    @Autowired
    UserAuthorisationService(DBServiceUser dbServiceUser){
        this.dbServiceUser = dbServiceUser;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException
    {
        return dbServiceUser.getUser(login)
                .map(u -> User.withUsername(u.getName())
                        .password(u.getPassword())
                        .authorities(u.getLogin().equals("admin") ? "ADMIN" : "USER")
                        .accountExpired(false)
                        .credentialsExpired(false)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }
}