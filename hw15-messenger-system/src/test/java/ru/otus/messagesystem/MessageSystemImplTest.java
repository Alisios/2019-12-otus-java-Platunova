package ru.otus.messagesystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.otus.api.model.User;
import ru.otus.backend.db.service.DBServiceUser;
import ru.otus.backend.handlers.GetUsersDataRequestHandler;
import ru.otus.backend.handlers.SaveUserRequestHandler;

import ru.otus.front.FrontendService;
import ru.otus.front.FrontendServiceImpl;
import ru.otus.front.handlers.GetUserDataResponseHandler;
import ru.otus.front.handlers.SaveUserDataResponseHandler;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class MessageSystemImplTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageSystemImplTest.class);

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    private MessageSystem messageSystem;
    private MsClient databaseMsClient;
    private MsClient frontendMsClient;
    private FrontendService frontendServiceImpl;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private List<User> listOfUser = List.of(new User(1L, "Вася", 21, "login1", "sdfjh"),
            new User(2L, "Петя", 31, "login2", "sdfjh"),
            new User(3L, "Женя", 41, "login3", "sdfjh"));
    private User user = new User("Федя", 34, "login5", "123");


    @MockBean
    FrontendService frontendService;

    @Test
    @DisplayName("корректную передачу данных между фронтом и бэком")
    void correctDataExchangingBetweenFrontAndBack() throws InterruptedException {
        createMessageSystem(true);
        frontendService.getUsers(users -> {
            logger.info("users {}", users);
            assertThat(users).isEqualTo(listOfUser);
        });
        frontendService.saveUser(user, userSaved -> {
            logger.info("user {}, saved {}", user, userSaved);
            assertThat(userSaved).isEqualTo(user);
        });
        Thread.sleep(3000);
        messageSystem.dispose();
    }

    void createMessageSystem(boolean startProcessing) {
        logger.info("setup");
        messageSystem = new MessageSystemImpl(startProcessing);
        databaseMsClient = new MsClientImpl(DATABASE_SERVICE_CLIENT_NAME, messageSystem);
        DBServiceUser dbService = mock(DBServiceUser.class);
        when(dbService.getAllUsers()).thenReturn(listOfUser);
        when(dbService.saveUser(any())).thenReturn(4L);
        when(dbService.getUser(4L)).thenReturn(Optional.of(user));
        databaseMsClient.addHandler(MessageType.GET_USERS, new GetUsersDataRequestHandler(dbService));
        databaseMsClient.addHandler(MessageType.SAVE_USER, new SaveUserRequestHandler(dbService, passwordEncoder));
        messageSystem.addClient(databaseMsClient);

        frontendMsClient = new MsClientImpl(FRONTEND_SERVICE_CLIENT_NAME, messageSystem);
        frontendService = new FrontendServiceImpl(DATABASE_SERVICE_CLIENT_NAME, frontendMsClient);
        frontendMsClient.addHandler(MessageType.GET_USERS, new GetUserDataResponseHandler(frontendService));
        frontendMsClient.addHandler(MessageType.SAVE_USER, new SaveUserDataResponseHandler(frontendService));
        messageSystem.addClient(frontendMsClient);
        logger.info("setup done");
    }

}