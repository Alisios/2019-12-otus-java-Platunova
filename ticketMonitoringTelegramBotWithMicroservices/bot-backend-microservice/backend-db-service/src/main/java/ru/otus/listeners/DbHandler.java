package ru.otus.listeners;

import org.springframework.amqp.core.Message;

import java.io.IOException;

public interface DbHandler {

    void processMsgFromRabbit(Message message) throws IOException;
}
