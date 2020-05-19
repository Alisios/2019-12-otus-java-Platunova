package ru.otus.front;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.otus.api.model.User;
import ru.otus.messagesystem.Message;
import ru.otus.messagesystem.MessageType;
import ru.otus.messagesystem.MsClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
@PropertySource(ignoreResourceNotFound = true, value = "app.properties")
public class FrontendServiceImpl implements FrontendService {
    private static final Logger logger = LoggerFactory.getLogger(FrontendServiceImpl.class);

    private final Map<UUID, Consumer<?>> consumerMap = new ConcurrentHashMap<>();
    private final MsClient msClient;
    private final String backendServiceClientName;

    public FrontendServiceImpl(@Value("${backendServiceClientName}") String backendServiceClientName,
                               @Qualifier("frontendMsClient") @Lazy MsClient msClient) {
        this.msClient = msClient;
        this.backendServiceClientName = backendServiceClientName;
    }

    @Override
    public void getUsers( Consumer<List<User>> dataConsumer) {
        Message outMsg = msClient.produceMessage(backendServiceClientName, null, MessageType.GET_USERS);
        consumerMap.put(outMsg.getId(), dataConsumer);
        msClient.sendMessage(outMsg);
    }

    @Override
    public void saveUser(User user, Consumer<User> dataConsumer) {
        Message outMsg = msClient.produceMessage(backendServiceClientName, user, MessageType.SAVE_USER);
        consumerMap.put(outMsg.getId(), dataConsumer);
        msClient.sendMessage(outMsg);
    }

    @Override
    public <T> Optional<Consumer<T>> takeConsumer(UUID sourceMessageId, Class<T> tClass) {
        Consumer<T> consumer = (Consumer<T>) consumerMap.remove(sourceMessageId);
        if (consumer == null) {
            logger.warn("consumer not found for:{}", sourceMessageId);
            return Optional.empty();
        }
        return Optional.of(consumer);
    }
}
