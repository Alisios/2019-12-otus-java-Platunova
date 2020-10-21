package ru.otus.backend;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.backend.eventApi.EventInformationService;
import ru.otus.backend.model.User;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * основной класс бизнес логики:
 * проверяет 1 из 2 событий: появились ли билеты на мероприятие и вышел ли срок мониторинга
 */

@Slf4j
@Service("userMonitoringService")
@NoArgsConstructor
public class UserMonitoringService implements MonitoringService {

    private EventInformationService eventInformationService;

    @Autowired
    public UserMonitoringService(EventInformationService eventInformationService) {
        this.eventInformationService = eventInformationService;
    }

    @Override
    public void getMonitoringResult(List<User> userList) throws IOException {
        Map<String, List<User>> map = userList.stream().collect(Collectors.groupingBy(user -> user.getConcert().getArtist()));
        map.keySet().forEach(c -> log.info("{} : number of users: {}", c, map.get(c).size()));
        for (String key : map.keySet()) {
            User u = map.get(key).get(0);
            String message = eventInformationService.getTicketInformation(u);
            if (!u.getConcert().getShouldBeMonitored()) {
                log.info("Переключение флага ShouldBeMonitored");
                map.get(key).forEach(user -> {
                    user.setIsMonitoringSuccessful(true);
                    user.setMessageText(message);
                    user.getConcert().setShouldBeMonitored(false);
                });
            } else {
                if (u.getDateOfMonitorFinish().before(new Date())) {
                    map.get(key).forEach(user -> user.setIsDateExpired(true));
                    log.info("Переключение флага setDateExpired");
                }
            }
        }
    }

    @Override
    public Boolean checkIfUserShouldBeNotified(User user) {
        return user.getIsMonitoringSuccessful() || user.getIsDateExpired();
    }
}
