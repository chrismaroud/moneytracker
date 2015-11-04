package com.bitsfromspace.moneytracker.jobs;

import com.bitsfromspace.moneytracker.model.Asset;
import com.bitsfromspace.moneytracker.model.AssetType;
import com.bitsfromspace.moneytracker.model.Dao;
import com.bitsfromspace.moneytracker.model.Price;
import com.bitsfromspace.moneytracker.services.CurrencyService;
import com.bitsfromspace.moneytracker.services.QuoteService;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author chris
 * @since 02-11-15.
 */
public class SyncPricesJobTest {

    private SyncPricesJob syncPricesJob;
    private Dao daoMock;
    private TimeProvider timeProviderMock;

    @Before
    public void setup() {
        daoMock = mock(Dao.class);
        timeProviderMock = mock(TimeProvider.class);
        QuoteService quoteServiceMock = mock(QuoteService.class);
        CurrencyService currencyServiceMock = mock(CurrencyService.class);
        syncPricesJob = new SyncPricesJob(daoMock, timeProviderMock, quoteServiceMock, currencyServiceMock);
    }

    @Test
    public void testSavings() {
        final AtomicReference<Asset> cash = new AtomicReference<>();
        cash.set(new Asset());
        cash.get().setAssetType(AssetType.CASH);
        cash.get().setId("CASH-01");
        cash.get().setAmount(100_000.0);
        cash.get().setInterestPercentage(0.012);

        final AtomicReference<Price> lastPrice = new AtomicReference<>();

        final ArgumentCaptor<Price> priceArgumentCaptor = ArgumentCaptor.forClass(Price.class);
        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);
        when(daoMock.getLatestPrice("CASH-01")).thenAnswer(new Answer<Price>() {
            @Override
            public Price answer(InvocationOnMock invocation) throws Throwable {
                return lastPrice.get();
            }
        });

        doNothing().when(daoMock).savePrice(priceArgumentCaptor.capture());
        doNothing().when(daoMock).saveAsset(assetArgumentCaptor.capture());

        for (int i =0 ; i < 365 ; i ++) {
            when(timeProviderMock.getDay()).thenReturn(i);
            when(daoMock.getAssetsNotPricedOnDay(i)).thenReturn(Collections.singletonList(cash.get()));
            syncPricesJob.run();
            lastPrice.set(priceArgumentCaptor.getValue());
            cash.set(assetArgumentCaptor.getValue());
        }
        assertEquals(101_200.0, cash.get().getAmount(), 5);
        assertEquals(101_200.0, lastPrice.get().getPrice(), 5);
    }


}