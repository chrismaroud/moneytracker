package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.model.Currency;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author chris
 * @since 27-10-15.
 */
@Singleton
public class CurrencyService {

    public static final int DEFAULT_CACHE_TIMEOUT_MILLIS = 6 * 60 * 60 * 1000; // 6 hours

    private final TimeProvider timeProvider;
    private final CurrencyRateProvider currencyRateProvider;
    private final TObjectLongMap<String> currenyPairToRetrievalDate;
    private final TObjectDoubleMap<String> currencyPairCache;
    private final long cacheTimeoutMillis;

    @Inject
    public CurrencyService(CurrencyRateProvider currencyRateProvider, TimeProvider timeProvider) {
        this(currencyRateProvider, timeProvider, DEFAULT_CACHE_TIMEOUT_MILLIS);
    }

    public CurrencyService(CurrencyRateProvider currencyRateProvider, TimeProvider timeProvider, long cacheTimeoutMillis) {
        this.currencyRateProvider = currencyRateProvider;
        this.timeProvider = timeProvider;
        this.cacheTimeoutMillis = cacheTimeoutMillis;
        currenyPairToRetrievalDate = new TObjectLongHashMap<>();
        currencyPairCache = new TObjectDoubleHashMap<>();
    }

    public double getConversionRate(Currency from, Currency to) {
        if (from == to){
            return 1;
        }
        String currencyPair = from.name() + to.name();
        if (currenyPairToRetrievalDate.containsKey(currencyPair)
                && timeProvider.getTime() - currenyPairToRetrievalDate.get(currencyPair) < cacheTimeoutMillis) {
            synchronized (currencyPairCache) {
                return currencyPairCache.get(currencyPair);
            }
        }

        double rate = currencyRateProvider.getConversionRate(from, to);
        synchronized (currencyPairCache) {
            currencyPairCache.put(currencyPair, rate);
            currenyPairToRetrievalDate.put(currencyPair, timeProvider.getTime());
        }
        return rate;
    }
}
