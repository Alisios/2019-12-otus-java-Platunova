package ru.otus.backend.handlers;

import ru.otus.api.model.User;
import ru.otus.backend.db.service.DBServiceUser;
import ru.otus.helpers.Serializers;
import ru.otus.messagesystem.Message;
import ru.otus.messagesystem.MessageType;
import ru.otus.messagesystem.RequestHandler;

import java.util.List;
import java.util.Optional;


public class GetUsersDataRequestHandler implements RequestHandler {
    private final DBServiceUser dbService;

    public GetUsersDataRequestHandler(DBServiceUser dbService) {
        this.dbService = dbService;
    }

    @Override
    public Optional<Message> handle(Message msg) {
        List<User> users = dbService.getAllUsers();
        return Optional.of(new Message(msg.getTo(), msg.getFrom(), msg.getId(), MessageType.GET_USERS.getValue(), Serializers.serialize(users)));
    }
}
