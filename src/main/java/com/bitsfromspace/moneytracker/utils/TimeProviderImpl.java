package com.bitsfromspace.moneytracker.utils;

import com.google.appengine.api.search.DateUtil;

import javax.inject.Singleton;

/**
 * @author chris
 * @since 29-10-15.
 */
@Singleton
public class TimeProviderImpl implements TimeProvider{

    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }

    @Override
    public int getDay() {
        return DayUtils.getDay(getTime());
    }
}
