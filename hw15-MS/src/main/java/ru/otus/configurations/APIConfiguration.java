package ru.otus.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.otus.api.model.User;
import ru.otus.backend.db.hibernate.HibernateUtils;
import ru.otus.backend.db.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.backend.db.service.DBServiceUser;
import ru.otus.services.InitialUsersService;


@Configuration
@ComponentScan(basePackages = { "ru.otus" })
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