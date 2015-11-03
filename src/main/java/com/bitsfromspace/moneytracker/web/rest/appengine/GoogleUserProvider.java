package com.bitsfromspace.moneytracker.web.rest.appengine;

import com.bitsfromspace.moneytracker.web.rest.UserProvider;
import com.google.appengine.api.users.UserServiceFactory;

import javax.inject.Singleton;
import javax.servlet.ServletRequest;

/**
 * @author chris
 * @since 29-10-15.
 */
@Singleton
public class GoogleUserProvider implements UserProvider {

    @Override
    public String getUserId(ServletRequest request) {
        return UserServiceFactory.getUserService().getCurrentUser().getUserId();
    }
}
