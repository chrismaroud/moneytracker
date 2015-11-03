<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
    UserService userService = UserServiceFactory.getUserService();

    String loginUrl = null;

    if (request.getUserPrincipal() != null){
        response.sendRedirect("overview.jsp");
    } else {
        loginUrl = userService.createLoginURL(request.getRequestURI());
    }


%>
<!DOCTYPE html>
<html lang="en">
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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.1/ui-bootstrap-tpls.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.1/ui-bootstrap.min.js"></script>

    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.7/angular.min.js"></script>

    <!-- Custom styles for this template -->
    <link href="moneytracker.css" rel="stylesheet">


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
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">MoneyTracker</a>

        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Home</a></li>
            </ul>
            <div class="navbar-right">
            <button type="button" class="btn btn-default navbar-btn" onclick="location.href='<%=loginUrl%>';">Sign in</button>
            <button type="button" class="btn btn-default navbar-btn" onclick="location.href='<%=loginUrl%>';">Register</button>
            </div>
        </div><!--/.nav-collapse -->

    </div>
</nav>

<div class="container">

    <div class="jumbotron maindiv">
        <h1>Welcome to MoneyTracker</h1>
        <p>Tracks all your assets and sends a daily summary over email for free!</p>
        Supported asset types:

            <br/>&nbsp;<span class="glyphicon glyphicon-usd">Saving accounts</span>
            <br/>&nbsp;<span class="glyphicon glyphicon-stats">Shares</span>
            <br/>&nbsp;<span class="glyphicon glyphicon-equalizer">ETFs</span>
            <br/>&nbsp;<span class="glyphicon glyphicon-signal">Mutual funds</span>
            <br/>&nbsp;<span class="glyphicon glyphicon-random">Options</span>
               <h5>All assets are automatically converted to your default currency.</h5>

        <p><a class="btn btn-primary btn-lg glyphicon glyphicon-user" href="<%=loginUrl%>" role="button"> &nbsp; Sign in / Register</a> <sub>(Using Google)</sub></p>
    </div>

</div><!-- /.container -->x`


</body>
</html>

