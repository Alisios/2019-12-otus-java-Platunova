package ru.otus.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.backend.db.service.DBServiceUser;


@Service
public class UserAuthorisationService implements UserDetailsService
{
    @Autowired
    private DBServiceUser dbServiceUser;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException
    {
        ru.otus.api.model.User user = dbServiceUser.getUser(login).orElse(null);
        if(user == null) {
            throw new UsernameNotFoundException("User is not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getName())
                .password(user.getPassword())
                .authorities(user.getLogin().equals("admin") ? "ADMIN" : "USER")
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}