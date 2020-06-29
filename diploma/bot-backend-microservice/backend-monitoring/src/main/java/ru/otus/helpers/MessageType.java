package ru.otus.helpers;

import java.io.Serializable;

public enum MessageType implements Serializable {
    DELETE_USER("deleteUser"),
    DELETE_USER_BY_ADMIN("deleteUserByAdmin"),
    SAVE_USER("saveUser"),
    SAVE_USER_BY_ADMIN("saveUserByAdmin"),
    GET_USERS("getAllUsers"),
    NOTIFY("notify"),
    GET_MONITORING_RESULT("monitoring"),
    GET_TICKET_INFO("get_ticket_info"),
    TEST("test"),
    SHUTDOWN_MESSAGE("shutdown"),
    ADMIN_GET_USERS("admin_get_users"),
    GET_EVENT_INFO("get_event_info");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
