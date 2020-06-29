package ru.otus.backend.listeners;

import org.springframework.amqp.core.Message;

import java.io.IOException;

public interface RabbitHandler {

    void processMsgFromRabbit(Message message) throws IOException;
}
