package com.bitsfromspace.moneytracker.web.rest;

import javax.servlet.ServletRequest;

/**
 * @author chris
 * @since 27-10-15.
 */
public interface UserProvider {

    /* Returns userID or throws exception */
    String getUserId(ServletRequest request);
}
