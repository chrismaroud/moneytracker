package com.bitsfromspace.moneytracker.model;

/**
 * @author chris
 * @since 23-10-15.
 */
public class User implements Verifiable{

    private String id;
    private Currency defaultCurrency;

    @Override
    public void verify(){
        notNull("id", id);
        notNull("defaultCurrency", defaultCurrency);
    }

    private void notNull(String propertyName, Object value){
        if (value == null){
            throw new NullPointerException(propertyName + " cannot be null");
        }
    }

    public String getSymbol(){
        return defaultCurrency == null ? null : defaultCurrency.getSymbol();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}