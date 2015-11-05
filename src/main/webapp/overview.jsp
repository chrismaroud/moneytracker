<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
    UserService userService = UserServiceFactory.getUserService();

    if (request.getUserPrincipal() == null) {
        response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
    }
    String logoutUrl = userService.createLogoutURL(request.getRequestURI().replaceFirst("app.jsp", "index.jsp"));
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

    <!-- Font awesome -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">


    <!-- Angular -->
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.7/angular.min.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.1/ui-bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.1/ui-bootstrap-tpls.min.js"></script>

    <!-- http://assisrafael.github.io/angular-input-masks/ -->
    <script src="angular-input-masks-standalone.js"></script>

    <!-- Chart.js -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/1.0.2/Chart.min.js"></script>
    <link rel="stylesheet" href="angular-chart.min.css">
    <script src="angular-chart.min.js"></script>

    <!-- Sortable table -->
    <script src="sortable.min.js"></script>
    <link rel="stylesheet" href="sortable-theme-bootstrap.css"/>

    <!-- Custom styles for this template -->
    <link href="moneytracker.css" rel="stylesheet">
    <link rel="shortcut icon" href="favicon.ico"/>


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
            <a class="navbar-brand" href="#">
                <img style="max-width:100px; margin-top: -7px;"
                     src="logo.png">
                MoneyTracker</a>

        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <%--<ul class="nav navbar-nav">--%>
            <%--<li class="active"><a href="#">Overview</a></li>--%>
            <%--</ul>--%>
            <div class="navbar-right">
                <button type="button" class="btn btn-default navbar-btn"
                        onclick="location.href='<%=logoutUrl%>';"><i class="glyphicon glyphicon-user"></i> Sign out
                </button>
            </div>
        </div>
        <!--/.nav-collapse -->

    </div>
</nav>

<div class="container-fluid" style="margin-top: 25px;"
     ng-controller="OverviewController">

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
                    ng-click="selectCurrency(selectedCurrency.value)">SAVE
            </button>
        </div>
    </script>
    <script type="text/ng-template" id="noAssets.html">
        <div class="modal-header">
            <h4 class="modal-title">No assets found</h4>
        </div>
        <div class="modal-body">
            You haven't defined any assets yet. <br/><br/> Let's create a couple ...
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" type="button" onclick="location.href='assets.jsp';">OK</button>
        </div>
    </script>


    <uib-alert type="danger" close="clearError()" ng-show="error">
        <strong>Oops!</strong> Something went wrong
        <pre>{{error}}</pre>
    </uib-alert>

    <uib-alert type="warning" close="clearWarning()" ng-show="warning">
        <strong>Note</strong> ...
        <p>{{warning}}</p>
    </uib-alert>

    <div id="spinner" class="overlay" ng-show="loading">
        <img src="spinner.gif" class="ajax-loader"/>
    </div>

    <ul class="nav nav-tabs">
        <li role="presentation" class="active"><a href="#">Overview</a></li>
        <li role="presentation"><a href="assets.jsp">My assets</a></li>
        <li role="presentation"><a href="#">Analytics</a></li>
    </ul>

    <div class="row" style="margin-top: 25px;" ng-show="overview">
        <div class="col-sm-4">
            <a class="btn btn-lg btn-success" href="#" style="width: 100%;margin: 10px;">
                <i class="fa fa-thumbs-up fa-5x pull-left"></i>

                <h1>{{overview.highestPrice | currency : user.symbol : 2}}</h1><br>
                <small ng-hide="!overview.highestPriceDate">Best day @ {{overview.highestPriceDate | date}}</small>
                <br>
            </a>
        </div>
        <div class="col-sm-4">
            <a class="btn btn-lg btn-info" href="#" style="width: 100%;margin: 10px;">
                <i class="fa fa-flag fa-5x pull-left"></i>

                <h1>{{overview.currentPrice | currency : user.symbol : 2}}</h1><br>
                <small>Your current capital</small>
                <br>
            </a>
        </div>
        <div class="col-sm-4">
            <a class="btn btn-lg btn-danger" href="#" style="width: 100%;margin: 10px;">
                <i class="fa fa-thumbs-down fa-5x pull-left"></i>

                <h1>{{overview.lowestPrice | currency : user.symbol : 2}}</h1><br>
                <small ng-hide="!overview.lowestPriceDate">Worst day @ {{overview.lowestPriceDate | date}}</small>
                <br>
            </a>
        </div>
    </div>

    <h3 ng-show="overview">Asset Allocation & Performance</h3>

    <div class="row" ng-show="overview">

        <div class="col-sm-6">
            <small>Asset distribution (%)</small>
            <canvas id="doughnut"
                    class="chart chart-doughnut"
                    options="overview.allocationChart.options"
                    chart-legend="true"
                    chart-data="overview.allocationChart.values"
                    chart-labels="overview.allocationChart.labels"
            >
            </canvas>
        </div>
        <div class="col-sm-6">
            <small>1-Month performance (%)</small>
            <canvas id="bar" class="chart chart-bar"
                    options="overview.performanceChart.options"
                    chart-data="overview.performanceChart.values"
                    chart-labels="overview.performanceChart.labels"
            >
            </canvas>

        </div>


    </div>


    <h3 ng-show="overview">Current Asset Values</h3>
    <table class="table table-hover sortable-theme-bootstrap" ng-show="overview" data-sortable>
        <caption>Based on most recent prices</caption>

        <thead>
        <tr>
            <th>Asset</th>
            <th>Currency</th>
            <th>Value</th>
            <th>1-Month yield</th>
            <th>Total yield</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-hide="overview.assets.length">
            <td colspan="3">
                <small>No asset prices found?</small>
            </td>
        </tr>
        <tr ng-repeat="asset in overview.assets">
            <td>{{asset.name}}</td>
            <td>{{asset.currencySymbol}}</td>
            <td>{{asset.price | currency : user.symbol : 2}}</td>
            <td>
                <span ng-class="{'label label-danger' : asset.oneMonthYieldPercentage < 0, 'label label-success' : asset.oneMonthYieldPercentage >= 0}">
                    {{asset.oneMonthYieldPercentage * 100 | number : 2}}%
                </span>
            </td>
            <td>
                <span ng-class="{'label label-danger' : asset.totalYieldPercentage < 0, 'label label-success' : asset.totalYieldPercentage >= 0}">
                    {{asset.totalYieldPercentage * 100 | number : 2}}%
                </span>
            </td>
        </tr>
        </tbody>
    </table>
</div>
<!-- /.container -->


</body>
</html>

