package ru.otus.service;

public class DbException extends RuntimeException {

    public DbException(String msg) {
        super(msg);
    }

    public DbException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DbException(Throwable cause) {
        super(cause);
    }
}
