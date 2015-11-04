package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.utils.TimeProviderImpl;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author chris
 * @since 13-10-15.
 */
public class BloombergQuoteServiceTest {

    @Test
    public void test(){
        BloombergQuoteService quoteService = new BloombergQuoteService(new TimeProviderImpl(), 1);
        assertNotNull(quoteService.getQuote("MRKT:US"));
        assertNull(quoteService.getQuote("CHRIS:AMS"));
    }

}