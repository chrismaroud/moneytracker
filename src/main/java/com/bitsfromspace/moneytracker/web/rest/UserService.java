package com.bitsfromspace.moneytracker.web.rest;

import com.bitsfromspace.moneytracker.model.Asset;
import com.bitsfromspace.moneytracker.model.Currency;
import com.bitsfromspace.moneytracker.model.Dao;
import com.bitsfromspace.moneytracker.model.User;
import com.bitsfromspace.moneytracker.utils.TimeProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.UUID;

/**
 * @author chris
 * @since 30-10-15.
 */
@Singleton
@Path("user")
public class UserService {
    private final Dao dao;
    private final UserProvider userProvider;

    @Inject
    public UserService(Dao dao, UserProvider userProvider) {
        this.dao = dao;
        this.userProvider = userProvider;
    }

    @GET
    @Produces("application/json")
    public User getUser(@Context HttpServletRequest request) {
        final String userId = userProvider.getUserId(request);
        return dao.getUser(userId);
    }


    @POST
    @Consumes("application/json")
    public void insert(@Context HttpServletRequest request, User user) {
        final String userId = userProvider.getUserId(request);
        if (userId == null) {

            throw new IllegalStateException("Not authorized");
        }
        user.setId(userId);
        dao.saveUser(user);
    }
}