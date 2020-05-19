package ru.otus.messagesystem;

public enum MessageType {
    GET_USERS("GET_USERS"),
    SAVE_USER("SAVE_USER"),
    USER_DATA("UserData");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
