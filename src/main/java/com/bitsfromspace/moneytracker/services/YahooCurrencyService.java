package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.MoneyTrackerException;
import com.bitsfromspace.moneytracker.model.Currency;
import com.eclipsesource.json.Json;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.bitsfromspace.moneytracker.utils.ExceptionUtils.runUnchecked;
import static com.bitsfromspace.moneytracker.utils.IoUtils.readFully;

/**
 * @author chris
 * @since 02-11-15.
 */
public class YahooCurrencyService implements CurrencyRateProvider {

    private static final String CURRENCY_PAIR_PARAMETER = "$CURRENCYPAIR";
    private static final String YAHOO_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20%3D%20%22" + CURRENCY_PAIR_PARAMETER + "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    @Override
    public double getConversionRate(Currency from, Currency to) {

        final String currencyPair = from.name()+to.name();

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
}
