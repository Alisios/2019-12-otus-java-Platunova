package ru.otus.configurations;

import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.otus.backend.db.service.DBServiceUser;
import ru.otus.backend.handlers.GetUsersDataRequestHandler;
import ru.otus.backend.handlers.SaveUserRequestHandler;
import ru.otus.front.FrontendService;
import ru.otus.front.FrontendServiceImpl;
import ru.otus.front.handlers.GetUserDataResponseHandler;
import ru.otus.front.handlers.SaveUserDataResponseHandler;
import ru.otus.messagesystem.*;
import ru.otus.services.InitialUsersService;

@Configuration
public class MessageSystemConfig {

    final private DBServiceUser dbServiceUser;
    final private PasswordEncoder passwordEncoder;

    final private ConfigProperties configProperties;

    @Autowired
    MessageSystemConfig(DBServiceUser dbServiceUser,PasswordEncoder passwordEncoder, ConfigProperties configProperties){
        this.dbServiceUser = dbServiceUser;
        this.passwordEncoder = passwordEncoder;
        this.configProperties = configProperties;
    }

    @Bean(destroyMethod = "dispose")
    MessageSystem messageSystem () {
        return new MessageSystemImpl();
    }

    @Bean
    MsClient backendMsClient(MessageSystem messageSystem) {
        final MsClient backendMsClient = new MsClientImpl(configProperties.getBackendServiceClientName(), messageSystem);
        backendMsClient.addHandler(MessageType.GET_USERS, new GetUsersDataRequestHandler(this.dbServiceUser));
        backendMsClient.addHandler(MessageType.SAVE_USER, new SaveUserRequestHandler(this.dbServiceUser, this.passwordEncoder));
        messageSystem.addClient(backendMsClient);
        return backendMsClient;
    }

    @Bean
    MsClient frontendMsClient (  MessageSystem messageSystem){
        return new MsClientImpl(configProperties.getFrontendServiceClientName(), messageSystem);
    }

    @Bean
    FrontendService frontendService( MessageSystem messageSystem, MsClient frontendMsClient) {
        FrontendService frontendService = new FrontendServiceImpl(configProperties.getBackendServiceClientName(), frontendMsClient);
        frontendMsClient.addHandler(MessageType.GET_USERS, new GetUserDataResponseHandler(frontendService));
        frontendMsClient.addHandler(MessageType.SAVE_USER, new SaveUserDataResponseHandler(frontendService));
        messageSystem.addClient(frontendMsClient);
        return frontendService;
    }

    @Bean(initMethod = "initiateUsers")
    public InitialUsersService userCreator() {
        return new InitialUsersService();
    }
}