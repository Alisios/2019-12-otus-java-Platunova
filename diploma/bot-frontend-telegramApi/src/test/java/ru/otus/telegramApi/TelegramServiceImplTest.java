package ru.otus.telegramApi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.otus.configurations.RabbitMQProperties;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Тесты проверяют вызов функции execute при различных типах сообщения от бэка")
class TelegramServiceImplTest {

    private final Message message = mock(Message.class);
    private MessageForFront mms;
    private final Bot bot = mock(Bot.class);
    private final AmqpTemplate template = mock(AmqpTemplate.class);
    private final RabbitMQProperties rabbitProperties = mock(RabbitMQProperties.class);
    private final TelegramService telegramService = new TelegramServiceImpl(template, rabbitProperties);


    @BeforeEach
    void setup() {
        telegramService.setBot(bot);
        mms = new MessageForFront(MessageType.GET_EVENT_INFO, Serializers.serialize("null"), 1L, 1);
        when(message.getBody()).thenReturn(Serializers.serialize(mms));
    }

    @Test
    void invokesExecuteInSendMsg() throws TelegramApiException {
        assertDoesNotThrow(() -> telegramService.sendMsg(mms));
        verify(bot, times(1)).execute(any());
    }

    @Test
    void invokesExecuteInSendNotifyingMsg() throws TelegramApiException {
        assertDoesNotThrow(() -> telegramService.sendNotifyingMsg(mms));
        verify(bot, times(1)).execute(any());
    }

    @Test
    void invokesExecuteInSendMsgQuery() throws TelegramApiException {
        assertDoesNotThrow(() -> telegramService.sendMsgQuery(mms));
        verify(bot, times(1)).execute(any());
    }

    @Test
    void correctlyHandleException() throws TelegramApiException {
        doThrow(new TelegramApiException()).when(bot).execute(any());
        Throwable thrown = assertThrows(TelegramApiException.class, () -> {
            telegramService.sendMsgQuery(mms);
        });
        assertThat(thrown).hasMessageContaining("is not sent to user");
    }

}