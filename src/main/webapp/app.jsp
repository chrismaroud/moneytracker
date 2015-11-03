<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
    UserService userService = UserServiceFactory.getUserService();

    if (request.getUserPrincipal() == null) {
        response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
    }
    String logoutUrl = userService.createLogoutURL(request.getRequestURI().replaceFirst("app.jsp", " index.jsp"));
%>
<!DOCTYPE html>
<html lang="en" ng-app="MoneyTracker">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">


    <title>MoneyTracker</title>

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">


    <!-- Angular -->
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.7/angular.min.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.1/ui-bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.1/ui-bootstrap-tpls.min.js"></script>


    <!-- Custom styles for this template -->
    <link href="moneytracker.css" rel="stylesheet">

    <script src="app.js"></script>


    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>

<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">MoneyTracker</a>

        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <%--<ul class="nav navbar-nav">--%>
            <%--<li class="active"><a href="#">Overview</a></li>--%>
            <%--</ul>--%>
            <div class="navbar-right">
                <button type="button" class="btn btn-default navbar-btn glyphicon glyphicon-user"
                        onclick="location.href='<%=logoutUrl%>';"> Sign out
                </button>
            </div>
        </div>
        <!--/.nav-collapse -->

    </div>
</nav>

<div class="container" style="margin-top: 25px;" ng-controller="OverviewController as overview">

    <script type="text/ng-template" id="selectCurrencyContent.html">
        <div class="modal-header">
            <h4 class="modal-title">You're new around here!</h4>
        </div>
        <div class="modal-body">
            Welcome to MoneyTracker. Select your <i>default</i> currency to get started.
            <select ng-model="selectedCurrency" required="required" class="form-control"
                    ng-options="currency as currency.label for currency in currencies">
            </select>

        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" type="button"
                    ng-disabled="selectedCurrency.value == undefined"
                    ng-click="selectCurrency(selectedCurrency.value)">SAVE</button>
        </div>
    </script>

    <uib-alert type="danger" close="clearError()" ng-show="error">
        <strong>Oops!</strong> Something went wrong while syncing with server
        <pre>{{error}}</pre>
    </uib-alert>

    <div id="spinner" class="overlay" ng-show="loading">
        <img src="spinner.gif" class="ajax-loader"/>
    </div>

    <ul class="nav nav-tabs">
        <li role="presentation" class="active"><a href="#">Overview</a></li>
        <li role="presentation"><a href="#">My assets</a></li>
        <li role="presentation"><a href="#">Analytics</a></li>
    </ul>

    <div class="row" ng-show="overview">
        <div class="col-md-4 col-xs-12">
            <h3>Highest recorded capital</h3>
            <strong>$/€ {{overview.highestPrice}}</strong>

            <p>(recorded on {{overview.highestPriceDate}})</p>
        </div>
        <div class="col-md-4 col-xs-12">
            <h3>Last recorded capital</h3>
            <strong>$/€ {{overview.currentPrice}}</strong>

            <p>(recorded on {{overview.currentPriceDate}})</p>
        </div>
        <div class="col-md-4 col-xs-12">
            <h3>Lowest recorded capital</h3>
            <strong>$/€ {{overview.lowestPrice}}</strong>

            <p>(recorded on {{overview.lowestPriceDate}})</p>
        </div>
    </div>
</div>
<!-- /.container -->


</body>
</html>

