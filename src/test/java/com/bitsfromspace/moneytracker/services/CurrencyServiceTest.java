package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.model.Currency;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author chris
 * @since 02-11-15.
 */
public class CurrencyServiceTest {

    private TimeProvider timeProviderMock;
    private CurrencyRateProvider currencyRateProviderMock;
    private CurrencyService currencyService;

    @Before
    public void setup(){
        timeProviderMock = mock(TimeProvider.class);
        when(timeProviderMock.getTime()).thenReturn(System.currentTimeMillis());
        currencyRateProviderMock = mock(CurrencyRateProvider.class);
        currencyService = new CurrencyService(currencyRateProviderMock, timeProviderMock);
    }

    @Test
    public void testExceptionsArePropogated(){
        when(currencyRateProviderMock.getConversionRate(any(Currency.class), any(Currency.class)))
                .thenThrow(new RuntimeException("blast"));

        try{
            currencyService.getConversionRate(Currency.EUR, Currency.USD);
            fail();
        } catch (RuntimeException re){
            assertEquals("blast", re.getMessage());
        }
    }

    @Test
    public void testIdenticalPairIsNotRetrieved(){
        currencyService.getConversionRate(Currency.EUR, Currency.EUR);
        currencyService.getConversionRate(Currency.USD, Currency.USD);

        verify(currencyRateProviderMock, times(0)).getConversionRate(any(Currency.class), any(Currency.class));
        verifyNoMoreInteractions(currencyRateProviderMock);
    }
    @Test
    public void testDelegate(){
        when(currencyRateProviderMock.getConversionRate(Currency.EUR, Currency.USD)).thenReturn(2.3);
        when(currencyRateProviderMock.getConversionRate(Currency.USD, Currency.EUR)).thenReturn(3.2);
        assertEquals(2.3, currencyService.getConversionRate(Currency.EUR, Currency.USD), 0);
        assertEquals(3.2, currencyService.getConversionRate(Currency.USD, Currency.EUR), 0);
        verify(currencyRateProviderMock, times(2)).getConversionRate(any(Currency.class), any(Currency.class));
        verifyNoMoreInteractions(currencyRateProviderMock);
    }

    @Test
    public void testCached(){
        when(currencyRateProviderMock.getConversionRate(Currency.EUR, Currency.USD)).thenReturn(2.3);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);

        verify(currencyRateProviderMock, times(1)).getConversionRate(any(Currency.class), any(Currency.class));
        verifyNoMoreInteractions(currencyRateProviderMock);

    }
    @Test
    public void testExpiration(){

        reset(timeProviderMock);
        when(timeProviderMock.getTime()).thenReturn(100_000L);
        when(currencyRateProviderMock.getConversionRate(Currency.EUR, Currency.USD)).thenReturn(2.3);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);

        verify(currencyRateProviderMock, times(1)).getConversionRate(any(Currency.class), any(Currency.class));
        verifyNoMoreInteractions(currencyRateProviderMock);

        reset(currencyRateProviderMock, timeProviderMock);
        when(timeProviderMock.getTime()).thenReturn(100_000L + CurrencyService.DEFAULT_CACHE_TIMEOUT_MILLIS - 1);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        verify(currencyRateProviderMock, times(0)).getConversionRate(any(Currency.class), any(Currency.class));
        verifyNoMoreInteractions(currencyRateProviderMock);

        reset(currencyRateProviderMock, timeProviderMock);
        when(timeProviderMock.getTime()).thenReturn(100_000L + CurrencyService.DEFAULT_CACHE_TIMEOUT_MILLIS + 1);
        currencyService.getConversionRate(Currency.EUR, Currency.USD);
        verify(currencyRateProviderMock, times(1)).getConversionRate(any(Currency.class), any(Currency.class));
        verifyNoMoreInteractions(currencyRateProviderMock);

    }

}