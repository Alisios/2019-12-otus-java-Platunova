package ru.otus.backend.eventApi.parsers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

import ru.otus.backend.eventApi.helper.DateParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тест проверяет: ")
public class DateParserTest {

    private final DateParser dateParser = new DateParser();

    @DisplayName("корректный перевод даты из события в Date()")
    @ParameterizedTest
    @MethodSource("generateData")
    void checkDate(String strings, GregorianCalendar date) {
        Date myDate = dateParser.parse(strings).getTime();
        date.set(Calendar.HOUR_OF_DAY, 19);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        System.out.println(myDate);
        System.out.println(date.getTime());
        assertEquals(myDate.getDate(), date.getTime().getDate());
        assertEquals(myDate.getHours(), date.getTime().getHours());
        assertEquals(myDate.getMonth(), date.getTime().getMonth());
        assertEquals(myDate.getYear(), date.getTime().getYear());
    }

    private static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("11 Июль 2021вс 19:00", new GregorianCalendar(2021, 6, 11)),
                Arguments.of("20 Июньсб 19:00", new GregorianCalendar(2020, 5, 20)),
                Arguments.of("24 Майвс 19:00", new GregorianCalendar(2020, 4, 24)),
                Arguments.of("12 Сентвс 19:00", new GregorianCalendar(2020, 8, 12)),
                Arguments.of("1 Декчт 19:00", new GregorianCalendar(2020, 11, 1)),
                Arguments.of("1 Янвчт 19:00", new GregorianCalendar(2020, 0, 1)));
    }
}

