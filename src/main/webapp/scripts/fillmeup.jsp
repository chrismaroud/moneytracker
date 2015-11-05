<%@ page import="com.bitsfromspace.moneytracker.model.Asset" %>
<%@ page import="com.bitsfromspace.moneytracker.model.AssetType" %>
<%@ page import="com.bitsfromspace.moneytracker.model.Dao" %>
<%@ page import="com.bitsfromspace.moneytracker.model.Price" %>
<%@ page import="com.bitsfromspace.moneytracker.model.appengine.AppEngineDao" %>
<%@ page import="com.bitsfromspace.moneytracker.utils.TimeProvider" %>
<%@ page import="com.bitsfromspace.moneytracker.utils.TimeProviderImpl" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.util.UUID" %>
<%
    TimeProvider timeProvider = new TimeProviderImpl();

    String userId = request.getParameter("userId");
    if (userId == null){
        throw new IllegalArgumentException("You must specify param userId, see: http://localhost:8888/_ah/admin/datastore?kind=User");
    }

    Dao dao = new AppEngineDao();
    List<Asset> assets = dao.getAssets(userId, false);
    if (assets.isEmpty()){
        throw new IllegalArgumentException("This user doesn't have any assets, idiot..");
    }

    Random random = new Random();
    int startDay = timeProvider.getDay() - 60;
    for (int day = startDay; day <= timeProvider.getDay(); day++){
        double lastPrice = 0;
        for (Asset asset : assets){
            Price price = new Price();
            price.setAssetId(asset.getId());
            price.setDay(day);
            price.setHolding(asset.getAssetType() == AssetType.CASH ? asset.getAmount().intValue() : asset.getNumberOfShares());
            price.setId(UUID.randomUUID().toString());
            price.setPrice(random.nextInt(10000) / 100d);
            if (lastPrice == 0){
                price.setChange(0d);
                price.setChangePercentage(0d);
            } else {
                price.setChange(price.getPrice() - lastPrice);
                price.setChangePercentage((price.getPrice() - lastPrice) / lastPrice);
            }
            lastPrice = price.getPrice();
            dao.savePrice(price);
        }
    }

%>
