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

<div class="container" style="margin-top: 25px;" ng-controller="AssetController">

    <script type="text/ng-template" id="editAsset.html">
        <div class="modal-header">
            <h4 class="modal-title">Edit Asset</h4>
        </div>
        <div class="modal-body">
            <form>
                <div class="form-group">
                    <label for="assetType">Asset type</label>
                    <select id="assetType" ng-model="asset.assetType">
                        <option value="CASH">Cash / Savings</option>
                        <option value="SHARE">Share</option>
                        <option value="OPTION">Option</option>
                    </select>
                </div>
                <div class="form-group" ng-show="asset.assetType != null">
                    <label for="assetName">Description</label>
                    <input id="assetName" type="text" class="form-control" placeholder="Description"
                           ng-model="asset.name" min="3" required>
                </div>
                <div class="form-group" ng-show="asset.assetType != null">
                    <label for="assetCurrency">Currency</label>
                    <select id="assetCurrency" ng-model="asset.currency" required="required" class="form-control"
                            ng-change="$locale.NUMBER_FORMATS.CURRENCY_SYM = currency2.symbol"
                            ng-options="currency2.value as currency2.label for currency2 in currencies">
                    </select>
                </div>
                <div class="form-group" ng-show="asset.assetType == 'CASH'">
                    <label for="assetAmount">Saving amount</label>
                    <input id="assetAmount" type="text" class="form-control" placeholder="Saving amount"
                           ng-model="asset.amount" required ui-money-mask="2">
                </div>
                <div class="form-group" ng-show="asset.assetType == 'CASH'">
                    <label for="assetInterestPercentage">Interest percentage</label>
                    <input id="assetInterestPercentage" type="text" class="form-control"
                           placeholder="Annual interest percentage (%)" ng-model="asset.interestPercentage" required
                           ui-percentage-mask="2">
                </div>
                <div class="form-group" ng-show="asset.assetType == 'SHARE' || asset.assetType == 'OPTION'">
                    <label for="assetIsin">Bloomberg code</label>
                    <input id="assetIsin" type="text" class="form-control"
                           placeholder="Bloomberg code (e.g. F:US, AADVP6B:LX)" ng-model="asset.isin" required>
                </div>
                <div class="form-group" ng-show="asset.assetType == 'SHARE' || asset.assetType == 'OPTION'">
                    <label for="assetNumberOfShares">Quantity</label>
                    <input id="assetNumberOfShares" type="number" class="form-control"
                           placeholder="Number of shares / options" ng-model="asset.numberOfShares" required>
                </div>
                <div class="form-group" ng-show="asset.assetType == 'SHARE' || asset.assetType == 'OPTION'">
                    <label for="assetTaxPercentage">Tax</label>
                    <input id="assetTaxPercentage" type="text" class="form-control"
                           placeholder="Tax rate percentage (e.g. 52%), type 0.00% if not applicable."
                           ng-model="asset.taxPercentage"
                           required ui-percentage-mask="2">
                </div>
                <div class="form-group" ng-show="asset.assetType == 'OPTION'">
                    <label for="assetStrikePrice">Option strike price</label>
                    <input id="assetStrikePrice" type="text" class="form-control" placeholder="Strike price"
                           ng-model="asset.strikePrice" required ui-money-mask="2">
                </div>
            </form>

        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" type="button"
                    ng-disabled="! verifyAsset()"
                    ng-click="saveAsset(selectedCurrency.value)">SAVE
            </button>
            <button class="btn" type="button"
                    ng-click="activeModal.close()">CANCEL
            </button>
        </div>
    </script>

    <uib-alert type="danger" close="clearError()" ng-show="error">
        <strong>Oops!</strong> Something went wrong while syncing with server
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
        <li role="presentation"><a href="overview.jsp">Overview</a></li>
        <li role="presentation" class="active"><a href="#">My assets</a></li>
        <li role="presentation"><a href="#">Analytics</a></li>
    </ul>

    <br/>

    <table class="table table-hover sortable-theme-bootstrap" data-sortable>

        <thead>
        <tr>
            <th>Description</th>
            <th>Type</th>
            <th>&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-hide="assets.length">
            <td colspan="3">
                <small>No assets found</small>
            </td>
        </tr>
        <tr ng-repeat="asset in assets">
            <td>{{asset.name}}</td>
            <td>{{asset.assetType | lowercase}}</td>
            <td>
                <a class="btn btn-danger" href="#" ng-click="deleteAsset(asset)"><i class="fa fa-trash-o fa-lg"></i>
                    Delete</a>
                <a class="btn btn-default btn-sm" href="#" ng-click="editAsset(asset)"><i
                        class="fa fa-pencil fa-fw"></i>Edit</a>

            </td>
        </tr>
        </tbody>
    </table>
    <button ng-click="editAsset()"><i class="glyphicon glyphicon-plus"></i> Add new asset</button>
</div>
<!-- /.container -->


</body>
</html>

