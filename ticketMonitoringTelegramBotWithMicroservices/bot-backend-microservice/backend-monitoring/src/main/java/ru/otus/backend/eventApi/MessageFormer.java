package ru.otus.backend.eventApi;

import lombok.NoArgsConstructor;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.util.Optional;

@NoArgsConstructor
public final class MessageFormer {

    public static Optional<MessageForFront> formMessageAboutMonitoringResults(User user) {
        String payload = "";
        MessageForFront messageForFront = new MessageForFront(MessageType.NOTIFY, Serializers.serialize(payload), user.getChatId(), -1);
        if (user.getIsMonitoringSuccessful())
            payload = "Запрашиваемые билеты на " + user.getConcert().getArtist() + " появились! Информация о событии:\n" +
                    user.getMessageText();
        else
            payload = "Время события или максимальное время ожидания появления билетов истекло. " +
                    "Запрашиваемые билеты на " + user.getConcert().getArtist() + " не появились :(";
        messageForFront.setPayload(Serializers.serialize(payload));
        return Optional.of(messageForFront);
    }
}