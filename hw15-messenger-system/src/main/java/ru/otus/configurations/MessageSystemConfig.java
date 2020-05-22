package ru.otus.configurations;

import org.springframework.context.annotation.Bean;

import org.springframework.beans.factory.annotation.Value;
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

@Configuration
public class MessageSystemConfig {

    final private DBServiceUser dbServiceUser;
    final private PasswordEncoder passwordEncoder;

    @Autowired
    MessageSystemConfig(DBServiceUser dbServiceUser,PasswordEncoder passwordEncoder){
        this.dbServiceUser = dbServiceUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean(destroyMethod = "dispose")
    MessageSystem messageSystem () {
        return new MessageSystemImpl();
    }

    @Bean
    MsClient backendMsClient(MessageSystem messageSystem, @Value("service.backendServiceClientName") String backendServiceClientName) {
        final MsClient backendMsClient = new MsClientImpl(backendServiceClientName, messageSystem);
        backendMsClient.addHandler(MessageType.GET_USERS, new GetUsersDataRequestHandler(this.dbServiceUser));
        backendMsClient.addHandler(MessageType.SAVE_USER, new SaveUserRequestHandler(this.dbServiceUser, this.passwordEncoder));
        messageSystem.addClient(backendMsClient);
        return backendMsClient;
    }

    @Bean
    MsClient frontendMsClient ( @Value("service.frontendServiceClientName") final String frontendServiceClientName, MessageSystem messageSystem){
        return new MsClientImpl(frontendServiceClientName, messageSystem);
    }

    @Bean
    FrontendService frontendService( @Value("service.backendServiceClientName") final String backendServiceClientName , MessageSystem messageSystem, MsClient frontendMsClient) {
        FrontendService frontendService = new FrontendServiceImpl(backendServiceClientName, frontendMsClient);
        frontendMsClient.addHandler(MessageType.GET_USERS, new GetUserDataResponseHandler(frontendService));
        frontendMsClient.addHandler(MessageType.SAVE_USER, new SaveUserDataResponseHandler(frontendService));
        messageSystem.addClient(frontendMsClient);
        return frontendService;
    }
}