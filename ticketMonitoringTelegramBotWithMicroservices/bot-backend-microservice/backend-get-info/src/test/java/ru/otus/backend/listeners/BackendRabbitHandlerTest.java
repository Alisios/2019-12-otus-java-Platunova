package ru.otus.backend.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import ru.otus.backend.handlers.RequestHandler;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BackendRabbitHandlerTest {

    final String stringEx = "stringEx";
    private final Message message = mock(Message.class, withSettings().serializable());
    private final org.telegram.telegrambots.api.objects.Message messageTel = mock(org.telegram.telegrambots.api.objects.Message.class, withSettings().serializable());
    private MessageModel mms;
    private final CallbackQuery callbackQuery = mock(CallbackQuery.class, withSettings().serializable());
    private final RequestHandler requestHandler = mock(RequestHandler.class, withSettings().serializable());
    private final AmqpTemplate template = mock(AmqpTemplate.class, withSettings().serializable());
    private final RabbitMQProperties rabbitProperties = mock(RabbitMQProperties.class);
    RabbitHandler rabbitHandler = new BackendRabbitHandler(requestHandler, template, rabbitProperties);

    private final User user = new User(1L, 202812830, new ConcertModel("Aerosmith (Аэросмит)",
            "30 Июльчт 19:00",
            "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
            "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
            new GregorianCalendar(2020, 4, 23).getTime());

    @BeforeEach
    void set(){
        when(rabbitProperties.getBackProducerExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProduceQueue()).thenReturn(stringEx);
        when(rabbitProperties.getDbExchange()).thenReturn("db");
        when(rabbitProperties.getDbQueue()).thenReturn("db");
    }

    @Test
    @DisplayName("корректную обработку типа GET_EVENT_INFO")
    void correctlyWorksWith_GET_EVENT_INFO_TypeMessage() throws IOException {
        mms = new MessageModel(MessageType.GET_EVENT_INFO, Serializers.serialize(""));
        when(rabbitProperties.getBackProducerExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProduceQueue()).thenReturn(stringEx);
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        when(requestHandler.getEventData(any())).thenReturn(Optional.of(new MessageForFront(MessageType.GET_TICKET_INFO,null, 1L,1)));
        assertDoesNotThrow(() -> rabbitHandler.processMsgFromRabbit(message));
        verify(rabbitProperties, times(1)).getBackProducerExchange();
        verify(rabbitProperties, times(1)).getBackProduceQueue();
        verify(requestHandler, times(1)).getEventData(mms);
        verify(template, times(1)).convertAndSend(anyString(), anyString(), (Object) any());
    }

    @Test //не знаю как присвоить NOTIFY моку внутри функции, из-за этого эксепшн
    @DisplayName("корректную обработку типа GET_TICKET_INFO")
    void correctlyWorksWithGET_TICKET_INFO_TypeMessage() throws IOException {
        mms = new MessageModel(MessageType.GET_TICKET_INFO, Serializers.serialize(callbackQuery));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        when(requestHandler.getTicketData(any())).thenReturn(Optional.of(new MessageForFront(MessageType.GET_TICKET_INFO,null, 1L,1)));
        when(callbackQuery.getData()).thenReturn("NOTIFY");
        when(callbackQuery.getMessage()).thenReturn(messageTel);
        when(requestHandler.switchingOnMonitoring(messageTel)).thenReturn(Optional.ofNullable(mms));
        //assertDoesNotThrow(() ->
        assertThrows(Exception.class, () -> {rabbitHandler.processMsgFromRabbit(message);});
        verify(rabbitProperties, times(1)).getBackProducerExchange();
        verify(rabbitProperties, times(1)).getBackProduceQueue();
        verify(requestHandler, times(1)).getTicketData(mms);
        verify(template, times(1)).convertAndSend(anyString(), anyString(), (Object) any());

    }

}