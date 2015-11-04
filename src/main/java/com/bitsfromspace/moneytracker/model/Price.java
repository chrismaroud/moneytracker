package com.bitsfromspace.moneytracker.model;

import com.bitsfromspace.moneytracker.utils.DayUtils;

import java.util.Date;

/**
 * @author chris
 * @since 23-10-15.
 */
public class Price implements Verifiable {

    private String id;
    private String assetId;
    private Integer day;
    private Double holding;
    private Double price;
    private Double change;
    private Double changePercentage;

    @Override
    public void verify() {
        notNull("id", id);
        notNull("assetId", assetId);
        notNull("day", day);
        notNull("holding", holding);
        notNull("price", price);
        notNull("change", change);
        notNull("changePercentage", changePercentage);
    }

    private void notNull(String propertyName, Object value) {
        if (value == null) {
            throw new NullPointerException(propertyName + " cannot be null");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Integer getDay() {
        return day;
    }

    public Date getDate(){
        return getDay() == null ? null : DayUtils.getDate(getDay());
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Double getHolding() {
        return holding;
    }

    public void setHolding(Double holding) {
        this.holding = holding;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(Double changePercentage) {
        this.changePercentage = changePercentage;
    }
}
