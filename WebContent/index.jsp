<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="java.util.*"%>


<% 

ResourceBundle res = ResourceBundle.getBundle("com.ibm.translation", 
									request.getLocale());						
%>    

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=res.getString("product")%></title>
<style type="text/css">
<%@ include file="css/bootstrap.min.css" %>
<%@ include file="css/bootstrap-table.min.css" %>
<%@ include file="css/grid.css" %>
</style>

<style type="text/css">
    body{
    	padding-top: 70px;
    }
</style>

</head>
<body>

<nav id="myNavbar" class="navbar navbar-default navbar-inverse navbar-fixed-top" role="navigation">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbarCollapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#"><%=res.getString("product")%></a>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="navbarCollapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="index.jsp"><%=res.getString("home")%></a></li>
                    <li><a href="form.jsp"><%=res.getString("start")%></a></li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container">
        <div class="jumbotron">
            <h1><%=res.getString("product")%></h1>
            <p><%=res.getString("information")%></p>
            <p><a href="form.jsp" class="btn btn-success btn-lg"><%=res.getString("get_started")%></a></p>
        </div>
        
        <hr>
    </div>
 


<script type="text/javascript">
<%@ include file="js/jquery-1.11.1.min.js" %>
<%@ include file="js/bootstrap.min.js" %>
</script>

</body>
</html>