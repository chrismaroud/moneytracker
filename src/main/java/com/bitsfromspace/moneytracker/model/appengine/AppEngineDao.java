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

    //
//    @Override
//    public User getUser(String userId) {
//        return loadUser(userId);
//    }
//
//    private int today(){
//        return timeProvider.getDay();
//    }
//
//    @Override
//    public User saveUser(String userId, Currency currency) {
//        UserImpl user = new UserImpl();
//        user.setUserId(userId);
//        user.setDefaultCurrency(currency);
//        saveUser(user);
//        return user;
//    }
//
//    @Override
//    public Share addShare(String userId, String name, Currency currency, String isin, int numberOfShares, double taxPercentage) {
//        return updateShare(userId, UUID.randomUUID().toString(), name, currency, isin, numberOfShares, taxPercentage);
//    }
//
//    @Override
//    public Share updateShare(String userId, String assetId, String name, Currency currency, String isin, int numberOfShares, double taxPercentage) {
//        AssetImpl asset = loadAsset(assetId);
//        if (asset == null){
//            asset = new AssetImpl();
//            ass
//        }
//    }
//
//    @Override
//    public void deleteShare(String userId, String assetId) {
//        deleteAsset(assetId);
//     }
//
//    @Override
//    public Option addOption(String userId, String name, Currency currency, String isin, int numberOfOptions, double strikePrice, double taxPercentage) {
//       return updateOption(userId, UUID.randomUUID().toString(), name, currency, isin, numberOfOptions, strikePrice, taxPercentage);
//    }
//
//    @Override
//    public Option updateOption(String userId, String assetId, String name, Currency currency, String isin, int numberOfOptions, double strikePrice, double taxPercentage) {
//    }
//
//    @Override
//    public void deleteOption(String userId, String assetId) {
//        deleteAsset(assetId);
//    }
//
//    @Override
//    public Cash addCash(String userId, String name, Currency currency, double amount, double interestPercentage) {
//        return updateCash(userId, UUID.randomUUID().toString(), name, currency, amount, interestPercentage);
//    }
//
//    @Override
//    public Cash updateCash(String userId, String assetId, String name, Currency currency, double amount, double interestPercentage) {
//        saveAsset(assetId, AssetType.CASH, userId, name, currency, timeProvider.getDay(), null, )
//    }
//
//    @Override
//    public void deleteCash(String userId, String assetId) {
//        Cash cash = loadCash(assetId);
//        if (cash == null){
//            return;
//        }
//        Cash deletedCash = new Cash(cash.getId(), userId, cash.getName(), cash.getCurrency(), cash.getStartDay(), timeProvider.getDay(),
//                cash.getInterestPercentage(), cash.getAmount());
//        saveCash(deletedCash);
//    }
//
//    @Override
//    public List<Asset> getAssets(String userId, boolean includeDeleted) {
//        List<Asset> assets = new ArrayList<>();
//        loadAssets(assets, CashImpl.class, userId, includeDeleted);
//        loadAssets(assets, ShareImpl.class, userId, includeDeleted);
//        loadAssets(assets, OptionImpl.class, userId, includeDeleted);
//        return assets;
//    }
//
//    private void loadAssets(List<Asset> target, Class<?> assetType, String userId, boolean includeDeleted){
//        Query query = ofy().load().type(assetType).filter("userId", userId);
//        if (! includeDeleted){
//            query = query.filter("endDay", null);
//        }
//        //noinspection unchecked
//        target.addAll(query.list());
//    }
//
//    @Override
//    public Price addTodaysPrice(long assetId, int holding, double price, double change, double changePercentage) {
//        PriceImpl newPrice = new PriceImpl(assetId, today(), holding, price, change, changePercentage);
//        ofy().save().entity(newPrice).now();
//        return newPrice;
//    }
//
//    @Override
//    public Price getLatestPrice(long assetId) {
//        return ofy()
//                .load()
//                .type(PriceImpl.class)
//                .filter("assetId = ", assetId)
//                .order("day")
//                .orderKey(true)
//                .first()
//                .now();
//    }
//
//    @Override
//    public List<Price> getPrices(long assetId, int daysBack) {
//        //noinspection unchecked
//        return (List) ofy()
//                .load()
//                .type(PriceImpl.class)
//                .filter("assetId = ", assetId)
//                .filter("day >= ", today() - daysBack)
//                .filter("day <= ", today())
//                .order("day")
//                .orderKey(false)
//                .list();
//
//    }
//
//    @Override
//    public void zap() {
//        zap(UserImpl.class);
//        zap(PriceImpl.class);
//        zap(ShareImpl.class);
//        zap(OptionImpl.class);
//        zap(CashImpl.class);
//    }
//
//    private void zap(Class<?> entityClass){
//        final List<Long> ids = new ArrayList<>();
//        ofy().load().type(entityClass).keys().forEach(new Consumer<Key<?>>() {
//            @Override
//            public void accept(Key<?> userKey) {
//                ids.add(userKey.getId());
//            }
//        });
//        ofy().delete().type(entityClass).ids(ids).now();
//    }
//
//    private User loadUser(String userId){
//        try {
//            Entity entity = datastoreService.get(KeyFactory.createKey("User", userId));
//            UserImpl user = new UserImpl();
//            user.setUserId(userId);
//            user.setDefaultCurrency(Currency.valueOf((String) entity.getProperty("currency")));
//            return user;
//        } catch (EntityNotFoundException enfe){
//            return null;
//        }
//    }
//    private void saveUser(User user) {
//        Entity entity = new Entity(KeyFactory.createKey("User", user.getUserId()));
//        entity.setProperty("currency", user.getDefaultCurrency());
//        datastoreService.put(entity);
//    }
//    private AssetImpl loadAsset(String assetId){
//        Entity entity;
//        try {
//            entity =  datastoreService.get(KeyFactory.createKey("Asset", assetId));
//        } catch (EntityNotFoundException enfe){
//            return null;
//        }
//        AssetImpl asset = new AssetImpl();
//        asset.setId((String) entity.getProperty("id"));
//        asset.setUserId((String) entity.getProperty("userId"));
//        asset.setName((String) entity.getProperty("name"));
//        asset.setCurrency(Currency.valueOf((String) entity.getProperty("currency")));
//        asset.setStartDay(getInteger(entity.getProperty("startDay"));
//        asset.setEndDay(getInteger(entity.getProperty("endDay"));
//        asset.setAmount((Double) entity.getProperty("amount"));
//        asset.setInterestPercentage((Double) entity.getProperty("interestPercentage"));
//        asset.setNumberOfShares(getInteger(entity.getProperty("numberOfShares"));
//        asset.setStrikePrice((Double) entity.getProperty("strikePrice"));
//        asset.setTaxPercentage((Double) entity.getProperty("taxPercentage"));
//        asset.setAssetType(AssetType.valueOf((String) entity.getProperty("assetType")));
//        asset.setIsin((String) entity.getProperty("isin"));
//        return asset;
//    }
//    private AssetImpl saveAsset(String id, AssetType assetType, String userId, String name, Currency currency,
//                                Integer startDay, Integer endDay, Double amount, Double interestPercentage,
//                                Integer numberOfShares,Double strikePrice, Double taxPercentage, String isin){
//        Entity entity = new Entity(KeyFactory.createKey("Asset", id));
//        entity.setProperty("userId", userId);
//        entity.setProperty("name", name);
//        entity.setProperty("currency", currency);
//        entity.setProperty("startDay", startDay);
//        entity.setProperty("endDay", endDay);
//        entity.setProperty("amount", amount);
//        entity.setProperty("interestPercentage", interestPercentage);
//        entity.setProperty("numberOfShares", numberOfShares);
//        entity.setProperty("strikePrice", strikePrice);
//        entity.setProperty("taxPercentage", taxPercentage);
//        entity.setProperty("assetType", assetType);
//        entity.setProperty("isin", isin);
//        datastoreService.put(entity);
//
//        AssetImpl asset = new AssetImpl();
//        asset.setId(id);
//        asset.setAssetType(assetType);
//        asset.setUserId(userId);
//        asset.setName(name);
//        asset.setCurrency(currency);
//        asset.setStartDay(startDay);
//        asset.setEndDay(endDay);
//        asset.setAmount(amount);
//        asset.setInterestPercentage(interestPercentage);
//        asset.setNumberOfShares(numberOfShares);
//        asset.setStrikePrice(strikePrice);
//        asset.setTaxPercentage(taxPercentage);
//        asset.setIsin(isin);
//
//        return asset;
//    }
//    private void deleteAsset(String id){
//        AssetImpl asset = loadAsset(id);
//        if (asset != null){
//            saveAsset(id, asset.getAssetType(), asset.getUserId(), asset.getName(), asset.getCurrency(), asset.getStartDay(),
//                    timeProvider.getDay(), asset.getAmount(),asset.getInterestPercentage(), asset.getNumberOfShares(),
//                    asset.getStrikePrice(), asset.getTaxPercentage(), asset.getIsin());
//
//        }
//    }
//
//    private Price loadPrice(String priceId){
//        Entity entity;
//        try {
//            entity = datastoreService.get(KeyFactory.createKey("Price", priceId));
//        } catch (EntityNotFoundException enfe){
//            return null;
//        }
//        assert "Price".equals(entity.getKind());
//        PriceImpl price = new PriceImpl();
//        price.setId(priceId);
//        price.setAssetId((String) entity.getProperty("assetId"));
//        price.setAssetId((String) entity.getProperty("day"));
//        price.setAssetId((String) entity.getProperty("holding"));
//        price.setAssetId((String) entity.getProperty("price"));
//        price.setAssetId((String) entity.getProperty("change"));
//        price.setAssetId((String) entity.getProperty("changePercentage"));
//        return price;
//    }
//    private Price savePrice(String id, String assetId, int day, int holding, double price, double change, double changePercentage){
//        Entity entity = new Entity(KeyFactory.createKey("Price", id));
//        entity.setProperty("assetId", assetId);
//        entity.setProperty("day", day);
//        entity.setProperty("holding", holding);
//        entity.setProperty("price", price);
//        entity.setProperty("change", change);
//        entity.setProperty("changePercentage", changePercentage);
//
//        datastoreService.put(entity);
//        return loadPrice(id);
//    }
}
