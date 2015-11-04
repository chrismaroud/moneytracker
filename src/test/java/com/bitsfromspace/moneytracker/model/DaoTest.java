package com.bitsfromspace.moneytracker.model;

import com.bitsfromspace.moneytracker.utils.TimeProvider;
import com.bitsfromspace.moneytracker.utils.TimeProviderImpl;
import org.junit.Before;
import org.junit.Test;
import sun.security.provider.SHA;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author chris
 * @since 23-10-15.
 */
public abstract class DaoTest {

    private Dao dao;
    private TimeProvider timeProvider;
    @Before
    public void setup(){
        timeProvider = mock(TimeProvider.class);
        TimeProvider realProvider = new TimeProviderImpl();
        when(timeProvider.getDay()).thenReturn(realProvider.getDay());
        when(timeProvider.getTime()).thenReturn(realProvider.getTime());
        dao = getDao(timeProvider);
        dao.zap();
    }

    protected abstract Dao getDao(TimeProvider timeProvider);

    @Test
    public void testUser(){
        assertNull(dao.getUser("someID"));
        User user = new User();
        user.setId("someID");
        user.setDefaultCurrency(Currency.USD);
        dao.saveUser(user);
        assertEquals("someID", user.getId());
        assertEquals(Currency.USD, user.getDefaultCurrency());
        user = dao.getUser("someID");
        assertNotNull(user);
        assertEquals("someID", user.getId());
        assertEquals(Currency.USD, user.getDefaultCurrency());
    }

    @Test
    public void testShare(){
        testUser();
        assertEquals(0, dao.getAssets("someID", true).size());
        Asset share = new Asset();
        share.setId("shareID");
        share.setName("myShare");
        share.setAssetType(AssetType.SHARE);
        share.setUserId("someID");
        share.setCurrency(Currency.USD);
        share.setIsin("ISIN1234");
        share.setNumberOfShares(124124.);
        share.setTaxPercentage(0.41123132);
        share.setStartDay(timeProvider.getDay());
        dao.saveAsset(share);
       
        //test load
        share = dao.getAsset("shareID");
        assertEquals("someID", share.getUserId());
        assertEquals("myShare", share.getName());
        assertEquals("ISIN1234", share.getIsin());
        assertEquals(124124, share.getNumberOfShares().intValue());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals(0.41123132, share.getTaxPercentage(), 0);
        assertEquals("myShare", share.getName());
        assertNotNull(share.getStartDay());
        assertNull(share.getEndDay());
        assertNotNull(share.getId());

        //test update
        share.setName("myShare2");
        share.setCurrency(Currency.EUR);
        share.setNumberOfShares(98.);
        share.setTaxPercentage(0.52);
        dao.saveAsset(share);
     
        //test load post update
        share =  dao.getAssets("someID", false).get(0);
        assertEquals("someID", share.getUserId());
        assertEquals("ISIN1234", share.getIsin());
        assertEquals(98, share.getNumberOfShares().intValue());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals(0.52, share.getTaxPercentage(), 0);
        assertEquals("myShare2", share.getName());
        assertNotNull(share.getStartDay());
        assertNull(share.getEndDay());
        assertNotNull(share.getId());

        //test delete
        assertEquals(1, dao.getAssets("someID", false).size());
        share.setEndDay(3);
        dao.saveAsset(share);
        assertEquals(0, dao.getAssets("someID", false).size());
        assertEquals(1, dao.getAssets("someID", true).size());
        share =  dao.getAssets("someID", true).get(0);
        assertEquals("someID", share.getUserId());
        assertEquals("ISIN1234", share.getIsin());
        assertEquals(98, share.getNumberOfShares().intValue());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals(0.52, share.getTaxPercentage(), 0);
        assertEquals("myShare2", share.getName());
        assertNotNull(share.getStartDay());
        assertNotNull(share.getEndDay());
        assertNotNull(share.getId());
   }

