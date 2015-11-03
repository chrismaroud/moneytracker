package com.bitsfromspace.moneytracker.web.rest;

import com.bitsfromspace.moneytracker.model.Asset;
import com.bitsfromspace.moneytracker.model.Dao;
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
@Path("asset")
public class AssetService {
    private final Dao dao;
    private final UserProvider userProvider;
    private final TimeProvider timeProvider;

    @Inject
    public AssetService(Dao dao, UserProvider userProvider, TimeProvider timeProvider) {
        this.dao = dao;
        this.userProvider = userProvider;
        this.timeProvider = timeProvider;
    }

    @GET
    @Produces("application/json")
    public List<Asset> getAssets(@Context HttpServletRequest request){
        final String userId = userProvider.getUserId(request);
        return dao.getAssets(userId, false);
    }


    @POST
    @Consumes("application/json")
    public void insert(@Context HttpServletRequest request, Asset asset){
        final String userId = userProvider.getUserId(request);
        if (asset.getId() != null){
            throw new IllegalArgumentException("Cannot save new asset with ID set");
        }
        asset.setId(UUID.randomUUID().toString());
        asset.setUserId(userId);
        asset.setStartDay(timeProvider.getDay());
        dao.saveAsset(asset);
    }
    @PUT
    @Consumes("application/json")
    public void update(@Context HttpServletRequest request, Asset asset){
        final String userId = userProvider.getUserId(request);
        if (userId == null || ! userId.equals(asset.getUserId())){
            throw new IllegalStateException("Unauthorized");
        }
        dao.saveAsset(asset);
    }
    @DELETE
    @Path("{assetId}")
    public void delete(@Context HttpServletRequest request, @PathParam("assetId")String assetId){
        Asset asset = dao.getAsset(assetId);
        if (asset == null){
            throw new IllegalArgumentException("No such asset #" + assetId);
        }
        final String userId = userProvider.getUserId(request);
        if (userId == null || ! userId.equals(asset.getUserId())){
            throw new IllegalStateException("Unauthorized");
        }
        asset.setEndDay(timeProvider.getDay());
        dao.saveAsset(asset);
    }
//
//    @PUT
//    @Path("cash")
//    @Consumes("application/json")
//    public void updateCash(@Context HttpServletRequest request, Cash cash){
//        final String userId = userProvider.getUserId(request);
//        dao.updateCash(userId, cash.getId(), cash.getName(), cash.getCurrency(), cash.getAmount(), cash.getInterestPercentage());
//    }
//    @DELETE
//    @Path("cash")
//    @Consumes("application/json")
//    public void deleteCash(@Context HttpServletRequest request, Cash cash){
//        final String userId = userProvider.getUserId(request);
//        dao.deleteCash(userId, cash.getId());
//    }
}
