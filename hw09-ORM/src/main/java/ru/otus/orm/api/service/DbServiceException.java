package ru.otus.orm.api.service;

class DbServiceException extends RuntimeException {
    DbServiceException(Exception e) {
        super(e);
    }
}
