package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.utils.Cache;
import com.bitsfromspace.moneytracker.utils.ExceptionUtils;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author chris
 * @since 13-10-15.
 */
@Singleton
public class BloombergQuoteService implements QuoteService {

    private final Cache<String, Double> quoteCache;

    @Inject
    public BloombergQuoteService(TimeProvider timeProvider, @Named("BloombergQuoteService.cacheRententionHours") int cacheRetentionHours) {
        quoteCache = new Cache<>(timeProvider, TimeUnit.HOURS.toMillis(cacheRetentionHours), new Cache.Delegate<String, Double>() {
            @Override
            public Double get(final String bloombergQuote) {
                return ExceptionUtils.runUnchecked(new Callable<Double>() {
                    @Override
                    public Double call() throws Exception {
                        Element priceElement =
                                Jsoup.connect(String.format("http://www.bloomberg.com/quote/%s", bloombergQuote))
                                        .get()
                                        .select(".price")
                                        .first();

                        return priceElement == null ? null : Double.valueOf(priceElement.text());
                    }
                });
            }
        });
    }

    @Override
    public Double getQuote(String bloombergQuote) {
        return quoteCache.get(bloombergQuote);
    }
}
