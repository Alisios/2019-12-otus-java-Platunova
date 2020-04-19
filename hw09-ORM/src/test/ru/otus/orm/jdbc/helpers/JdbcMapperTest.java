package ru.otus.orm.jdbc.helpers;

import com.google.gson.JsonPrimitive;
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
        assertLinesMatch(List.of("1", String.valueOf(new JsonPrimitive((String)"Женя")), "29"), jdbcMapper.getParams(user1));
        assertLinesMatch(List.of("3", String.valueOf(new JsonPrimitive((String)"Боб")), "29"), jdbcMapper.getParams(user2));
        assertLinesMatch(List.of("3", String.valueOf(new JsonPrimitive((String)"funny")), "41"), jdbcMapper.getParams(account));
        assertLinesMatch(List.of("5", String.valueOf(new JsonPrimitive((String)"sad")), "29"), jdbcMapper.getParams(account2));
    }

    @Test
    void createObjectFromResultSet() {
    }

    @DisplayName("находить поле отмеченное аннотацией id  вне зависимости от входного класса")
    @Test
    void correctlyGetIdField() {
        assertEquals(1,jdbcMapper.getId(user1));
        assertEquals(3,jdbcMapper.getId(account));
        assertEquals(3,jdbcMapper.getId(user2));
        assertEquals(5,jdbcMapper.getId(account2));
        user1.setId(7);
        assertEquals(7,jdbcMapper.getId(user1));
    }
}