package com.kakaoscan.profile.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static String getBeforeDiffToString(LocalDateTime before, LocalDateTime after) {
        long m = ChronoUnit.MINUTES.between(before, after);
        long h = ChronoUnit.HOURS.between(before, after);
        long d = ChronoUnit.DAYS.between(before, after);
        long mth = ChronoUnit.MONTHS.between(before, after);
        long y = ChronoUnit.YEARS.between(before, after);

        if (m == 0) {
            return "방금";
        }
        if (m < 60) {
            return String.format("%d분", m);
        }
        if (h > 0 && h < 24) {
            return String.format("%d시간", h);
        }
        if (d > 0 && mth == 0) {
            return String.format("%d일", d);
        }
        if (mth > 0 && y == 0) {
            return String.format("%d달", mth);
        }

        return String.format("%d년", y);
    }
}
