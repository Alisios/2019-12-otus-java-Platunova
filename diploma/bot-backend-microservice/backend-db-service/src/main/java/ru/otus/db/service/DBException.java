package ru.otus.db.service;

public class DBException extends RuntimeException {

    public DBException(String msg) {
        super(msg);
    }

    public DBException() {
        super();
    }

    public DBException(RuntimeException ex) {
        super(ex);
    }
}
