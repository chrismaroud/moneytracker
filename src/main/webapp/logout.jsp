<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
  response.sendRedirect(
          UserServiceFactory
                  .getUserService()
                  .createLogoutURL(request.getRequestURI().replaceFirst("logout.jsp", "index.jsp"))
  );

%>