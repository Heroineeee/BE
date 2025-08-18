package com.kkinikong.be.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

  public static String relativeTimeFormatter(LocalDateTime past) {
    LocalDateTime now = LocalDateTime.now();

    Duration duration = Duration.between(past, now);
    long minutes = duration.toMinutes();
    long hours = duration.toHours();

    if (minutes < 1) {
      return "방금";
    } else if (minutes < 60) {
      return minutes + "분 전";
    } else if (hours < 24) {
      return hours + "시간 전";
    }

    long days = duration.toDays();

    if (days < 7) {
      return days + "일 전";
    } else if (days < 30) {
      long weeks = days / 7;
      return weeks + "주 전";
    } else if (days < 365) {
      long months = ChronoUnit.MONTHS.between(past.toLocalDate(), now.toLocalDate());
      return months + "개월 전";
    } else {
      long years = ChronoUnit.YEARS.between(past.toLocalDate(), now.toLocalDate());
      return years + "년 전";
    }
  }
}
