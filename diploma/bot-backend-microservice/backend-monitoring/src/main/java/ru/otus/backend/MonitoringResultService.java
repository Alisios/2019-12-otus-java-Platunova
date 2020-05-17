package ru.otus.backend;

import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;

import java.util.Optional;

public interface MonitoringResultService {

    Optional<MessageForFront> getMonitoringResult(User user);
}
