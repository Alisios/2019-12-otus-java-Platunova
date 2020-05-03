package ru.otus.orm.jdbc.helpers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.orm.api.model.Account;
import ru.otus.orm.api.model.User;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тест должен ")
class CreateSqlStatementTest <T> {

    @DisplayName("корректно формировать sql выражения вне зависимости от запроса и входного класса")
    @Test
    void shouldCorrectlyFormSQLExpression() {
        User user1 = new User(1, "Женя", 29);
        Account account = new Account(3, "funny", new BigDecimal(29));
        CreateSqlStatement <T> createSqlStatement  = new CreateSqlStatement();
        assertEquals("insert into User (id,name,age) values (?,?,?)",(createSqlStatement.getSqlStatement((Class<T>) user1.getClass(), "insert")));
        assertEquals("update User set name = ?, age = ? where id = ?",(createSqlStatement.getSqlStatement((Class<T>) user1.getClass(), "update")));
        assertEquals("select id, name, age from User where id = ?",(createSqlStatement.getSqlStatement((Class<T>) user1.getClass(), "select")));

        assertEquals("insert into Account (no,type,rest) values (?,?,?)",(createSqlStatement.getSqlStatement((Class<T>) account.getClass(), "insert")));
        assertEquals("update Account set type = ?, rest = ? where no = ?",(createSqlStatement.getSqlStatement((Class<T>) account.getClass(), "update")));
        assertEquals("select no, type, rest from Account where no = ?",(createSqlStatement.getSqlStatement((Class<T>) account.getClass(), "select")));
    }
}