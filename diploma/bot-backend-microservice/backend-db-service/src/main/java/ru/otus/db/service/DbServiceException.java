package ru.otus.db.service;

class DbServiceException extends RuntimeException {
  DbServiceException(Exception e) {
    super(e);
  }
}
