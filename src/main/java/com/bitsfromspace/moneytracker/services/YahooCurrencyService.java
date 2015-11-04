package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.MoneyTrackerException;
import com.bitsfromspace.moneytracker.model.Currency;
import com.bitsfromspace.moneytracker.utils.Cache;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import com.eclipsesource.json.Json;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.bitsfromspace.moneytracker.utils.ExceptionUtils.runUnchecked;
import static com.bitsfromspace.moneytracker.utils.IoUtils.readFully;

/**
 * @author chris
 * @since 02-11-15.
 */
@Singleton
public class YahooCurrencyService implements CurrencyService {

    private static final String CURRENCY_PAIR_PARAMETER = "$CURRENCYPAIR";
    private static final String YAHOO_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20%3D%20%22" + CURRENCY_PAIR_PARAMETER + "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    private final Cache<String, Double> currencyCache;

    @Inject
    public YahooCurrencyService(TimeProvider timeProvider, @Named("YahooCurrencyService.cacheRententionHours")int cacheRetentionHours) {
        currencyCache = new Cache<>(timeProvider, TimeUnit.HOURS.toMillis(cacheRetentionHours), new Cache.Delegate<String, Double>() {
            @Override
            public Double get(final String currencyPair) {
                return runUnchecked(new Callable<Double>(){

                    @Override
                    public Double call() throws IOException{

                        HttpURLConnection httpConnection = (HttpURLConnection) new URL(YAHOO_URL.replace(CURRENCY_PAIR_PARAMETER, currencyPair)).openConnection();


                        if (httpConnection.getResponseCode() < 200 || httpConnection.getResponseCode() > 200){
                            throw new MoneyTrackerException("Error retrieving currency pair 'currencyPair': "
                                    + httpConnection.getResponseCode() + " : " + httpConnection.getResponseMessage());
                        }

                        try (InputStream in = httpConnection.getInputStream()){

                            String httpResponse = readFully(in);

                            return Double.valueOf(Json.parse(httpResponse).asObject()
                                    .get("query").asObject()
                                    .get("results").asObject()
                                    .get("rate").asObject()
                                    .get("Rate").asString());
                        }
                    }

                });
            }
        });
    }

    @Override
    public double getConversionRate(Currency from, Currency to) {

        final String currencyPair = from.name()+to.name();
        return currencyCache.get(currencyPair);


    }
}
