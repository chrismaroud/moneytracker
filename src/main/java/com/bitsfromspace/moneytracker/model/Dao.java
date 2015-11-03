package com.bitsfromspace.moneytracker.model;

import java.util.Date;
import java.util.List;

/**
 * @author chris
 * @since 13-10-15.
 */
public interface Dao {

    User getUser(String id);
    void saveUser(User user);

    Asset getAsset(String assetId);
    void saveAsset(Asset asset);
    List<Asset> getAssets(String userId, boolean includeDeleted);
    List<Asset> getAssetsNotPricedOnDay(int day);

    void savePrice(Price price);
    Price getLatestPrice(String assetId);
    List<Price> getPrices(String assetId, int fromDay);

    //used for tests only:
    void zap();

}
