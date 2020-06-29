package ru.otus.backend.eventApi;


public class EventException extends RuntimeException {

    public EventException(String msg) {
        super(msg);
    }

    public EventException(RuntimeException ex) {
        super(ex);
    }
}
