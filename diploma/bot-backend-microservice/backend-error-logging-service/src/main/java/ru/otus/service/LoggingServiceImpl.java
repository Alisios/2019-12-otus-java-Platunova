package ru.otus.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.model.Log;
import ru.otus.backend.model.User;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.util.List;
import java.util.Objects;

/**
 * основной сервис бизнес логики: формирование лога, его сущности и запись в БД
 */

@Service
public class LoggingServiceImpl implements LoggingService {

    @Override
    public Log loggingMessageForFront(MessageForFront mmsFrom) {
        Log log = new Log();
        log.setTypeOfError(mmsFrom.getMessageType().getValue());
        log.setChatId(mmsFrom.getChatId());
        StringBuilder res = new StringBuilder();
        if (mmsFrom.getMessageType().getValue().equals(MessageType.ADMIN_GET_USERS.getValue())) {
            res.append("Fail to get list of users by admin: ")
                    .append(Serializers.deserialize(mmsFrom.getPayload(), List.class).toString())
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
        } else if (mmsFrom.getMessageType().getValue().equals(MessageType.DELETE_USER_BY_ADMIN.getValue())) {
            res.append("Fail to delete user with id ")
                    .append(Serializers.deserialize(mmsFrom.getPayload(), long.class))
                    .append("by admin")
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
        } else if (mmsFrom.getMessageType().getValue().equals(MessageType.SAVE_USER_BY_ADMIN.getValue())) {
            res.append("Fail to save user ")
                    .append(Serializers.deserialize(mmsFrom.getPayload(), User.class))
                    .append("by admin")
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
        } else {
            res.append("Fail to send message<")
                    .append(Serializers.deserialize(mmsFrom.getPayload(), String.class))
                    .append("> to the user with chatId ")
                    .append(mmsFrom.getChatId())
                    .append(" and messageId ")
                    .append(mmsFrom.getMessageId())
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
        }
        log.setErrorMessage(res.toString());
        return log;
    }

    @Override
    public Log loggingMessageModel(MessageModel mmsFrom) {
        Message message = null;
        Log log = new Log();
        log.setTypeOfError(mmsFrom.getMessageType().getValue());
        StringBuilder res = new StringBuilder();
        if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_EVENT_INFO.getValue())) {
            message = Serializers.deserialize(mmsFrom.getPayload(), Message.class);
            res.append("Message <")
                    .append(Objects.requireNonNull(message).getText())
                    .append("> has failed to be handled and the answer for user with messageId ")
                    .append(message.getChatId()).append("  is not sent. The id of initial message of user: ")
                    .append(message.getMessageId())
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
            log.setChatId(message.getChatId());
        } else if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_TICKET_INFO.getValue())) {
            CallbackQuery callbackQuery = Serializers.deserialize(mmsFrom.getPayload(), CallbackQuery.class);
            message = callbackQuery.getMessage();
            res.append("Message <")
                    .append(Objects.requireNonNull(message).getText())
                    .append("> has failed to be handled and the answer for user with messageId ")
                    .append(message.getChatId()).append("  is not sent. The id of initial message of user: ")
                    .append(message.getMessageId())
                    .append(". CallbackData: ")
                    .append(callbackQuery.getData())
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
            log.setChatId(message.getChatId());
        } else if (mmsFrom.getMessageType().getValue().equals(MessageType.DELETE_USER.getValue()) ||
                (mmsFrom.getMessageType().getValue().equals(MessageType.SAVE_USER.getValue()))) {
            res.append("Fail to delete/save user:  ")
                    .append(Serializers.deserialize(mmsFrom.getPayload(), User.class))
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
            log.setChatId(Serializers.deserialize(mmsFrom.getPayload(), User.class).getChatId());
        } else if (mmsFrom.getMessageType().getValue().equals(MessageType.GET_MONITORING_RESULT.getValue())) {
            res.append("Fail to get list of users for getting of monitoring result: ")
                    .append(Serializers.deserialize(mmsFrom.getPayload(), List.class).toString())
                    .append(". MessageType: ")
                    .append(mmsFrom.getMessageType())
                    .append(".\n");
        }
        log.setErrorMessage(res.toString());
        return log;
    }
}
