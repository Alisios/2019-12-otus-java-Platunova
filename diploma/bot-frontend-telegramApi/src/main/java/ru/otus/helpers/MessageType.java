package ru.otus.helpers;

import java.io.Serializable;

public enum MessageType implements Serializable {
    DELETE_USER("deleteUser"),
    SAVE_USER("saveUser"),
    GET_USERS("getAllUsers"),
    NOTIFY("notify"),
    GET_TICKET_INFO("get_ticket_info"),
    TEST("test"),
    SHUTDOWN_MESSAGE("shutdown"),
    GET_EVENT_INFO("get_event_info");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
