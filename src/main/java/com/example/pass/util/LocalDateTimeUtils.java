package com.example.pass.util;

import com.vladmihalcea.hibernate.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class LocalDateTimeUtils {
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(final LocalDateTime localDateTime) {
        return localDateTime.format(YYYY_MM_DD_HH_MM);
    }

    public static String format(final LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    public static LocalDateTime parse(final String localDateTimeString) {
        if (StringUtils.isBlank(localDateTimeString)) {
            return null;
        }

        return LocalDateTime.parse(localDateTimeString, YYYY_MM_DD_HH_MM);
    }

    public static LocalDateTime getMidnightDateTime(LocalDateTime target) {
        return target.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static int getWeekOfYear(LocalDateTime target) {
        return target.get(WeekFields.of(Locale.KOREA).weekOfYear());
    }
}
