package ru.otus.listeners;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("Тест проверяет ")
class MsgDbListenerTest {

    private final DbHandler dbHandler = mock(DbHandler.class);

    MsgDbListener msgDbListener = new MsgDbListener(dbHandler);
    private final Message message = mock(Message.class);
    private final Channel channel = spy(Channel.class);
    long tag = 1L;
    Boolean isRedelivered = false;

    @Test
    @DisplayName("корректуню обработку сообщения и факт его подтверждения")
    void correctlyHandleMessageAndAckIt() throws IOException {
        msgDbListener.dbHandler(message, channel, isRedelivered, tag);
        verify(channel, times(1)).basicQos(1);
        verify(channel, times(1)).basicAck(tag, false);
    }

    @Test
    @DisplayName("корректную обработку исключения расшифровки смообщения и его отправка в dead-letters")
    void correctlyHandleMessageAnRejectIt() throws IOException {
        doThrow(new ClassCastException()).when(dbHandler).processMsgFromRabbit(message);
        assertDoesNotThrow(() -> msgDbListener.dbHandler(message, channel, isRedelivered, tag));
        verify(channel, times(1)).basicQos(1);
        verify(channel, times(1)).basicReject(tag, false);
        verify(channel, times(0)).basicAck(tag, false);
    }

    @Test
    @DisplayName("корректную обработку исключения и его переадресацию")
    void correctlyHandleMessageAndNotAckIt() throws IOException {
        doThrow(new RuntimeException()).when(dbHandler).processMsgFromRabbit(message);
        assertDoesNotThrow(() -> msgDbListener.dbHandler(message, channel, isRedelivered, tag));
        verify(channel, times(1)).basicQos(1);
        verify(channel, times(0)).basicReject(tag, false);
        verify(channel, times(0)).basicAck(tag, false);
    }
}