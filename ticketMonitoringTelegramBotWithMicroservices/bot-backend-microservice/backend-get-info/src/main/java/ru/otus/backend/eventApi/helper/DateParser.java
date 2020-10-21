package ru.otus.backend.eventApi.helper;

import java.util.*;

public final class DateParser {

    private final List<String> months = Arrays.asList("янв", "фев", "мар", "фпр", "май", "июн", "июл", "фвг", "сен",
            "окт", "ноя", "дек");
    private final List<String> days = Arrays.asList("пн", "вт", "ср", "чт", "пт", "сб", "вс");

    public Calendar parse(String date) {
        List<String> dateList = Arrays.asList(date.replaceAll(":", " ").split(" "));
        if (dateList.size() == 4)
            return parseForThisYear(date, dateList);
        else
            return parseForNextYear(date, dateList);
    }

    private Calendar parseForNextYear(String date, List<String> dateList) {
        var gcalendar = new GregorianCalendar();
        int index = 0;
        for (String s : months)
            if (dateList.get(1).toLowerCase().contains(s)) {
                index = months.indexOf(s);
            }
        String temp = "";
        for (String s : days)
            if (dateList.get(2).contains(s)) {
                temp = dateList.get(2).replaceAll(s, "");
            }
        if (temp.isBlank())
            gcalendar.set(Calendar.YEAR, gcalendar.get(Calendar.YEAR));
        else
            gcalendar.set(Calendar.YEAR, Integer.parseInt(temp));
        gcalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateList.get(0)));
        gcalendar.set(Calendar.MONTH, index);
        if (!date.contains("—")) {
            gcalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateList.get(3)));
            gcalendar.set(Calendar.MINUTE, Integer.parseInt(dateList.get(4)));
            gcalendar.set(Calendar.SECOND, 0);
        }
        return gcalendar;
    }

    private Calendar parseForThisYear(String date, List<String> dateList) {
        var gcalendar = new GregorianCalendar();
        List<String> months = Arrays.asList("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен",
                "Окт", "Ноя", "Дек");
        int index = 0;
        for (String s : months)
            if (dateList.get(1).contains(s)) {
                index = months.indexOf(s);
            }
        gcalendar.set(Calendar.YEAR, gcalendar.get(Calendar.YEAR));
        gcalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateList.get(0)));
        gcalendar.set(Calendar.MONTH, index);
        if (!date.contains("—")) {
            gcalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateList.get(2)));
            gcalendar.set(Calendar.MINUTE, Integer.parseInt(dateList.get(3)));
            gcalendar.set(Calendar.SECOND, 0);
        }
        return gcalendar;
    }
}
