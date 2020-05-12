package ru.otus.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.db.hibernate.dao.UserDao;
import ru.otus.db.hibernate.HibernateUtils;
import ru.otus.db.hibernate.dao.UserDaoHibernate;
import ru.otus.db.hibernate.dao.UserDaoHibernateIntegrationTest;
import ru.otus.db.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.db.service.DBServiceUser;
import ru.otus.db.service.DbServiceUserImpl;
import ru.otus.backend.eventApi.Concert;
import ru.otus.backend.eventApi.MonitoredEvent;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.eventApi.helpers.HtmlParserKassirRu;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест проверяет:")
class ConcertJobListenerTest {
    private static Logger logger = LoggerFactory.getLogger(ConcertJobListenerTest.class);
    private DBServiceUser dbServiceUser;
    private SessionManagerHibernate sessionManager;
    private SessionFactory sessionFactory;

    @BeforeEach
    void set(){
        sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml",
                User.class, ConcertModel.class);

        sessionManager = new SessionManagerHibernate(sessionFactory);
        UserDao userDao = new UserDaoHibernate(sessionManager);
        dbServiceUser = new DbServiceUserImpl(userDao);
    }

    @AfterEach
    void closeAfter(){
        sessionManager.close();
        sessionFactory.close();
    }

    @Test
    @DisplayName("корректную работу с пользователями в Job в потоке мониторинга Quartz")
    void checkCorrectWorkOfQuartzJob() {
        List<User> userList = UserDaoHibernateIntegrationTest.initiateUsersForTest();
        userList.forEach(dbServiceUser::saveUser);
        HtmlParser htmlParser = new HtmlParserKassirRu();
        MonitoredEvent monitoredEvent = new Concert(htmlParser);
        List<User> userListDb = dbServiceUser.getAllUsers();
        assertThat(dbServiceUser.getUsersForNotifying()).isNullOrEmpty();
        if (userList.size() != 0)
            for (User user : userListDb) {
                if (monitoredEvent.checkingTickets(user)) {
                    dbServiceUser.saveUser(user);
                }
                if (user.getDateOfMonitorFinish().before(new Date())) {
                    user.setDateExpired(true);
                    dbServiceUser.saveUser(user);
                }
            }
        List<User>  userListDb2 = dbServiceUser.getAllUsers();
        assertThat(userListDb2).isNotEqualTo(userListDb);
        assertThat(dbServiceUser.getUsersForNotifying()).isNotEmpty();
    }
}