package com.bitsfromspace.moneytracker.model;

/**
 * @author chris
 * @since 30-10-15.
 */
public class Asset implements Verifiable {

    private String id;
    private String userId;
    private String name;
    private AssetType assetType;
    private Currency currency;
    private Integer startDay;
    private Integer endDay;
    private Double interestPercentage;
    private Double amount;
    private Double strikePrice;
    private String isin;
    private Double numberOfShares;
    private Double taxPercentage;

    private Double latestPrice;
    private Double latestPriceDay;

    @Override
    public void verify(){

        notNull("assetType", assetType);
        notNull("id", id);
        notNull("userId", userId);
        notNull("name", name);
        notNull("currency", currency);
        notNull("startDay", startDay);
        switch (assetType){
            case CASH:
                notNull("interestPercentage", interestPercentage);
                notNull("amount", amount);
                break;
            case OPTION:
                notNull("strikePrice", strikePrice);
            case SHARE://intentional fallthrough
                notNull("isin", isin);
                notNull("numberOfShares", numberOfShares);
                notNull("taxPercentage", taxPercentage);
                break;
            default:throw new IllegalArgumentException("No scuh asset type: " + assetType);
        }
    }

    private void notNull(String propertyName, Object value){
        if (value == null){
            throw new NullPointerException("Property '" + propertyName + "' required for asset type " + assetType);
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }

    public Integer getEndDay() {
        return endDay;
    }

    public void setEndDay(Integer endDay) {
        this.endDay = endDay;
    }

    public Double getInterestPercentage() {
        return interestPercentage;
    }

    public void setInterestPercentage(Double interestPercentage) {
        this.interestPercentage = interestPercentage;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Double getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(Double numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public Double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }
}
