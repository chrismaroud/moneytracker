package com.bitsfromspace.moneytracker.web.rest.appengine;

import com.bitsfromspace.moneytracker.web.rest.UserProvider;
import com.google.appengine.api.users.User;
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
        User user = UserServiceFactory.getUserService().getCurrentUser();
        return user == null ? null : user.getEmail();
    }
}
