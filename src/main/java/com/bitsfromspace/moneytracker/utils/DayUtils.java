package com.bitsfromspace.moneytracker.utils;

import com.bitsfromspace.moneytracker.services.CurrencyRateProvider;

import java.util.Calendar;
import java.util.Date;

/**
 * @author chris
 * @since 29-10-15.
 */
public class DayUtils {

    private static final int DAY_MS = 24 * 60 * 60 * 1000;

    private DayUtils() {
    }

    public static int getDay(long timeMillis) {
        return (int) (timeMillis / DAY_MS);
    }

    public static Date getDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(day * DAY_MS);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
