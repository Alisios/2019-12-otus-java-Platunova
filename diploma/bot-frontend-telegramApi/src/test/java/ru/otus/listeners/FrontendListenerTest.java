package ru.otus.listeners;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("Тест проверяет ")
class MsgDbListenerTest {

    private final FrontendRabbitHandler frontendRabbitHandler = mock(FrontendRabbitHandler.class);

    FrontendListener msgFrontListener = new FrontendListener(frontendRabbitHandler);
    private final Message message = mock(Message.class);
    private final Channel channel = spy(Channel.class);
    long tag = 1L;

    @Test
    @DisplayName("корректуню обработку сообщения и факт его подтверждения")
    void correctlyHandleMessageAndAckIt() throws IOException {
        msgFrontListener.frontendHandler(message, channel, false, tag);
        verify(channel, times(1)).basicQos(1);
        verify(channel, times(1)).basicAck(tag, false);
    }

    @Test
    @DisplayName("корректную обработку исключения расшифровки смообщения и его отправка в dead-letters")
    void correctlyHandleMessageAnRejectIt() throws IOException, TelegramApiException {
        doThrow(new ClassCastException()).when(frontendRabbitHandler).processMsgFromRabbit(message);
        assertDoesNotThrow(() -> msgFrontListener.frontendHandler(message, channel, true, tag));
        verify(channel, times(1)).basicQos(1);
        verify(channel, times(1)).basicReject(tag, false);
        verify(channel, times(0)).basicAck(tag, false);
    }

    @Test
    @DisplayName("корректную обработку исключения и его переадресацию")
    void correctlyHandleMessageAndNotAckIt() throws IOException, TelegramApiException {
        doThrow(new RuntimeException()).when(frontendRabbitHandler).processMsgFromRabbit(message);
        assertDoesNotThrow(() -> msgFrontListener.frontendHandler(message, channel, true, tag));
        verify(channel, times(1)).basicQos(1);
        verify(channel, times(1)).basicReject(tag, false);
        verify(channel, times(0)).basicAck(tag, false);
    }

    @Test
    @DisplayName("корректную обработку исключения и отправку в очередь обработки снова")
    void correctlyHandleMessageAndRedirectToHandlerAgain() throws IOException, TelegramApiException {
        doThrow(new RuntimeException()).when(frontendRabbitHandler).processMsgFromRabbit(message);
        assertDoesNotThrow(() -> msgFrontListener.frontendHandler(message, channel, true, tag));
        verify(channel, times(1)).basicQos(1);
        verify(channel, times(0)).basicReject(tag, true);
        verify(channel, times(0)).basicAck(tag, false);
    }
}