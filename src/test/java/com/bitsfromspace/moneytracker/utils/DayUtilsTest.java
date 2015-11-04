package com.bitsfromspace.moneytracker.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.lt;

/**
 * @author chris
 * @since 03-11-15.
 */
public class DayUtilsTest {

    @Test
    public void test(){
        long now = System.currentTimeMillis();
        int day = DayUtils.getDay(now);
        Date nowDate = DayUtils.getDate(day);
        long delta = Math.abs(now - nowDate.getTime());
        assertTrue("Today is " + nowDate + ". Asserting that "  + delta + " < 24 HRS",
                Math.abs(now - nowDate.getTime()) < 24 * 60 * 60 * 1000);

    }

}