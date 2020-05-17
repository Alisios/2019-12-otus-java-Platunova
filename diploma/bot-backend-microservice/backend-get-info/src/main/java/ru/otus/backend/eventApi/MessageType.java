package ru.otus.backend.eventApi;

public enum MessageType {
    QUERY_HANDLER_ACTIVATE("queryActivate"),
    HELLO("hello"),
    IS_NOTIFY("shouldBeNotify"),
    NOTIFY_EVENT("notifyEvent");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}