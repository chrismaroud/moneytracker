package com.bitsfromspace.moneytracker.web.rest;

import com.bitsfromspace.moneytracker.model.Asset;
import com.bitsfromspace.moneytracker.model.Dao;
import com.bitsfromspace.moneytracker.model.Price;
import com.bitsfromspace.moneytracker.utils.DayUtils;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.procedure.TIntDoubleProcedure;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chris
 * @since 27-10-15.
 */
@Singleton
@Path("overview")
public class OverviewService {

    private final Dao dao;
    private final UserProvider userProvider;
    private final TimeProvider timeProvider;

    @Inject
    public OverviewService(Dao dao, UserProvider userProvider, TimeProvider timeProvider){
        this.dao = dao;
        this.userProvider = userProvider;
        this.timeProvider = timeProvider;
        System.out.println("Overvie service created!");
    }

    @GET
    @Produces("application/json")
    public Overview getOverview(@Context HttpServletRequest request){
        String userId = userProvider.getUserId(request);
        final int today = timeProvider.getDay();

        List<Asset> assets = dao.getAssets(userId, false);
        List<AssetOverview> assetOverviews = new ArrayList<>();
        TIntDoubleMap dayToPrice = new TIntDoubleHashMap();
        for (Asset asset : assets) {
            double totalYieldPercentage = 0;
            double oneMonthYieldPercentage = 0;
            double lastPrice = 0;
            for (Price price : dao.getPrices(asset.getId(), 10_000)){
                totalYieldPercentage += price.getChangePercentage();
                if (today - price.getDay() <= 30){
                    oneMonthYieldPercentage += price.getChangePercentage();
                }
                lastPrice = price.getPrice();
                dayToPrice.adjustOrPutValue(price.getDay(), price.getPrice(), price.getPrice());
            }
            assetOverviews.add(new AssetOverview(asset.getName(), asset.getCurrency().getSymbol(), lastPrice, totalYieldPercentage, oneMonthYieldPercentage));
        }
        final AtomicReference<Double> highestPrice = new AtomicReference<>(0.0);
        final AtomicInteger highestPriceDay = new AtomicInteger();
        final AtomicReference<Double> lowestPrice = new AtomicReference<>(0.0);
        final AtomicInteger lowestPriceDay = new AtomicInteger();
        final AtomicReference<Double> latestPrice = new AtomicReference<>(0.0);
        final AtomicInteger latestPriceDay = new AtomicInteger();

        dayToPrice.forEachEntry(new TIntDoubleProcedure() {
            @Override
            public boolean execute(int day, double totPrice) {
                if (highestPrice.get() < totPrice){
                    highestPrice.set(totPrice);
                    highestPriceDay.set(day);
                }

                if (lowestPrice.get() == 0 || lowestPrice.get() > totPrice){
                    lowestPrice.set(totPrice);
                    lowestPriceDay.set(day);
                }

                if (latestPriceDay.get() < day){
                    latestPriceDay.set(day);
                    latestPrice.set(totPrice);
                }

                return true;
            }
        });
        return new Overview(
                assetOverviews,
                highestPriceDay.get() == 0 ? null : DayUtils.getDate(highestPriceDay.get()),
                highestPrice.get(),
                lowestPriceDay.get() == 0 ? null : DayUtils.getDate(lowestPriceDay.get()),
                lowestPrice.get(),
                latestPriceDay.get() == 0 ? null : DayUtils.getDate(latestPriceDay.get()),
                latestPrice.get());
    }


    public static class Overview{
        private final List<AssetOverview> assets;

        private final Date highestPriceDate;
        private final double highestPrice;
        private final Date lowestPriceDate;
        private final double lowestPrice;
        private final Date currentPriceDate;
        private final double currentPrice;

        private Overview(List<AssetOverview> assets, Date highestPriceDate, double highestPrice, Date lowestPriceDate, double lowestPrice, Date currentPriceDate, double currentPrice) {
            this.assets = assets;
            this.highestPriceDate = highestPriceDate;
            this.highestPrice = highestPrice;
            this.lowestPriceDate = lowestPriceDate;
            this.lowestPrice = lowestPrice;
            this.currentPriceDate = currentPriceDate;
            this.currentPrice = currentPrice;
      }

        public List<AssetOverview> getAssets() {
            return assets;
        }
        public Date getHighestPriceDate() {
            return highestPriceDate;
        }

        public double getHighestPrice() {
            return highestPrice;
        }

        public Date getLowestPriceDate() {
            return lowestPriceDate;
        }

        public double getLowestPrice() {
            return lowestPrice;
        }

        public Date getCurrentPriceDate() {
            return currentPriceDate;
        }

        public double getCurrentPrice() {
            return currentPrice;
        }
    }

    public static class AssetOverview{
        private final String name;
        private final String currencySymbol;
        private final double price;
        private final double totalYieldPercentage;
        private final double oneMonthYieldPercentage;

        private AssetOverview(String name, String currencySymbol, double price, double totalYieldPercentage, double oneMonthYieldPercentage) {
            this.name = name;
            this.currencySymbol = currencySymbol;
            this.price = price;
            this.totalYieldPercentage = totalYieldPercentage;
            this.oneMonthYieldPercentage = oneMonthYieldPercentage;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public double getTotalYieldPercentage() {
            return totalYieldPercentage;
        }

        public double getOneMonthYieldPercentage() {
            return oneMonthYieldPercentage;
        }
    }
}
