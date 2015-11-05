<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>

<%
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Query q = new Query("Asset");
    for (Entity entity : datastoreService.prepare(q).asIterable()){
        entity.setProperty("endDay", 0);
        datastoreService.put(entity);
    }
%>