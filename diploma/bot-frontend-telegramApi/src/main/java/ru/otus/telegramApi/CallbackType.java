package ru.otus.telegramApi;

import java.io.Serializable;

public enum CallbackType implements Serializable {
    IF_SHOULD_BE_MONITORED("IF_SHOULD_BE_MONITORED"),
    LIST_OF_EVENTS("LIST_OF_EVENTS"),
    NOTHING("NOTHING"),
    NOTIFY("NOTIFY"),
    HELLO("HELLO"),
    NO("NO");

    private final String value;

    CallbackType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
