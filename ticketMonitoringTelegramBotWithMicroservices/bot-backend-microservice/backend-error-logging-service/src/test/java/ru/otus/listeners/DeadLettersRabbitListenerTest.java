package ru.otus.listeners;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;
import ru.otus.service.LoggingService;
import ru.otus.service.LoggingServiceImpl;

import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("Тест проверяет ")
class DeadLettersRabbitListenerTest {

    private final MessageForFront messageForFront = mock(MessageForFront.class, withSettings().serializable());
    private LoggingService loggingService = new LoggingServiceImpl();

    private final User user = new User(1L, 202812830, new ConcertModel("Aerosmith (Аэросмит)",
            "30 Июльчт 19:00",
            "ВТБ Арена – Центральный стадион «Динамо» имени Льва Яшина",
            "https://msk.kassir.ru/koncert/vtb-arena-tsentralnyiy-stadion-dinamo/aerosmith-aerosmit_2020-07-30"),
            new GregorianCalendar(2020, 4, 23).getTime());


    @Test
    @DisplayName("корректно формирует лог из сообщения об ошибке")
    void correctlyFormLogFromErrorMessage() {
        when(messageForFront.getMessageType()).thenReturn(MessageType.SAVE_USER_BY_ADMIN);
        when(messageForFront.getChatId()).thenReturn(1L);
        when(messageForFront.getPayload()).thenReturn(Serializers.serialize(user));
        var res = loggingService.loggingMessageForFront(messageForFront);
        assertAll("res", () -> {
            assertThat(res.getChatId()).isEqualTo(1L);
            assertThat(res.getTypeOfError()).isEqualTo(MessageType.SAVE_USER_BY_ADMIN.getValue());
            assertThat(res.getErrorMessage()).contains(user.getConcert().getArtist());
            assertThat(res.getErrorMessage()).contains("Fail to save user");
        });
    }

}