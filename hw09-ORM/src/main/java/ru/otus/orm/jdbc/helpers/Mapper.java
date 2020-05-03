package ru.otus.orm.jdbc.helpers;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface Mapper <T> {
    List<String> getParams(T obj);
    T createObjectFromResultSet (ResultSet resultSet, Class <T> clazz);
    Optional<Long> getId(T obj);
}
