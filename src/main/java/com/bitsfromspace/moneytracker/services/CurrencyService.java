package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.model.Currency;

/**
 * @author chris
 * @since 02-11-15.
 */
public interface CurrencyService {
    double getConversionRate(Currency from, Currency to);
}
