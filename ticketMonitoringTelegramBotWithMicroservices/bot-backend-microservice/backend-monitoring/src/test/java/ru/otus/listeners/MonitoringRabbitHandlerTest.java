package ru.otus.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import ru.otus.backend.MonitoringService;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BackendRabbitHandlerTest {

    final String stringEx = "stringEx";
    private final Message message = mock(Message.class, withSettings().serializable());
    private MessageModel mms;
    private final AmqpTemplate template = mock(AmqpTemplate.class, withSettings().serializable());
    private final RabbitMQProperties rabbitProperties = mock(RabbitMQProperties.class);
    MonitoringService monitoringService = mock(MonitoringService.class);
    RabbitHandler rabbitHandler = new MonitoringRabbitHandler(rabbitProperties, monitoringService, template);

    private final List<User> userList = new ArrayList<>(List.of(new User(1L, 202812830, new ConcertModel("Aerosmith (Аэросмит)",
                    "30 Июльчт 19:00",
                    "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                    "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                    new GregorianCalendar(2020, 4, 23).getTime()),
            new User(2L, 202812830, new ConcertModel("Aerosmith (Аэросмит)",
                    "30 Июльчт 19:00",
                    "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                    "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                    new GregorianCalendar(2020, 4, 23).getTime()),
            new User(3L, 202812830, new ConcertModel("Aerosmith (Аэросмит)",
                    "30 Июльчт 19:00",
                    "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
                    "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
                    new GregorianCalendar(2020, 4, 23).getTime())));


    @BeforeEach
    void set() {
        when(rabbitProperties.getBackProducerExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProduceQueue()).thenReturn(stringEx);
        when(rabbitProperties.getDbExchange()).thenReturn("db");
        when(rabbitProperties.getDbQueue()).thenReturn("db");
        mms = new MessageModel(MessageType.GET_MONITORING_RESULT, Serializers.serialize(userList));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
    }

    @Test
    @DisplayName("отправку сообщений только нужным пользователям и запрос на удаление их из базы")
    void sendingMessagesToUsersWhichShouldBeSendAndSendingRequestForTheirDeleting() throws IOException {
        when(monitoringService.checkIfUserShouldBeNotified(userList.get(0))).thenReturn(true);
        when(monitoringService.checkIfUserShouldBeNotified(userList.get(1))).thenReturn(true);
        when(monitoringService.checkIfUserShouldBeNotified(userList.get(2))).thenReturn(false);
        assertDoesNotThrow(() -> rabbitHandler.processMsgFromRabbit(message));
        verify(monitoringService, times(1)).getMonitoringResult(userList);
        verify(monitoringService, times(3)).checkIfUserShouldBeNotified(any());
        verify(rabbitProperties, times(2)).getBackProducerExchange();
        verify(rabbitProperties, times(2)).getBackProduceQueue();
        verify(rabbitProperties, times(2)).getDbExchange();
        verify(rabbitProperties, times(2)).getDbQueue();
        verify(template, times(4)).convertAndSend(anyString(), anyString(), (Object) any());
    }
    @Test
    @DisplayName("корректно реагирует на исключение")
    void correctlyReactOnException() throws IOException {
        when(monitoringService.checkIfUserShouldBeNotified(userList.get(0))).thenReturn(true);
        doThrow(new IOException()).when(monitoringService).getMonitoringResult(userList);
        assertThrows(IOException.class, () -> {rabbitHandler.processMsgFromRabbit(message);});
        verify(monitoringService, times(0)).checkIfUserShouldBeNotified(any());
        verify(rabbitProperties, times(0)).getBackProducerExchange();
        verify(rabbitProperties, times(0)).getBackProduceQueue();
        verify(rabbitProperties, times(0)).getDbExchange();
        verify(rabbitProperties, times(0)).getDbQueue();
        verify(template, times(0)).convertAndSend(anyString(), anyString(), (Object) any());
    }


}