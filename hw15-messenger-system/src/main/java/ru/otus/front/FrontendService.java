package ru.otus.front;

import ru.otus.api.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface FrontendService {

    void getUsers(Consumer<List<User>> dataConsumer);

    void saveUser(User user, Consumer<User> dataConsumer);

    <T> Optional<Consumer<T>> takeConsumer(UUID sourceMessageId, Class<T> tClass);
}

