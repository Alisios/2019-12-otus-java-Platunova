package ru.otus.configurations;

import org.springframework.context.annotation.Bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.otus.backend.db.service.DBServiceUser;
import ru.otus.backend.handlers.GetUsersDataRequestHandler;
import ru.otus.backend.handlers.SaveUserRequestHandler;
import ru.otus.front.FrontendService;
import ru.otus.front.handlers.GetUserDataResponseHandler;
import ru.otus.front.handlers.SaveUserDataResponseHandler;
import ru.otus.messagesystem.*;

@Configuration
@ComponentScan(basePackages = { "ru.otus" })
@PropertySource(ignoreResourceNotFound = true, value = "app.properties")
public class MessageSystemConfig {

    @Autowired
    private DBServiceUser dbServiceUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    MsClient databaseMsClient(MessageSystem messageSystem, @Value("${backendServiceClientName}") String backendServiceClientName) {
        final MsClient databaseMsClient = (MsClient)new MsClientImpl(backendServiceClientName, messageSystem);
        databaseMsClient.addHandler(MessageType.GET_USERS, new GetUsersDataRequestHandler(this.dbServiceUser));
        databaseMsClient.addHandler(MessageType.SAVE_USER, new SaveUserRequestHandler(this.dbServiceUser, this.passwordEncoder));
        messageSystem.addClient(databaseMsClient);
        return databaseMsClient;
    }

    @Bean
    MsClient frontendMsClient(MessageSystem messageSystem, FrontendService frontendService, @Value("${frontendServiceClientName}") final String frontendServiceClientName) {
        final MsClient frontendMsClient = new MsClientImpl(frontendServiceClientName, messageSystem);
        frontendMsClient.addHandler(MessageType.GET_USERS, new GetUserDataResponseHandler(frontendService));
        frontendMsClient.addHandler(MessageType.SAVE_USER, new SaveUserDataResponseHandler(frontendService));
        messageSystem.addClient(frontendMsClient);
        return frontendMsClient;
    }
}