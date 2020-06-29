package ru.otus.listeners;

import org.springframework.amqp.core.Message;

import java.io.IOException;

public interface RabbitHandler {

    void processMsgFromRabbit(Message message) throws IOException;
}
