package com.bitsfromspace.moneytracker.model;

/**
 * @author chris
 * @since 13-10-15.
 */
public enum Currency {
    USD("$"), EUR("\u20ac");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol(){
        return symbol;
    }
}
