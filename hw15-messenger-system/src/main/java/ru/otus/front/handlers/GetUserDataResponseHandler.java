package ru.otus.front.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.helpers.Serializers;
import ru.otus.front.FrontendService;
import ru.otus.messagesystem.Message;
import ru.otus.messagesystem.RequestHandler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetUserDataResponseHandler implements RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(GetUserDataResponseHandler.class);

    private final FrontendService frontendService;

    public GetUserDataResponseHandler(FrontendService frontendService) {
        this.frontendService = frontendService;
    }

    @Override
    public Optional<Message> handle(Message msg) {
        try {
            List userData = Serializers.deserialize(msg.getPayload(), List.class);
            UUID sourceMessageId = msg.getSourceMessageId().orElseThrow(() -> new RuntimeException("Not found sourceMsg for message:" + msg.getId()));
            frontendService.takeConsumer(sourceMessageId, List.class).ifPresent(consumer -> consumer.accept(userData));
        } catch (Exception ex) {
            logger.error("msg:" + msg, ex);
        }
        return Optional.empty();
    }
}
