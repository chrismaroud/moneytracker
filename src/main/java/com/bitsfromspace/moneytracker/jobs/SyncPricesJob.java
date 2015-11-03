package com.bitsfromspace.moneytracker.jobs;

import com.bitsfromspace.moneytracker.model.*;
import com.bitsfromspace.moneytracker.services.CurrencyService;
import com.bitsfromspace.moneytracker.services.QuoteService;
import com.bitsfromspace.moneytracker.utils.TimeProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author chris
 * @since 02-11-15.
 */
@Singleton
public class SyncPricesJob implements Job {

    private final static Logger LOGGER = Logger.getLogger(SyncPricesJob.class.getName());

    private final Dao dao;
    private final TimeProvider timeProvider;
    private final QuoteService quoteService;
    private final CurrencyService currencyService;

    @Inject
    public SyncPricesJob(Dao dao, TimeProvider timeProvider, QuoteService quoteService, CurrencyService currencyService) {
        this.dao = dao;
        this.timeProvider = timeProvider;
        this.quoteService = quoteService;
        this.currencyService = currencyService;
    }


    @Override
    public void run() {
        int today = timeProvider.getDay();
        for (Asset asset : dao.getAssetsNotPricedOnDay(today)) {
            try {
                switch (asset.getAssetType()) {
                    case CASH:
                        priceCash(asset);
                        break;
                    case SHARE:
                    case OPTION:
                        priceShare(asset);
                        break;
                    default:
                        throw new IllegalArgumentException(" No such asset type: " + asset.getAssetType());
                }
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, "Error pricing asset #" + asset.getId(), t);
            }
        }
    }

    private void priceShare(Asset asset) {
        int today = timeProvider.getDay();
        User user = dao.getUser(asset.getUserId());
        Price lastPrice = dao.getLatestPrice(asset.getId());
        Double quote = quoteService.getQuote(asset.getId());
        Price price = new Price();
        price.setAssetId(asset.getId());
        price.setDay(today);
        price.setHolding(asset.getNumberOfShares());
        if (asset.getAssetType() == AssetType.OPTION) {
            quote = Math.max(0, quote - asset.getStrikePrice());
        }
        price.setPrice(asset.getNumberOfShares() * quote * currencyService.getConversionRate(asset.getCurrency(), user.getDefaultCurrency()) * (1.0 - asset.getTaxPercentage()));
        price.setId(UUID.randomUUID().toString());
        price.setChange(lastPrice == null ? 0 : price.getPrice() - lastPrice.getPrice());
        price.setChangePercentage(price.getChange() / price.getPrice());
        dao.savePrice(price);
    }

    private void priceCash(Asset asset) {
        assert asset.getAssetType() == AssetType.CASH;
        int today = timeProvider.getDay();
        Price lastPrice = dao.getLatestPrice(asset.getId());
        double value = asset.getAmount();
        int days = lastPrice == null ? 0 : today - lastPrice.getDay();

        double newValue = value + (value * asset.getInterestPercentage() * days / 365);
        Price price = new Price();
        price.setPrice(newValue);
        price.setHolding(0);
        price.setDay(today);
        price.setAssetId(asset.getId());
        price.setId(UUID.randomUUID().toString());
        price.setChange(newValue - value);
        price.setChangePercentage((newValue - value) / value);
        dao.savePrice(price);

        asset.setAmount(newValue);
        dao.saveAsset(asset);
    }
}
