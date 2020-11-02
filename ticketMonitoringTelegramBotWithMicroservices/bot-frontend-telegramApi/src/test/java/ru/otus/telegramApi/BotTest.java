package ru.otus.telegramApi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import static org.mockito.Mockito.*;

@DisplayName("Тест проверяет ")
class BotTest {
    private final Update update = mock(Update.class,withSettings().serializable());
    private final Message message = mock(Message.class, withSettings().serializable());
    private final CallbackQuery callbackQuery =  mock(CallbackQuery.class, withSettings().serializable());
    private final TelegramService telegramService = mock(TelegramService.class, withSettings().serializable());
    private final Bot bot = new Bot("bot", "12345",telegramService, 5);

    @Test
    @DisplayName("корректную обработку сообщения типа message и назначение GET_EVENT_INFO")
    void correctlyHandleMessageAndSetUpRightMessageType()  {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        bot.onUpdateReceived(update);
        verify(telegramService, times(0)).sendMessageToRabbit(new MessageModel(MessageType.GET_TICKET_INFO, Serializers.serialize(message)));
        verify(telegramService, times(1)).sendMessageToRabbit(new MessageModel(MessageType.GET_EVENT_INFO, Serializers.serialize(message)));

    }

    @Test
    @DisplayName("корректную обработку условий if")
    void correctlyHandleAllConditions1()  {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.hasText()).thenReturn(false);
        bot.onUpdateReceived(update);
        verify(telegramService, times(0)).sendMessageToRabbit(any());
    }

    @Test
    @DisplayName("корректную обработку условий if")
    void correctlyHandleAllConditions2()  {
        bot.onUpdateReceived(update);
        verify(telegramService, times(0)).sendMessageToRabbit(any());
    }

    @Test
    @DisplayName("корректную обработку сообщения типа CallbackQuery и назначение GET_TICKET_INFO")
    void correctlyHandleCallbackQueryAndSetUpRightMessageType()  {
        when(update.hasMessage()).thenReturn(false);
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        bot.onUpdateReceived(update);
        verify(telegramService, times(1)).sendMessageToRabbit(new MessageModel(MessageType.GET_TICKET_INFO, Serializers.serialize(callbackQuery)));
        verify(telegramService, times(0)).sendMessageToRabbit(new MessageModel(MessageType.GET_EVENT_INFO, Serializers.serialize(callbackQuery)));

    }

}