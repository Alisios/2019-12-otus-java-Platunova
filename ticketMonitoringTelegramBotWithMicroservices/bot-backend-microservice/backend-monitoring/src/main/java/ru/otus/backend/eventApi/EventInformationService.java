package ru.otus.backend.eventApi;

import ru.otus.backend.model.User;

import java.io.IOException;

public interface EventInformationService {

    String getTicketInformation(User user) throws IOException;

}
