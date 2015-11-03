package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.model.Currency;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * @author chris
 * @since 02-11-15.
 */
public class YahooCurrencyServiceTest {

    @Test
    public void test() {
        YahooCurrencyService yahooCurrencyService = new YahooCurrencyService();
        double euroToUsd = yahooCurrencyService.getConversionRate(Currency.EUR, Currency.USD);
        double usdToEuro = yahooCurrencyService.getConversionRate(Currency.USD, Currency.EUR);

        assertTrue(euroToUsd + " > 1.0 && " + euroToUsd + " < 2.0", euroToUsd > 1.0 && euroToUsd < 2.0);
        assertTrue(usdToEuro + " > 0.6 && " + usdToEuro + " < 1.0", usdToEuro > 0.6 && usdToEuro < 1.0);
    }

}