package com.bitsfromspace.moneytracker.utils;

import java.util.concurrent.Callable;

/**
 * @author chris
 * @since 02-11-15.
 */
public class ExceptionUtils {
    private ExceptionUtils(){

    }

    public static <V> V runUnchecked(Callable<V> callable){
        try {
            return callable.call();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
