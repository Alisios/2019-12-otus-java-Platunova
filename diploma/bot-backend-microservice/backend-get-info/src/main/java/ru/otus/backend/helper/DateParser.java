package ru.otus.backend.helper;

import java.util.*;

public class DateParser {

     public Calendar parse(String date) {
        List<String> dateList = Arrays.asList(date.replaceAll(":", " ").split(" "));
        Calendar gcalendar = new GregorianCalendar();
        List<String>  months=  Arrays.asList("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен",
                "Окт", "Ноя", "Дек");
        int index = 0;
        for (String s: months)
            if (dateList.get(1).contains(s)) {
                index = months.indexOf(s);
            }
        gcalendar.set(Calendar.YEAR, gcalendar.get(Calendar.YEAR));
        gcalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateList.get(0)));
        gcalendar.set(Calendar.MONTH,  index);
        if (!date.contains("—")) {
            gcalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateList.get(2)));
            gcalendar.set(Calendar.MINUTE, Integer.parseInt(dateList.get(3)));
            gcalendar.set(Calendar.SECOND, 0);
        }
        return gcalendar;
    }

}
