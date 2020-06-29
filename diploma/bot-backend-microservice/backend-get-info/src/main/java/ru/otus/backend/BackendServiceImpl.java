package ru.otus.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.EventException;
import ru.otus.backend.eventApi.MonitoredEvent;
import ru.otus.backend.eventApi.helper.CommandHandler;
import ru.otus.backend.model.User;
import ru.otus.helpers.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * один из основных классов бизнес логики:
 * определение типа сообщения и при необходимости перенаправление нужному обработчику
 **/

@Service
public class BackendServiceImpl implements BackendService {
    private static final Logger logger = LoggerFactory.getLogger(BackendServiceImpl.class);
    private final MonitoredEvent monitoredEvent;
    private final CommandHandler commandHandler = new CommandHandler();
    private final String COMMAND = "/";
    private final String NOTHING = "NOTHING";
    private final String NOTIFY = "NOTIFY";
    private final String NO = "NO";
    private final String errorMessage = "Извините, запрос не может быть выполнен :( Попробуйте повторить позже";

    public BackendServiceImpl(MonitoredEvent monitoredEvent) {
        this.monitoredEvent = monitoredEvent;
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public MessageForFront getEventData(Message message) throws IOException {
        try {
            if (message.getText().trim().startsWith(COMMAND))
                return new MessageForFront(MessageType.GET_EVENT_INFO, Serializers.serialize(commandHandler.getInfo(message.getText())), message.getChatId(), message.getMessageId());
            else
                return monitoredEvent.getConcertInfo(message);
        } catch (RuntimeException ex) {
            throw new EventException("Error with handling message: " + message.getText() + ". " + ex.getCause() + ". " + ex.getMessage());
        }
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public MessageForFront getTicketData(CallbackQuery callbackQuery) throws IOException {
        Message message = callbackQuery.getMessage();
        String payload;
        MessageForFront messageForFront = new MessageForFront(MessageType.GET_TICKET_INFO, null, message.getChatId(), message.getMessageId());
        switch (callbackQuery.getData()) {
            case NOTHING:
                payload = "Очень жаль! Обращайтесь еще!";
                logger.info("MessageType after Nothing {}", payload);
                break;

            case NO:
                payload = "Обращайтесь еще!";
                logger.info("MessageType after NO {}", payload);
                break;

            case NOTIFY:
                payload = "Хорошо! Я сообщу, если появятся билеты в фанзону или танцевальный партер!";
                logger.info("MessageType after NOTIFY{}", payload);
                break;

            default:
                try {
                    payload = monitoredEvent.getTicketInfo(message.getText(), Integer.parseInt(callbackQuery.getData()));
                    if (payload.contains("Хотите отслеживать появление билетов в фанзону"))
                        messageForFront.setCallbackType(CallbackType.IF_SHOULD_BE_MONITORED.getValue());
                } catch (RuntimeException ex) {
                    throw new EventException("Error with handling message " + message.getText() + ex.getCause() + Arrays.toString(ex.getStackTrace()));
                }
        }
        messageForFront.setPayload(Serializers.serialize(payload));
        return messageForFront;
    }

    @Override
    public User switchingOnEventMonitoring(Message message) {
        return monitoredEvent.monitorOfEvent(message);
    }

    @Override
    public MessageForFront errorMessage(MessageModel message) {
        Message msg;
        try {
            if (message.getMessageType().getValue().equals(MessageType.GET_EVENT_INFO.getValue())) {
                msg = Serializers.deserialize(message.getPayload(), Message.class);
                return new MessageForFront(MessageType.NOTIFY, Serializers.serialize(errorMessage), msg.getChatId(), msg.getMessageId());
            } else if (message.getMessageType().getValue().equals(MessageType.GET_TICKET_INFO.getValue())) {
                msg = Serializers.deserialize(message.getPayload(), CallbackQuery.class).getMessage();
                return new MessageForFront(MessageType.NOTIFY, Serializers.serialize(errorMessage), msg.getChatId(), msg.getMessageId());
            }
        } catch (RuntimeException ex) {
            throw new EventException("Error in forming error message for user: " + ex.getMessage());
        }
        return null;
    }
}