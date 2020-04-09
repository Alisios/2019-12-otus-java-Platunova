package ru.otus;

import org.hibernate.SessionFactory;
import ru.otus.api.dao.UserDao;
import ru.otus.api.model.User;
import ru.otus.api.service.DBServiceUser;
import ru.otus.api.service.DbServiceUserImpl;
import ru.otus.hibernate.HibernateUtils;
import ru.otus.hibernate.dao.UserDaoHibernate;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.server.UsersWebServer;
import ru.otus.server.UsersWebServerHW;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;


/**
 Класс демонстрирует работу сервера со страницей администратора.
 Стартовая страница: http://localhost:8080
 чтобы войти на страницу введите логин: admin, пароль: 11111
**/
public class WebServerHWDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {

        SessionFactory sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
                User.class);
        SessionManagerHibernate sessionManager = new SessionManagerHibernate(sessionFactory);
        UserDao userDao = new UserDaoHibernate(sessionManager);
        DBServiceUser dbServiceUser = new DbServiceUserImpl(userDao);
        initialUsers(dbServiceUser);

        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(dbServiceUser);

        UsersWebServer usersWebServer = new UsersWebServerHW(WEB_SERVER_PORT,authService, dbServiceUser, templateProcessor);

        usersWebServer.start();
        usersWebServer.join();

        sessionManager.close();
        sessionFactory.close();
    }

    static void initialUsers(DBServiceUser dbServiceUser){
        dbServiceUser.saveUser(new User ( "Вася",27, "vas", "11111"));
        dbServiceUser.saveUser(new User("Женя", 29, "zhenya", "11111"));
        dbServiceUser.saveUser(new User("Джон",30,  "admin", "11111"));
        dbServiceUser.saveUser(new User ( "Боб",37, "bob", "bobpassword"));
        dbServiceUser.saveUser(new User("Энтони", 19, "ent", "ent123"));
        dbServiceUser.saveUser(new User ( "Рома",33, "poma", "12345"));
        dbServiceUser.saveUser(new User("Саша", 35, "sashka", "12"));
        dbServiceUser.saveUser(new User("Эл", 28, "el123", "2222"));
    }

}
