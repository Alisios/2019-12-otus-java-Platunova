package ru.otus.backend;

import ru.otus.backend.model.User;

import java.io.IOException;
import java.util.List;


public interface MonitoringService {

    void getMonitoringResult(List<User> user) throws IOException;

    Boolean checkIfUserShouldBeNotified(User user);

}
