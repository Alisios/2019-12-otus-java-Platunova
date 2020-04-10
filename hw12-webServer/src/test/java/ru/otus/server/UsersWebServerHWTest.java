package ru.otus.server;

import org.eclipse.jetty.http.HttpMethod;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.*;
import ru.otus.api.dao.UserDao;
import ru.otus.api.model.User;
import ru.otus.api.service.DbServiceUserImpl;
import ru.otus.api.sessionmanager.SessionManager;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;

import static ru.otus.server.WebServerHelper.*;

import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@DisplayName("Тест сервера должен ")
@ExtendWith(MockitoExtension.class)
class UsersWebServerHWTest {

    private static final int WEB_SERVER_PORT = 8080;
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT + "/";
    private static final String LOGIN_URL = "login";
    private static final String USERS_URL = "users";

    private static final String NOT_ADMIN_USER_LOGIN ="vas";
    private static final String NOT_ADMIN_USER_RIGHT_PASSWORD = "123";
    private static final String ADMIN_USER_LOGIN = "admin";
    private static final String ADMIN_USER_PASSWORD = "11111";

    private static List<User> usersList =new ArrayList<>(Arrays.asList(
            new User ( "Вася",27, "vas", "12345"),
            new User ( "Женя",30, "zhen", "3456"),
            new User ( "Боб>",21, "bob", "56789"),
            new User("Джон",30, "admin", "11111")
    ));

    private static final User ADMIN_USER = new User("Джон",30, "admin", "11111");

    private static SessionManager sessionManager = mock(SessionManager.class);
    private static UserDao userDao = mock(UserDao.class);

    private static DbServiceUserImpl dbServiceUser;

    private static UserAuthService authService;

    private static UsersWebServer webServer;

    @BeforeAll
    static void setUp() throws Exception {

        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        given(userDao.getSessionManager()).willReturn(sessionManager);
        given(userDao.findByLogin(ADMIN_USER_LOGIN)).willReturn(Optional.of(ADMIN_USER));
        given(userDao.findByLogin(NOT_ADMIN_USER_LOGIN)).willReturn(Optional.of(usersList.get(0)));

        dbServiceUser = new DbServiceUserImpl(userDao);
        authService = new UserAuthServiceImpl(dbServiceUser);
        given(userDao.findAllUser()).willReturn((usersList));


        webServer = new UsersWebServerHW(WEB_SERVER_PORT,authService, dbServiceUser, templateProcessor);
        webServer.start();
    }

    @DisplayName("проверять правильную работу реализованного userDao, поиск User по id и login, вывод всех пользователей")
    @Test
    void checkCorrectWorkWithUsers() {
        given(userDao.saveUser(any())).willReturn(1L);
        long id = dbServiceUser.saveUser(new User());
        assertThat(id).isEqualTo(1L);

        User expectedUser =new User ( "Вася",27, "vas", "12345");
        given(userDao.findById(1)).willReturn(Optional.of(expectedUser));
        assertThat(dbServiceUser.getUser(1)).isPresent().get().isEqualToComparingFieldByField(expectedUser);

        given(userDao.findByLogin("vas")).willReturn(Optional.of(expectedUser));
        assertThat(dbServiceUser.getUser("vas")).isPresent().get().isEqualToComparingFieldByField(expectedUser);

        assertThat(dbServiceUser.getAllUsers()).isEqualTo(usersList);
    }

    @DisplayName("проверять, что аутентифицироваться может только администратор")
    @Test
    void checkOnlyAdminLogin(){
        assertThat(authService.authenticate(ADMIN_USER_LOGIN, ADMIN_USER_PASSWORD)).isEqualTo(true);
        assertThat(authService.authenticate(NOT_ADMIN_USER_LOGIN, ADMIN_USER_PASSWORD)).isFalse();
        assertThat(authService.authenticate(ADMIN_USER_LOGIN, NOT_ADMIN_USER_RIGHT_PASSWORD)).isFalse();
        assertThat(authService.authenticate(NOT_ADMIN_USER_LOGIN, NOT_ADMIN_USER_RIGHT_PASSWORD)).isFalse();
    }


    @DisplayName("возвращать 302 при запросе пользователей без аутентификации ")
    @Test
    void shouldReturnForbiddenStatusForUserRequestWhenUnauthorized() throws Exception {
        HttpURLConnection connection = sendRequest(buildUrl(WEB_SERVER_URL, USERS_URL, null), HttpMethod.GET);
        connection.setInstanceFollowRedirects(false);
        int responseCode = connection.getResponseCode();
        assertThat(responseCode).isEqualTo(HttpURLConnection.HTTP_MOVED_TEMP);
    }

    @DisplayName("возвращать ID сессии при выполнении входа с верными данными")
    @Test
    void shouldReturnJSessionIdWhenLoggingInWithCorrectData() throws Exception {
        HttpCookie jSessionIdCookie = login(buildUrl(WEB_SERVER_URL, LOGIN_URL, null), ADMIN_USER_LOGIN, ADMIN_USER_PASSWORD);
        assertThat(jSessionIdCookie).isNotNull();
    }

    @DisplayName("не возвращать ID сессии при выполнении входа если данные входа не верны")
    @Test
    void shouldNotReturnJSessionIdWhenLoggingInWithIncorrectData() throws Exception {
        HttpCookie jSessionIdCookie = login(buildUrl(WEB_SERVER_URL, LOGIN_URL, null), NOT_ADMIN_USER_LOGIN, NOT_ADMIN_USER_RIGHT_PASSWORD);
        assertThat(jSessionIdCookie).isNull();
    }
}