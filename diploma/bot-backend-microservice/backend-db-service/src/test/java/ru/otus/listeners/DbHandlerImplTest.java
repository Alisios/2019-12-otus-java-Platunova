package ru.otus.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.test.context.junit4.SpringRunner;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.db.service.DBServiceUser;
import ru.otus.db.service.DBServiceUserJPA;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тест проверяет ")
class DbHandlerImplTest {
    final String stringEx = "stringEx";
    private final Message message = mock(Message.class);
    private MessageModel mms;
    private final AmqpTemplate template = mock(AmqpTemplate.class);
    private final DBServiceUser dbServiceUser = mock(DBServiceUserJPA.class);
    private DbHandler dbHandler;
    private final RabbitMQProperties rabbitProperties = mock(RabbitMQProperties.class);

    private final User user = new User(1L, 202812830, new ConcertModel("Aerosmith (Аэросмит)",
            "30 Июльчт 19:00",
            "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
            "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
            new GregorianCalendar(2020, 4, 23).getTime());

    @BeforeEach
    void setUp() {
        dbHandler = new DbHandlerImpl(dbServiceUser, template, rabbitProperties);
    }

    @Test
    @DisplayName("корректную обработку типа Save")
    void correctlyWorksWithSaveTypeMessage() {
        mms = new MessageModel(MessageType.SAVE_USER, Serializers.serialize(user));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        assertDoesNotThrow(() -> dbHandler.processMsgFromRabbit(message));
        verify(dbServiceUser, times(1)).saveUser(user);
    }

    @Test
    @DisplayName("выброс исключения при неправильном типе сущности в сообщении")
    void correctlyThrowExceptionWhenWrongTypeOfEntity() {
        String s = "ошибка расшифровки";
        mms = new MessageModel(MessageType.SAVE_USER, Serializers.serialize(s));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        verify(dbServiceUser, times(0)).saveUser(any());
        assertThrows(ClassCastException.class, () -> {
            dbHandler.processMsgFromRabbit(message);
        });
    }

    @Test
    @DisplayName("корректную обработку типа Save")
    void correctlyWorksWithFindAllTypeMessage() {
        mms = new MessageModel(MessageType.SAVE_USER, Serializers.serialize(user));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        when(template.convertSendAndReceive(any())).thenThrow(RuntimeException.class);
        when(dbServiceUser.saveUser(user)).thenReturn(user);
        assertDoesNotThrow(() -> dbHandler.processMsgFromRabbit(message));
    }

    @Test
    @DisplayName("корректную обработку типа Delete")
    void correctlyWorksWithDeleteTypeMessage() {
        user.getConcert().setOwner(user);
        mms = new MessageModel(MessageType.DELETE_USER, Serializers.serialize(user));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        assertDoesNotThrow(() -> dbHandler.processMsgFromRabbit(message));
        verify(dbServiceUser, times(1)).delete(user.getId());
    }

    @Test
    @DisplayName("корректную обработку типа DeletebyUser")
    void correctlyWorksWithDeletebyUserTypeMessage() {
        user.getConcert().setOwner(user);
        mms = new MessageModel(MessageType.DELETE_USER_BY_ADMIN, Serializers.serialize(1L));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        assertDoesNotThrow(() -> dbHandler.processMsgFromRabbit(message));
        verify(dbServiceUser, times(1)).delete(user.getId());
    }

    @Test
    @DisplayName("корректную обработку исключений при Delete")
    void correctlyWorksWithExceptionsDeleteTypeMessage() {
        user.getConcert().setOwner(user);
        mms = new MessageModel(MessageType.DELETE_USER, Serializers.serialize(user));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        doThrow(new RuntimeException()).when(dbServiceUser).delete(user.getId());
        //willThrow(new RuntimeException()).given(dbServiceUser).delete(user.getId());
        assertThrows(RuntimeException.class, () -> {
            dbHandler.processMsgFromRabbit(message);
        });
    }

    @Test
    @DisplayName("корректную обработку типа GetUsersByAdminType")
    void correctlyWorksWithGetUsersByAdminTypeMessage() {
        user.getConcert().setOwner(user);
        mms = new MessageModel(MessageType.ADMIN_GET_USERS, Serializers.serialize(user));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        when(rabbitProperties.getBackProducerWebExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProducerToWebQueue()).thenReturn(stringEx);
        assertDoesNotThrow(() -> dbHandler.processMsgFromRabbit(message));
        verify(dbServiceUser, times(1)).getAllUsers();
        verify(template, times(1)).convertAndSend(anyString(), anyString(), (Object) any());
    }

    @Test
    @DisplayName("бросание исключения при ошибке в отправке по rabbitMq")
    void throwingAmqpExceptionWhenSendingError() throws IOException {
        user.getConcert().setOwner(user);
        mms = new MessageModel(MessageType.ADMIN_GET_USERS, Serializers.serialize(user));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        when(rabbitProperties.getBackProducerWebExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProducerToWebQueue()).thenReturn(stringEx);
        doThrow(new AmqpException("")).when(template).convertAndSend(anyString(), anyString(), (Object) any());
        assertThrows(AmqpException.class, () -> { dbHandler.processMsgFromRabbit(message); });
        verify(dbServiceUser, times(1)).getAllUsers();
    }

}