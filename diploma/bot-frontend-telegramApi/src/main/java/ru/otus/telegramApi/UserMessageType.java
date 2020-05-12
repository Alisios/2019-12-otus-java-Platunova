package ru.otus.telegramApi;

public enum UserMessageType {
    START_COM("start_command"),
    HELP_COM("help_command"),
    UNKNOWN_COM("unknown_command"),
    GET_INFO_UPDATE("get_info_update"),
    GET_INFO_CALLBACK_QUERY("get_info_callback"),
    NOTHING_CALLBACK_QUERY("no_info_callback")
    ;
    private final String value;
    UserMessageType(String value) {
        this.value = value;
    }
    public String getValue(String value) {
        return value;
    }

}
