package com.bitsfromspace.moneytracker;

/**
 * @author chris
 * @since 13-10-15.
 */
public class MoneyTrackerException extends RuntimeException {
    public MoneyTrackerException() {
    }

    public MoneyTrackerException(String message) {
        super(message);
    }

    public MoneyTrackerException(String message, Throwable cause) {
        super(message, cause);
    }

}
