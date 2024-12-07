package com.yoongu.security.apiserver.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static boolean isMinuteBefore(LocalDateTime targetLocalDateTime, long minute) {
        return targetLocalDateTime.isBefore(LocalDateTime.now().minusMinutes(minute));
    }

    public static LocalDateTime convertLongMillsToLocalDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String convertLongMillsToLocalDateTimeString(long millis, DateTimeFormatter dateTimeFormatter) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime().format(dateTimeFormatter);
    }

    public static LocalDateTime parseStringToLocalDateTime(String stringDate, boolean isStart) {
        LocalDate date = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalTime time = isStart ? LocalTime.of(0, 0, 0) : LocalTime.of(23, 59, 59);
        return LocalDateTime.of(date, time);
    }
}