    @Test
    public void testOption(){
        testUser();
        assertEquals(0, dao.getAssets("someID", true).size());
        Asset option = new Asset();
        option.setId("optionID");
        option.setName("myOption");
        option.setAssetType(AssetType.OPTION);
        option.setUserId("someID");
        option.setCurrency(Currency.USD);
        option.setIsin("ISIN1234");
        option.setNumberOfShares(124124.);
        option.setTaxPercentage(0.41123132);
        option.setStrikePrice(32.1234);
        option.setStartDay(timeProvider.getDay());
        dao.saveAsset(option);

        //test load
        option = dao.getAsset("optionID");
        assertEquals("someID", option.getUserId());
        assertEquals("myOption", option.getName());
        assertEquals("ISIN1234", option.getIsin());
        assertEquals(124124, option.getNumberOfShares().intValue());
        assertEquals(AssetType.OPTION, option.getAssetType());
        assertEquals(0.41123132, option.getTaxPercentage(), 0);
        assertEquals("myOption", option.getName());
        assertEquals(32.1234, option.getStrikePrice(), 0);
        assertNotNull(option.getStartDay());
        assertNull(option.getEndDay());
        assertNotNull(option.getId());

        //test update
        option.setName("myOption2");
        option.setCurrency(Currency.EUR);
        option.setNumberOfShares(98.);
        option.setTaxPercentage(0.52);
        option.setStrikePrice(35.6789);
        dao.saveAsset(option);

        //test load post update
        option =  dao.getAssets("someID", false).get(0);
        assertEquals("someID", option.getUserId());
        assertEquals("ISIN1234", option.getIsin());
        assertEquals(98, option.getNumberOfShares().intValue());
        assertEquals(AssetType.OPTION, option.getAssetType());
        assertEquals(0.52, option.getTaxPercentage(), 0);
        assertEquals("myOption2", option.getName());
        assertEquals(35.6789, option.getStrikePrice(), 0);
        assertNotNull(option.getStartDay());
        assertNull(option.getEndDay());
        assertNotNull(option.getId());

        //test delete
        assertEquals(1, dao.getAssets("someID", false).size());
        option.setEndDay(3);
        dao.saveAsset(option);
        assertEquals(0, dao.getAssets("someID", false).size());
        assertEquals(1, dao.getAssets("someID", true).size());
        option =  dao.getAssets("someID", true).get(0);
        assertEquals("someID", option.getUserId());
        assertEquals("ISIN1234", option.getIsin());
        assertEquals(98, option.getNumberOfShares().intValue());
        assertEquals(AssetType.OPTION, option.getAssetType());
        assertEquals(0.52, option.getTaxPercentage(), 0);
        assertEquals("myOption2", option.getName());
        assertEquals(35.6789, option.getStrikePrice(), 0);
        assertNotNull(option.getStartDay());
        assertNotNull(option.getEndDay());
        assertNotNull(option.getId());
    }

