package ru.otus.orm.jdbc.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.orm.api.model.Account;
import ru.otus.orm.api.model.User;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тест должен ")
class JdbcMapperTest {
    private User user1;
    private User user2;
    private Account account2;
    private Account account;
    private JdbcMapper jdbcMapper = new JdbcMapper();

    @BeforeEach
    void setUp() {
        user1 = new User(1, "Женя", 29);
        account = new Account(3, "funny", new BigDecimal(41));
        user2= new User(3, "Боб", 29);
        account2 = new Account(5, "sad", new BigDecimal(29));
    }

    @Test
    @DisplayName("корректно формировать значения всех полей вне зависимости от входного класса")
    void correctlyFormValuesOfClassParameters() {
        assertLinesMatch(List.of("1", "Женя", "29"), jdbcMapper.getParams(user1));
        assertLinesMatch(List.of("3", "Боб", "29"), jdbcMapper.getParams(user2));
        assertLinesMatch(List.of("3", "funny", "41"), jdbcMapper.getParams(account));
        assertLinesMatch(List.of("5","sad", "29"), jdbcMapper.getParams(account2));
    }

    @Test //не знаю как проверять
    void createObjectFromResultSet() {
    }

    @DisplayName("находить поле отмеченное аннотацией id  вне зависимости от входного класса")
    @Test
    void correctlyGetIdField() {
        assertEquals(1L,jdbcMapper.getId(user1).get());
        assertEquals(3L,jdbcMapper.getId(account).get());
        assertEquals(3L,jdbcMapper.getId(user2).get());
        assertEquals(5L,jdbcMapper.getId(account2).get());
        user1.setId(7);
        assertEquals(7L,jdbcMapper.getId(user1).get());
    }
}