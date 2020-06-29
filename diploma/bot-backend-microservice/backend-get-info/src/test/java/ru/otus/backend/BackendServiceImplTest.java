package ru.otus.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.EventException;
import ru.otus.backend.eventApi.MonitoredEvent;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BackendServiceImplTest {

    private final MonitoredEvent monitoredEvent = mock(MonitoredEvent.class);
    private final BackendService backendService = new BackendServiceImpl(monitoredEvent);
    private final Message message = mock(Message.class, withSettings().serializable());
    private final CallbackQuery callbackQuery = mock(CallbackQuery.class, withSettings().serializable());
    private final MessageModel messageModel = mock(MessageModel.class, withSettings().serializable());


    @DisplayName("корректно реагирует на сообщение с запросом")
    @Test
    void correctlyHandleNotCommandMessage() throws IOException {
        when(message.getText()).thenReturn("ABC");
        backendService.getEventData(message);
        verify(monitoredEvent, times(1)).getConcertInfo(message);
    }

    @DisplayName("корректно реагирует на сообщение типа команда")
    @Test
    void correctlyHandleCommandMessage() throws IOException {
        when(message.getText()).thenReturn("/ABC");
        backendService.getEventData(message);
        verify(monitoredEvent, times(0)).getConcertInfo(message);
    }


    @DisplayName("корректно реагирует на исключения типа IOException ")
    @Test
    void correctlyReactOnIOException() throws IOException {
        when(message.getText()).thenReturn("ABC");
        doThrow(new IOException("")).when(monitoredEvent).getConcertInfo(message);
        assertThrows(IOException.class, () -> {
            backendService.getEventData(message);
        });
    }

    @DisplayName("корректно реагирует на исключения типа RuntimeException ")
    @Test
    void correctlyReactOnRuntimeException() throws IOException {
        when(message.getText()).thenReturn("ABC");
        doThrow(new RuntimeException("")).when(monitoredEvent).getConcertInfo(message);
        Throwable thrown = assertThrows(EventException.class, () -> {
            backendService.getEventData(message);
        });
        assertThat(thrown).hasMessageContaining("Error with handling message");
    }

    @DisplayName("корректно обрабатывает сообщения типа callbackQuery")
    @Test
    void correctlyHandleCallbackQuery() throws IOException {
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("ABC");
        when(callbackQuery.getData()).thenReturn("NOTHING");
        assertThat(Serializers.deserialize(backendService.getTicketData(callbackQuery).getPayload(), String.class)).contains("Очень жаль! Обращайтесь еще");
        verify(monitoredEvent, times(0)).getTicketInfo(anyString(), anyInt());
    }

    @DisplayName("корректно обрабатывает callbackQuery типа no")
    @Test
    void correctlyHandleCallbackQueryOfNoType() throws IOException {
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("ABC");
        when(callbackQuery.getData()).thenReturn("NO");
        assertThat(Serializers.deserialize(backendService.getTicketData(callbackQuery).getPayload(), String.class)).contains("Обращайтесь еще!");
        verify(monitoredEvent, times(0)).getTicketInfo(anyString(), anyInt());
    }

    @DisplayName("корректно обрабатывает callbackQuery типа NOTIFY")
    @Test
    void correctlyHandleCallbackQueryOfNotifyType() throws IOException {
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("ABC");
        when(callbackQuery.getData()).thenReturn("NOTIFY");
        assertThat(Serializers.deserialize(backendService.getTicketData(callbackQuery).getPayload(), String.class)).contains("Хорошо! Я сообщу, если появятся билеты");
        verify(monitoredEvent, times(0)).getTicketInfo(anyString(), anyInt());
    }

    @DisplayName("корректно обрабатывает callbackQuery типа NOTIFY")
    @Test
    void correctlyHandleCallbackQueryOfNumberType() throws IOException {
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("ABC");
        when(callbackQuery.getData()).thenReturn("3");
        when(monitoredEvent.getTicketInfo(anyString(), anyInt())).thenReturn("result");
        backendService.getTicketData(callbackQuery);
        verify(monitoredEvent, times(1)).getTicketInfo(anyString(), anyInt());
    }

    @DisplayName("корректно реагирует на исключения при обработке сообщения типа CallbackQuery")
    @Test
    void correctlyReactOnExceptionWhenHandlingCallbackQuery() throws IOException {
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("ABC");
        when(callbackQuery.getData()).thenReturn("3");
        when(monitoredEvent.getTicketInfo(anyString(), anyInt())).thenReturn("result");
        doThrow(new RuntimeException()).when(monitoredEvent).getTicketInfo(anyString(), anyInt());
        Throwable thrown = assertThrows(EventException.class, () -> {
            backendService.getTicketData(callbackQuery);
        });
        assertThat(thrown).hasMessageContaining("Error with handling message");
    }

    @DisplayName("корректно формирует сообщение о внутренней ошибке пользователю ")
    @Test
    void correctlyFormErrorMessage()  {
        when(messageModel.getMessageType()).thenReturn(MessageType.GET_EVENT_INFO);
        when(messageModel.getPayload()).thenReturn(Serializers.serialize(message));
        assertThat(backendService.errorMessage(messageModel).getMessageType().getValue()).isEqualTo(MessageType.NOTIFY.getValue());
        assertThat(Serializers.deserialize(backendService.errorMessage(messageModel).getPayload(), String.class)).contains("Извините, запрос не может быть");
    }

    @DisplayName("корректно обрабатывает Exception, возникшие при формировании сообщения об ошибке")
    @Test
    void correctlyHandleExceptionWhenFormingErrorMessage()  {
        when(messageModel.getMessageType()).thenReturn(MessageType.GET_EVENT_INFO);
        when(messageModel.getPayload()).thenReturn(Serializers.serialize("message"));
        assertThrows(EventException.class, () -> { backendService.errorMessage(messageModel); });
    }

}