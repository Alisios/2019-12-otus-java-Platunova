package ru.otus.api.service;

class DbServiceException extends RuntimeException {
    DbServiceException(Exception e) {
        super(e);
    }
}
