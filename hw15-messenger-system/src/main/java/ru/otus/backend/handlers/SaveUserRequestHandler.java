package ru.otus.backend.handlers;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.api.model.User;
import ru.otus.backend.db.service.DBServiceUser;
import ru.otus.helpers.Serializers;
import ru.otus.messagesystem.Message;
import ru.otus.messagesystem.MessageType;
import ru.otus.messagesystem.RequestHandler;

import java.util.Optional;

public class SaveUserRequestHandler implements RequestHandler {
    private final DBServiceUser dbService;
    private PasswordEncoder passwordEncoder;

    public SaveUserRequestHandler(DBServiceUser dbService, PasswordEncoder passwordEncoder) {
        this.dbService = dbService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Message> handle(Message msg) {
        User user = Serializers.deserialize(msg.getPayload(), User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        long id = dbService.saveUser(user);
        User userSaved = dbService.getUser(id).get();
        return Optional.of(new Message(msg.getTo(), msg.getFrom(), msg.getId(), MessageType.SAVE_USER.getValue(), Serializers.serialize(userSaved)));
    }
}
