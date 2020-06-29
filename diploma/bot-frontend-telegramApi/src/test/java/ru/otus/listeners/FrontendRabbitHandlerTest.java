package ru.otus.listeners;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;
import ru.otus.telegramApi.TelegramService;

import static org.mockito.Mockito.*;

@DisplayName("Тест проверяет ")
class FrontendRabbitHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);
    private final FrontendRabbitHandler frontendRabbitHandlerTest = new FrontendRabbitHandler(telegramService);
    private final Message message = mock(Message.class,withSettings().serializable());
    private MessageForFront mms;

    @Test
    @DisplayName("корректный вызов требуемой функции при типе сообщения GET_EVENT_INFO")
    void correctFunctionCallWhenTypeOfMessageIsGET_EVENT_INFO() throws TelegramApiException {
        mms = new MessageForFront(MessageType.GET_EVENT_INFO, Serializers.serialize("null"), 1L,1);
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        frontendRabbitHandlerTest.processMsgFromRabbit(message);
        verify(telegramService, times(1)).sendMsg(mms);
    }
    @Test
    @DisplayName("корректный вызов требуемой функции при типе сообщения GET_TICKET_INFO")
    void correctFunctionCallWhenTypeOfMessageIsGET_TICKET_INFO() throws TelegramApiException {
        mms = new MessageForFront(MessageType.GET_TICKET_INFO, Serializers.serialize("null"), 1L,1);
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        frontendRabbitHandlerTest.processMsgFromRabbit(message);
        verify(telegramService, times(1)).sendMsgQuery(mms);
    }
    @Test
    @DisplayName("корректный вызов требуемой функции при типе сообщения NOTIFY")
    void correctFunctionCallWhenTypeOfMessageIsNOTIFY() throws TelegramApiException {
        mms = new MessageForFront(MessageType.NOTIFY, Serializers.serialize("null"), 1L,1);
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
        frontendRabbitHandlerTest.processMsgFromRabbit(message);
        verify(telegramService, times(1)).sendNotifyingMsg(mms);
    }
}