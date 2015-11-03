package com.bitsfromspace.moneytracker.services;

/**
 * @author chris
 * @since 13-10-15.
 */
public interface QuoteService {

    /**
     * Get latest services
     * @param bloombergQuote - the instrument identifier for which to get the services, see: http://www.bloomberg.com/quote/MRKT:US
     * @return the last quoted price - or - null if services could not be found
     */
    Double getQuote(String bloombergQuote);
}
