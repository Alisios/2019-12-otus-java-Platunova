package ru.otus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.api.model.User;
import ru.otus.services.InitialUsersService;
import ru.otus.hibernate.HibernateUtils;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;

@Configuration
public class APIConfiguration {

    @Bean
    SessionManagerHibernate sessionManager (){
    return new SessionManagerHibernate(HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
            User.class));
    }

    @Bean(initMethod = "initiateUsers")
    public InitialUsersService userCreator() {
        return new InitialUsersService();
    }
}