    @Test
    public void testCash(){
        testUser();
        assertEquals(0, dao.getAssets("someID", true).size());
        Asset cash = new Asset();
        cash.setId("cashID");
        cash.setName("myCash");
        cash.setUserId("someID");
        cash.setCurrency(Currency.USD);
        cash.setAssetType(AssetType.CASH);
        cash.setAmount(90000.0);
        cash.setInterestPercentage(0.0012345);
        cash.setStartDay(timeProvider.getDay());
        dao.saveAsset(cash);

        //test load
        cash = dao.getAsset("cashID");
        assertEquals("someID", cash.getUserId());
        assertEquals("myCash", cash.getName());
        assertEquals(AssetType.CASH, cash.getAssetType());
        assertEquals(90000.0, cash.getAmount(), 0);
        assertEquals(0.0012345, cash.getInterestPercentage(), 0);
        assertEquals(Currency.USD, cash.getCurrency());
        assertNotNull(cash.getStartDay());
        assertNull(cash.getEndDay());
        assertNotNull(cash.getId());

        //test update
        cash.setName("myCash2");
        cash.setCurrency(Currency.EUR);
        cash.setAmount(98.1234);
        cash.setInterestPercentage(0.02233);
        dao.saveAsset(cash);

        //test load post update
        cash =  dao.getAssets("someID", false).get(0);
        assertEquals("someID", cash.getUserId());
        assertEquals("myCash2", cash.getName());
        assertEquals(AssetType.CASH, cash.getAssetType());
        assertEquals(98.1234, cash.getAmount(), 0);
        assertEquals(0.02233, cash.getInterestPercentage(), 0);
        assertEquals(Currency.EUR, cash.getCurrency());
        assertNotNull(cash.getStartDay());
        assertNull(cash.getEndDay());
        assertNotNull(cash.getId());

        //test delete
        assertEquals(1, dao.getAssets("someID", false).size());
        cash.setEndDay(3);
        dao.saveAsset(cash);
        assertEquals(0, dao.getAssets("someID", false).size());
        assertEquals(1, dao.getAssets("someID", true).size());
        cash =  dao.getAssets("someID", true).get(0);
        assertEquals("someID", cash.getUserId());
        assertEquals("myCash2", cash.getName());
        assertEquals(AssetType.CASH, cash.getAssetType());
        assertEquals(98.1234, cash.getAmount(), 0);
        assertEquals(0.02233, cash.getInterestPercentage(), 0);
        assertEquals(Currency.EUR, cash.getCurrency());
        assertNotNull(cash.getStartDay());
        assertNotNull(cash.getEndDay());
        assertNotNull(cash.getId());
    }

