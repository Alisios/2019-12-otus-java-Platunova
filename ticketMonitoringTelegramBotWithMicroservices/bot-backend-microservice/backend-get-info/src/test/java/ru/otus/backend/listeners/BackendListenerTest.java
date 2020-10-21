package ru.otus.backend.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import ru.otus.backend.eventApi.EventException;
import ru.otus.backend.handlers.RequestHandler;
import ru.otus.configurations.RabbitMQProperties;
import com.rabbitmq.client.Channel;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Тесты проверяют: ")
class BackendListenerTest {

    private final RabbitMQProperties rabbitProperties = mock(RabbitMQProperties.class);

    private final RabbitHandler backendRabbitHandler = mock(RabbitHandler.class);
    private final RequestHandler requestHandler = mock(RequestHandler.class);

    private final AmqpTemplate template = mock(AmqpTemplate.class);

    private final Message message = mock(Message.class);
    private final Channel channelMessageConsumer = spy(Channel.class);
    private final BackendListener backendListener = new BackendListener(rabbitProperties, backendRabbitHandler, requestHandler, template);
    private MessageModel mms;
    private final long tag = 123L;
    private final String stringEx = "stringEx";

    @BeforeEach
    void set() {
        mms = new MessageModel(MessageType.GET_EVENT_INFO, Serializers.serialize("String"));
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
    }

    @Test
    @DisplayName("подтверждение получения сообщения в случае успешной его обработки")
    void acknowledgeMessageInCaseOfSuccessfulHandling() throws IOException {
        assertDoesNotThrow(() -> backendListener.backendHandler(message, channelMessageConsumer, false, tag));
        verify(channelMessageConsumer, times(1)).basicQos(1);
        verify(channelMessageConsumer, times(1)).basicAck(tag, false);
    }

    @Test
    @DisplayName("пересылку сообщения другому обработчику и новая попытка обработать сообщение в случае проблем с интернетом")
    void redirectingMessageInCaseOfUnknownHostException() throws IOException {
        doThrow(new UnknownHostException()).when(backendRabbitHandler).processMsgFromRabbit(message);
        assertDoesNotThrow(() -> backendListener.backendHandler(message, channelMessageConsumer, false, tag));
        verify(channelMessageConsumer, times(1)).basicQos(1);
        verify(channelMessageConsumer, times(0)).basicAck(tag, false);
        verify(channelMessageConsumer, times(1)).basicReject(tag, true);
    }

    @Test
    @DisplayName("отправку сообщения в очередь dead-letters в случае невозможности его обработать")
    void rejectingMessageInCaseOfImpossibilityOfHandling() throws IOException {
        doThrow(new EventException(" ")).when(backendRabbitHandler).processMsgFromRabbit(message);
        when(requestHandler.errorMessageForFront(mms)).thenReturn(Optional.of(new MessageForFront(MessageType.GET_EVENT_INFO, null, 1L, 1)));
        when(rabbitProperties.getBackProducerExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProduceQueue()).thenReturn(stringEx);
        assertDoesNotThrow(() -> backendListener.backendHandler(message, channelMessageConsumer, false, tag));
        verify(channelMessageConsumer, times(1)).basicQos(1);
        verify(channelMessageConsumer, times(0)).basicAck(tag, false);
        verify(channelMessageConsumer, times(1)).basicReject(tag, false);
        verify(template, times(1)).convertAndSend(anyString(), anyString(), (Object) any());

    }


    @Test
    @DisplayName("попытку отправить сообщение на повторную обработку, если неизвестное исключение при обработку вылетает в первый раз")
    void redirectingMessageInCaseOfUnknownException() throws IOException {
        doThrow(new NullPointerException()).when(backendRabbitHandler).processMsgFromRabbit(message);
        when(requestHandler.errorMessageForFront(mms)).thenReturn(Optional.of(new MessageForFront(MessageType.GET_EVENT_INFO, null, 1L, 1)));
        when(rabbitProperties.getBackProducerExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProduceQueue()).thenReturn(stringEx);
        assertDoesNotThrow(() -> backendListener.backendHandler(message, channelMessageConsumer, false, tag));
        verify(channelMessageConsumer, times(1)).basicQos(1);
        verify(channelMessageConsumer, times(0)).basicAck(tag, false);
        verify(channelMessageConsumer, times(1)).basicReject(tag, true);
    }

    @Test
    @DisplayName("отправку сообщения в dead-letters, если неизвестное исключение при обработку вылетает во второй раз")
    void rejectingMessageInCaseOfUnknownException() throws IOException {
        doThrow(new NullPointerException()).when(backendRabbitHandler).processMsgFromRabbit(message);
        when(requestHandler.errorMessageForFront(mms)).thenReturn(Optional.of(new MessageForFront(MessageType.GET_EVENT_INFO, null, 1L, 1)));
        when(rabbitProperties.getBackProducerExchange()).thenReturn(stringEx);
        when(rabbitProperties.getBackProduceQueue()).thenReturn(stringEx);
        assertDoesNotThrow(() -> backendListener.backendHandler(message, channelMessageConsumer, true, tag));
        verify(channelMessageConsumer, times(1)).basicQos(1);
        verify(channelMessageConsumer, times(0)).basicAck(tag, false);
        verify(channelMessageConsumer, times(1)).basicReject(tag, false);
        verify(template, times(1)).convertAndSend(anyString(), anyString(), (Object) any());
    }
}