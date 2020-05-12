package ru.otus.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.MonitoredEvent;
import ru.otus.backend.helper.CommandHandler;
import ru.otus.backend.helper.DateParser;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;
import java.util.Calendar;


public class BackendServiceImpl implements  BackendService {
    private static Logger logger = LoggerFactory.getLogger(BackendServiceImpl.class);
    private MonitoredEvent monitoredEvent;
    private CommandHandler commandHandler = new CommandHandler();
    private DateParser dateParser  = new DateParser();
    final private String COMMAND = "/";
    final private String NOTHING = "NOTHING";
    final private String NOTIFY = "NOTIFY";
    final private String NO = "NO";

    public BackendServiceImpl(MonitoredEvent monitoredEvent) {
        this.monitoredEvent = monitoredEvent;
    }

    @Override
    public MessageForFront getEventData(Message message) {
        if (message.getText().trim().startsWith(COMMAND))
            return new MessageForFront(MessageType.GET_EVENT_INFO, Serializers.serialize(commandHandler.getInfo(message.getText())),message.getChatId(), message.getMessageId());
        else
            return monitoredEvent.getConcertInfo(message);
    }

    @Override
    public MessageForFront getTicketData(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String payload = "";
        MessageForFront messageForFront = new MessageForFront(MessageType.GET_TICKET_INFO,Serializers.serialize(payload), message.getChatId(), message.getMessageId());
        switch (callbackQuery.getData()) {
            case NOTHING:
                monitoredEvent.endOfWorkForThisEvent(message.getChatId(), message.getMessageId()-1);
                payload =  "Очень жаль! Обращайтесь еще!";
                logger.info("MessageType after Nothing {}", payload);
                break;

            case NO:
                monitoredEvent.endOfWorkForThisEvent(message.getChatId(), message.getMessageId()-1);
                payload =  "Обращайтесь еще!";
                logger.info("MessageType after NO {}", payload);
                break;

            case NOTIFY:
               // this.switchingOnEventMonitoring(message);
                // monitoredEvent.setType(MessageType.HELLO); //??
                payload = "Хорошо! Я сообщу, если появятся билеты в фанзону или танцевальный партер!";
                logger.info("MessageType after NOTIFY{}", payload);
                break;

            default:
                return monitoredEvent.getTicketInfo(message.getChatId(), message.getMessageId() - 1, Integer.parseInt(callbackQuery.getData()), message);
        }
        messageForFront.setPayload(Serializers.serialize(payload));
        return messageForFront;
    }

    @Override
    public MessageForFront getMonitoringResult(User user) {
        String payload = "";
        MessageForFront messageForFront = new MessageForFront(MessageType.NOTIFY, Serializers.serialize(payload), user.getChatId(), -1);
        if (user.getMonitoringSuccessful())
            payload= "Запрашиваемые билеты на " + user.getConcert().getArtist() + " появились! Информация о событии:\n" +
                    user.getMessageText();
        else
            payload = "Время события или максимальное время ожидания появления билетов истекло. " +
                    "Запрашиваемые билеты на " + user.getConcert().getArtist() + " не появились :(";
        messageForFront.setPayload(Serializers.serialize(payload));
        return messageForFront;
    }
    @Override
    public User switchingOnEventMonitoring(Message message){
        ConcertModel concertModel = monitoredEvent.getModel(message.getChatId(),message.getMessageId()-1);
        Calendar dateOfMonitorFinish = dateParser.parse(concertModel.getDate());
        dateOfMonitorFinish.set(Calendar.DAY_OF_MONTH, dateOfMonitorFinish.get(Calendar.DAY_OF_MONTH)-2);
        User user = new User( message.getChatId(), concertModel, dateOfMonitorFinish.getTime());
        concertModel.setOwner(user);
        monitoredEvent.endOfWorkForThisEvent(message.getChatId(), message.getMessageId()-1);
        return user;
    }
}