    @Test
    public void testGetAssets(){
        assertEquals(0, dao.getAssets("someID", false).size());
        assertEquals(0, dao.getAssets("someID", true).size());

        Asset share = new Asset();
        share.setUserId("someID");
        share.setId("shareID");
        share.setName("myShare");
        share.setCurrency(Currency.EUR);
        share.setIsin("isin1234");
        share.setNumberOfShares(124124.);
        share.setTaxPercentage(0.2514);
        share.setAssetType(AssetType.SHARE);
        share.setStartDay(timeProvider.getDay());
        dao.saveAsset(share);

        assertEquals(1, dao.getAssets("someID", false).size());
        assertEquals(1, dao.getAssets("someID", true).size());
        share = getAsset(AssetType.SHARE, true);
        assertEquals("isin1234", share.getIsin());
        assertEquals(Currency.EUR, share.getCurrency());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals("someID", share.getUserId());
        assertEquals("myShare", share.getName());
        assertEquals(124124, share.getNumberOfShares().intValue());
        assertEquals(0.2514, share.getTaxPercentage(), 0);

        Asset option = new Asset();
        option.setUserId("someID");
        option.setId("optionId");
        option.setName("myOption");
        option.setCurrency(Currency.USD);
        option.setIsin("isin72131");
        option.setNumberOfShares(242424.);
        option.setStrikePrice(199.9996);
        option.setTaxPercentage(0.3938);
        option.setAssetType(AssetType.OPTION);
        option.setStartDay(100);
        dao.saveAsset(option);

        assertEquals(2, dao.getAssets("someID", false).size());
        assertEquals(2, dao.getAssets("someID", true).size());
        share = getAsset(AssetType.SHARE, true);
        assertEquals("isin1234", share.getIsin());
        assertEquals(Currency.EUR, share.getCurrency());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals("someID", share.getUserId());
        assertEquals("myShare", share.getName());
        assertEquals(124124, share.getNumberOfShares().intValue());
        assertEquals(0.2514, share.getTaxPercentage(), 0);
        option = getAsset(AssetType.OPTION, true);
        assertEquals("isin72131", option.getIsin());
        assertEquals(Currency.USD, option.getCurrency());
        assertEquals(AssetType.OPTION, option.getAssetType());
        assertEquals("someID", option.getUserId());
        assertEquals("myOption", option.getName());
        assertEquals(242424, option.getNumberOfShares().intValue());
        assertEquals(0.3938, option.getTaxPercentage(), 0);
        assertEquals(199.9996, option.getStrikePrice(), 0);

        Asset cash = new Asset();
        cash.setId("cashId");
        cash.setUserId("someID");
        cash.setName("myCash");
        cash.setCurrency(Currency.EUR);
        cash.setAmount(921849124.0);
        cash.setInterestPercentage(0.00214124);
        cash.setAssetType(AssetType.CASH);
        cash.setStartDay(100);
        dao.saveAsset(cash);

        assertEquals(3, dao.getAssets("someID", false).size());
        assertEquals(3, dao.getAssets("someID", true).size());
        share = getAsset(AssetType.SHARE, true);
        assertEquals("isin1234", share.getIsin());
        assertEquals(Currency.EUR, share.getCurrency());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals("someID", share.getUserId());
        assertEquals(124124, share.getNumberOfShares().intValue());
        assertEquals("myShare", share.getName());
        assertEquals(0.2514, share.getTaxPercentage(), 0);
        option = getAsset(AssetType.OPTION, true);
        assertEquals("isin72131", option.getIsin());
        assertEquals(Currency.USD, option.getCurrency());
        assertEquals(AssetType.OPTION, option.getAssetType());
        assertEquals("someID", option.getUserId());
        assertEquals("myOption", option.getName());
        assertEquals(242424, option.getNumberOfShares().intValue());
        assertEquals(0.3938, option.getTaxPercentage(), 0);
        assertEquals(199.9996, option.getStrikePrice(), 0);
        cash = getAsset(AssetType.CASH, false);
        assertEquals("someID", cash.getUserId());
        assertEquals("myCash", cash.getName());
        assertEquals(921849124, cash.getAmount(), 0);
        assertEquals(0.00214124, cash.getInterestPercentage(), 0);

        cash.setEndDay(timeProvider.getDay());
        dao.saveAsset(cash);
        assertEquals(2, dao.getAssets("someID", false).size());
        assertEquals(3, dao.getAssets("someID", true).size());
        share = getAsset(AssetType.SHARE, true);
        assertEquals("isin1234", share.getIsin());
        assertEquals(Currency.EUR, share.getCurrency());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals("someID", share.getUserId());
        assertEquals("myShare", share.getName());
        assertEquals(124124, share.getNumberOfShares().intValue());
        assertEquals(0.2514, share.getTaxPercentage(), 0);
        option = getAsset(AssetType.OPTION, false);
        assertEquals("isin72131", option.getIsin());
        assertEquals(Currency.USD, option.getCurrency());
        assertEquals(AssetType.OPTION, option.getAssetType());
        assertEquals("someID", option.getUserId());
        assertEquals("myOption", option.getName());
        assertEquals(242424, option.getNumberOfShares().intValue());
        assertEquals(0.3938, option.getTaxPercentage(), 0);
        assertEquals(199.9996, option.getStrikePrice(), 0);

        option.setEndDay(timeProvider.getDay());
        dao.saveAsset(option);
        assertEquals(1, dao.getAssets("someID", false).size());
        assertEquals(3, dao.getAssets("someID", true).size());
        share = getAsset(AssetType.SHARE, true);
        assertEquals("isin1234", share.getIsin());
        assertEquals(Currency.EUR, share.getCurrency());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals("someID", share.getUserId());
        assertEquals("myShare", share.getName());
        assertEquals(124124, share.getNumberOfShares().intValue());
        assertEquals(0.2514, share.getTaxPercentage(), 0);

        share.setEndDay(timeProvider.getDay());
        dao.saveAsset(share);
        assertEquals(0, dao.getAssets("someID", false).size());
        assertEquals(3, dao.getAssets("someID", true).size());
        assertNull(getAsset(AssetType.CASH, false));
        assertNull(getAsset(AssetType.OPTION, false));
        assertNull(getAsset(AssetType.SHARE, false));

        share = getAsset(AssetType.SHARE, true);
        assertEquals("isin1234", share.getIsin());
        assertEquals(Currency.EUR, share.getCurrency());
        assertEquals(AssetType.SHARE, share.getAssetType());
        assertEquals("someID", share.getUserId());
        assertEquals("myShare", share.getName());
        assertEquals(124124, share.getNumberOfShares().intValue());
        assertEquals(0.2514, share.getTaxPercentage(), 0);
        option = getAsset(AssetType.OPTION, true);
        assertEquals("isin72131", option.getIsin());
        assertEquals(Currency.USD, option.getCurrency());
        assertEquals(AssetType.OPTION, option.getAssetType());
        assertEquals("someID", option.getUserId());
        assertEquals("myOption", option.getName());
        assertEquals(242424, option.getNumberOfShares().intValue());
        assertEquals(0.3938, option.getTaxPercentage(), 0);
        assertEquals(199.9996, option.getStrikePrice(), 0);
        cash = getAsset(AssetType.CASH, true);
        assertEquals("someID", cash.getUserId());
        assertEquals(921849124, cash.getAmount(), 0);
        assertEquals(0.00214124, cash.getInterestPercentage(), 0);
    }

