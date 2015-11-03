package com.bitsfromspace.moneytracker.model.appengine;

import com.bitsfromspace.moneytracker.model.*;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import com.google.appengine.api.datastore.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


/**
 * @author chris
 * @since 26-10-15.
 */
@Singleton
public class AppEngineDao implements Dao {
    private final DatastoreService datastoreService;


    public AppEngineDao() {

        datastoreService = DatastoreServiceFactory.getDatastoreService();
//
//
//        Query q = new Query("User");
//        q.addSort("pietje", Query.SortDirection.DESCENDING);
//        q=q.setKeysOnly();
//        datastoreService.prepare(q).asList(FetchOptions.Builder.withDefaults());
    }

    @Override
    public User getUser(String id) {
        try {
            Entity entity = datastoreService.get(KeyFactory.createKey("User", id));
            User user = new User();
            user.setDefaultCurrency(Currency.valueOf((String) entity.getProperty("defaultCurrency")));
            user.setId(id);
            user.verify();
            return user;
        } catch (EntityNotFoundException enfe) {
            return null;
        }
    }

    @Override
    public void saveUser(User user) {
        user.verify();
        Entity entity = new Entity(KeyFactory.createKey("User", user.getId()));
        entity.setUnindexedProperty("defaultCurrency", user.getDefaultCurrency().toString());
        datastoreService.put(entity);
    }

    @Override
    public void saveAsset(Asset asset) {
        asset.verify();
        Entity entity = new Entity(KeyFactory.createKey("Asset", asset.getId()));
        entity.setProperty("userId", asset.getUserId());
        entity.setUnindexedProperty("name", asset.getName());
        entity.setUnindexedProperty("currency", asset.getCurrency().toString());
        entity.setProperty("startDay", asset.getStartDay());
        entity.setProperty("endDay", asset.getEndDay());
        entity.setUnindexedProperty("amount", asset.getAmount());
        entity.setUnindexedProperty("interestPercentage", asset.getInterestPercentage());
        entity.setUnindexedProperty("numberOfShares", asset.getNumberOfShares());
        entity.setUnindexedProperty("strikePrice", asset.getStrikePrice());
        entity.setUnindexedProperty("taxPercentage", asset.getTaxPercentage());
        entity.setUnindexedProperty("assetType", asset.getAssetType().toString());
        entity.setUnindexedProperty("isin", asset.getIsin());
        datastoreService.put(entity);
    }

    @Override
    public List<Asset> getAssets(String userId, boolean includeDeleted) {
        Query.Filter filter = new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, userId);

        if (!includeDeleted) {
            filter = Query.CompositeFilterOperator.and(filter, new Query.FilterPredicate("endDay", Query.FilterOperator.EQUAL, null));
        }

