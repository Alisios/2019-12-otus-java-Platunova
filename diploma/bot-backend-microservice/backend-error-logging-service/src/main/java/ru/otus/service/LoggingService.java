package ru.otus.service;

import ru.otus.backend.model.Log;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageModel;

public interface LoggingService {

    Log loggingMessageForFront(MessageForFront message);

    Log loggingMessageModel (MessageModel message);
}
