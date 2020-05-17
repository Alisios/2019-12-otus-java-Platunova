
package ru.otus.helpers;
import java.io.Serializable;
import java.util.Objects;

public class MessageForFront extends MessageModel implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;

    private long chatId;
    private int messageId;
    private int numberOfEvents = 0;
    private String callbackType = CallbackType.HELLO.getValue();

    public MessageForFront(MessageType messageType, byte[] payload,long chatId, int messageId) {
        super(messageType, payload);
        this.chatId = chatId;
        this.messageId = messageId;
    }

    public long getChatId() {
        return chatId;
    }

    public int getNumberOfEvents() {
        return numberOfEvents;
    }

    public String getCallbackType() {
        return callbackType;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setNumberOfEvents(int numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    public void setCallbackType(String callbackType) {
        this.callbackType = callbackType;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MessageForFront that = (MessageForFront) o;
        return chatId == that.chatId &&
                messageId == that.messageId &&
                numberOfEvents == that.numberOfEvents &&
                callbackType.equals(that.callbackType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatId, messageId, numberOfEvents, callbackType);
    }

    @Override
    public String toString() {
        return "MessageForFront{" +
                "chatId=" + chatId +
                ", messageId=" + messageId +
                ", numberOfEvents=" + numberOfEvents +
                ", callbackType=" + callbackType +
                ", message Type=" + super.getMessageType() +
                // ", message payload=" + Arrays.toString(super.getPayload()) +
                '}';
    }
}