        List<Asset> results = new ArrayList<>();
        Query query = new Query("Asset")
                .setFilter(filter)
                .setKeysOnly();
        for (Entity entity : datastoreService.prepare(query).asIterable()) {
            results.add(getAsset(entity.getKey().getName()));
        }
        return results;
    }

    @Override
    public List<Asset> getAssetsNotPricedOnDay(int day) {
        Query.Filter filter = new Query.FilterPredicate("endDay", Query.FilterOperator.EQUAL, null);

        List<String> assetIds = new ArrayList<>();
        Query query = new Query("Asset")
                .setFilter(filter)
                .setKeysOnly();
        for (Entity entity : datastoreService.prepare(query).asIterable()) {
            assetIds.add(
                    entity.getKey().getName());
        }

        List<Asset> assetsNotPriced = new ArrayList<>();
        for (String assetId : assetIds) {
            filter = Query.CompositeFilterOperator.and(
                    new Query.FilterPredicate("assetId", Query.FilterOperator.EQUAL, assetId),
                    new Query.FilterPredicate("day", Query.FilterOperator.EQUAL, day));
            query = new Query("Price")
                    .setFilter(filter)
                    .setKeysOnly();
            if (null != datastoreService.prepare(query).asSingleEntity()){
                assetsNotPriced.add(getAsset(assetId));
            }
        }
        return assetsNotPriced;
    }

    @Override
    public Asset getAsset(String assetId) {
        try {
            Entity entity = datastoreService.get(KeyFactory.createKey("Asset", assetId));
            Asset asset = new Asset();
            asset.setId(assetId);
            asset.setUserId((String) entity.getProperty("userId"));
            asset.setName((String) entity.getProperty("name"));
            asset.setCurrency(Currency.valueOf((String) entity.getProperty("currency")));
            asset.setStartDay(getInteger(entity.getProperty("startDay")));
            asset.setEndDay(getInteger(entity.getProperty("endDay")));
            asset.setAmount((Double) entity.getProperty("amount"));
            asset.setInterestPercentage((Double) entity.getProperty("interestPercentage"));
            asset.setNumberOfShares(getInteger(entity.getProperty("numberOfShares")));
            asset.setStrikePrice((Double) entity.getProperty("strikePrice"));
            asset.setTaxPercentage((Double) entity.getProperty("taxPercentage"));
            asset.setAssetType(AssetType.valueOf((String) entity.getProperty("assetType")));
            asset.setIsin((String) entity.getProperty("isin"));
            asset.verify();
            return asset;
        } catch (EntityNotFoundException nfe) {
            return null;
        }
    }


    @Override
    public void savePrice(Price price) {
        price.verify();
        Entity entity = new Entity(KeyFactory.createKey("Price", price.getId()));
        entity.setProperty("assetId", price.getAssetId());
        entity.setProperty("day", price.getDay());
        entity.setUnindexedProperty("holding", price.getHolding());
        entity.setUnindexedProperty("price", price.getPrice());
        entity.setUnindexedProperty("change", price.getChange());
        entity.setUnindexedProperty("changePercentage", price.getChangePercentage());

        datastoreService.put(entity);
    }

    private Price getPrice(String id) {
        try {
            Entity entity = datastoreService.get(KeyFactory.createKey("Price", id));
            Price price = new Price();
            price.setId(id);
            price.setAssetId((String) entity.getProperty("assetId"));
            price.setDay(getInteger(entity.getProperty("day")));
            price.setHolding(getInteger(entity.getProperty("holding")));
            price.setPrice((Double) entity.getProperty("price"));
            price.setChange((Double) entity.getProperty("change"));
            price.setChangePercentage((Double) entity.getProperty("changePercentage"));
            price.verify();
            return price;
        } catch (EntityNotFoundException enfe) {
            return null;
        }
    }

    @Override
    public Price getLatestPrice(String assetId) {
        Query query = new Query("Price")
                .setFilter(new Query.FilterPredicate(assetId, Query.FilterOperator.EQUAL, assetId))
                .setKeysOnly()
                .addSort("day", Query.SortDirection.DESCENDING);
        Entity latestPrice = datastoreService.prepare(query).asSingleEntity();
        if (latestPrice == null) {
            return null;
        }

        return getPrice(latestPrice.getKey().getName());
    }

    @Override
    public List<Price> getPrices(String assetId, int fromDay) {
        Query query = new Query("Price")
                .setFilter(
                        Query.CompositeFilterOperator.and(
                            new Query.FilterPredicate("assetId", Query.FilterOperator.EQUAL, assetId),
                            new Query.FilterPredicate("day", Query.FilterOperator.GREATER_THAN_OR_EQUAL, fromDay)
                        )
                )
                .setKeysOnly()
                .addSort("day", Query.SortDirection.ASCENDING);
        List<Price> prices = new ArrayList<>();
        for (Entity entity : datastoreService.prepare(query).asIterable()) {
            prices.add(getPrice(entity.getKey().getName()));
        }
        return prices;
    }

    @Override
    public void zap() {
        for (Entity entity : datastoreService.prepare(new Query("Price").setKeysOnly()).asIterable()){
            datastoreService.delete(entity.getKey());
        }
        for (Entity entity : datastoreService.prepare(new Query("User").setKeysOnly()).asIterable()){
            datastoreService.delete(entity.getKey());
        }
        for (Entity entity : datastoreService.prepare(new Query("Asset").setKeysOnly()).asIterable()){
            datastoreService.delete(entity.getKey());
        }
    }

    private Integer getInteger(Object in){
        if (in == null){
            return null;
        }
        return ((Long)in).intValue();
    }

}