    @Test
    public void testGetLatestPrice(){
        assertNull(dao.getLatestPrice("assetId"));
        assertEquals(0, dao.getPrices("assetId", 100_000).size());

        Price price = new Price();
        price.setId("id");
        price.setAssetId("assetId");
        price.setChange(2.24124);
        price.setChangePercentage(21.2141241);
        price.setHolding(100.);
        price.setPrice(66666.789);
        price.setDay(10);
        dao.savePrice(price);

        price = dao.getLatestPrice("assetId");
        assertNotNull(price);
        assertNotNull(price.getId());
        assertEquals("assetId", price.getAssetId());
        assertEquals(100, price.getHolding().intValue());
        assertEquals(2.24124, price.getChange(), 0);
        assertEquals(21.2141241, price.getChangePercentage(), 0);
        assertEquals(66666.789, price.getPrice(), 0);
        assertEquals(10, price.getDay().intValue());

        assertNull(dao.getLatestPrice("assetId2"));
        price = dao.getLatestPrice("assetId");
        assertNotNull(price);
        assertNotNull(price.getId());
        assertEquals("assetId", price.getAssetId());
        assertEquals(100, price.getHolding().intValue());
        assertEquals(2.24124, price.getChange(), 0);
        assertEquals(21.2141241, price.getChangePercentage(), 0);
        assertEquals(66666.789, price.getPrice(), 0);
        assertEquals(10, price.getDay().intValue());

        List<Price> prices = dao.getPrices("assetId", 0);
        assertEquals(1, prices.size());
        price = prices.get(0);
        assertNotNull(price);
        assertNotNull(price.getId());
        assertEquals("assetId", price.getAssetId());
        assertEquals(100, price.getHolding().intValue());
        assertEquals(2.24124, price.getChange(), 0);
        assertEquals(21.2141241, price.getChangePercentage(), 0);
        assertEquals(66666.789, price.getPrice(), 0);
        assertEquals(10, price.getDay().intValue());

        prices = dao.getPrices("assetId", 11);
        assertEquals(0, prices.size());
        prices = dao.getPrices("assetId", 19);
        assertEquals(0, prices.size());
        prices = dao.getPrices("assetId", 10);
        assertEquals(1, prices.size());
        prices = dao.getPrices("assetId", 0);
        assertEquals(1, prices.size());


    }

    private Date date(int year, int month, int day, int hour, int minute){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        try {
            return dateFormat.parse(String.format("%d-%d-%d-%d-%d", year, month, day, hour, minute));
        } catch (ParseException pex){
            throw new RuntimeException(pex);
        }
    }

    private <T extends Asset> T getAsset(AssetType assetType, boolean includeDeleted){
        T asset = null;
        for (Asset nxAsset : dao.getAssets("someID", includeDeleted)) {
            if (nxAsset.getAssetType() == assetType){
                if (asset != null){
                    throw new IllegalArgumentException("Duplicate asset in list of type: " + assetType);
                }
                //noinspection unchecked
                asset =  (T) nxAsset;
            }
        }
        return asset;
    }

}