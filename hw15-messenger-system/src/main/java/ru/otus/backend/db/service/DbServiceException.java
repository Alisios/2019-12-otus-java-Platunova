package ru.otus.backend.db.service;

class DbServiceException extends RuntimeException {
    DbServiceException(Exception e) {
        super(e);
    }
}
