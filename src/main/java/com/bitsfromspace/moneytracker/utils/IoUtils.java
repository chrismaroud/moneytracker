package com.bitsfromspace.moneytracker.utils;

import java.io.InputStream;
import java.util.concurrent.Callable;

import static com.bitsfromspace.moneytracker.utils.ExceptionUtils.runUnchecked;

/**
 * @author chris
 * @since 02-11-15.
 */
public class IoUtils {

    private IoUtils(){

    }

    public static String readFully(final InputStream in){
        return runUnchecked(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder stringBuilder = new StringBuilder();
                byte[] buffer = new byte[64 * 1024];
                for (int read = in.read(buffer); read != -1; read = in.read(buffer)){
                    stringBuilder.append(new String(buffer, 0, read));
                }
                return stringBuilder.toString();
            }
        });

    }
}
