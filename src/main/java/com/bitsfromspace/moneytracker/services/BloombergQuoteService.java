package com.bitsfromspace.moneytracker.services;

import com.bitsfromspace.moneytracker.MoneyTrackerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import javax.inject.Singleton;
import java.io.IOException;

/**
 * @author chris
 * @since 13-10-15.
 */
@Singleton
public class BloombergQuoteService implements QuoteService {

    @Override
    public Double getQuote(String bloombergQuote) {

        try {
            Element priceElement =
                    Jsoup.connect(String.format("http://www.bloomberg.com/quote/%s", bloombergQuote))
                            .get()
                            .select(".price")
                            .first();

            return priceElement == null ? null : Double.valueOf(priceElement.text());

        } catch (IOException ioex) {
            throw new MoneyTrackerException(ioex.getMessage(), ioex);
        }
    }
}
