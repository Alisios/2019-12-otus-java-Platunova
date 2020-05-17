package ru.otus.backend.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест проверяет: ")
class DateParserTest {

    private DateParser dateParser = new DateParser();


    @DisplayName("корректный перевод даты из события в Date()")
    @ParameterizedTest
    @MethodSource("generateData")
    void checkDate(String strings, GregorianCalendar date)  {
        Date myDate = dateParser.parse(strings).getTime();
     //   GregorianCalendar c = (GregorianCalendar) dateParser.parse(strings);
        date.set(Calendar.HOUR_OF_DAY, 19);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        System.out.println(myDate);
        System.out.println(date.getTime());
     //   assertEquals( myDate.getTime(),date.getTimeInMillis());
        assertEquals(myDate.getDate(), date.getTime().getDate());
        assertEquals(myDate.getHours(), date.getTime().getHours());
        assertEquals(myDate.getMonth(), date.getTime().getMonth());
        assertEquals(myDate.getYear(), date.getTime().getYear());
       // c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-1);
        //System.out.println(c.getTime());
        //System.out.println(new GregorianCalendar().getTime());
    }
    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("20 Июньсб 19:00", new GregorianCalendar(2020, 5,20)),
                Arguments.of("24 Майвс 19:00", new GregorianCalendar(2020, 4,24)),
                Arguments.of("12 Сентвс 19:00", new GregorianCalendar(2020, 8,12)),
                Arguments.of("1 Декчт 19:00", new GregorianCalendar(2020, 11,1)),
                Arguments.of("1 Янвчт 19:00", new GregorianCalendar(2020, 0,1)));
             //   Arguments.of("12 Июнь—28 Авг.",new GregorianCalendar(2020, 5,12)));


    }
}