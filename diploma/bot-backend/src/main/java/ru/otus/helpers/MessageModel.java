package ru.otus.helpers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class MessageModel implements Serializable {

    private MessageType messageType ;

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    private  byte[] payload;

    public MessageModel(MessageType messageType, byte[] payload) {
        this.messageType = messageType;
        this.payload = payload;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageModel that = (MessageModel) o;
        return messageType == that.messageType &&
                Arrays.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(messageType);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }

    @Override
    public String toString() {
        return "MessageModelRabbitMq{" +
                "messageType=" + messageType +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}
