<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="java.util.*" %>

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
                    <li><a href="index.jsp"><%=res.getString("home")%></a></li>
                    <li class="active"><a href="form.jsp"><%=res.getString("start")%></a></li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container">
        <div class="container-fluid">
            <form id="translateForm" method="post" class="form-horizontal" action="TranslationServlet" enctype="multipart/form-data" role="form">
            <div class="form-group">
        		<label for="file"><%=res.getString("java")%></label>
        		<input type="file" name="file"/>
        		<p class="help-block"><%=res.getString("java_help")%></p>
        	</div>
        	<div class="form-group">
        		<label for="source_language" class="control-label"><%=res.getString("source_language")%></label> 
        		<select name="source_language" class="form-control" required>
    			<option value="en-US" selected><%=res.getString("english")%></option>
    			</select>
    			<p class="help-block"><%=res.getString("source_language_help")%></p>
    		</div>
    		<div class="form-group">
    			<label for="target_language" class="control-label"><%=res.getString("target_language")%></label>
    			<select name="target_language" class="form-control" required>
    				<option value="pt-BR"><%=res.getString("portuguese")%></option>
    				<option value="fr"><%=res.getString("french")%></option>
    				<option value="es"><%=res.getString("spanish")%></option>
    			</select>
    			<p class="help-block"><%=res.getString("target_language_help")%></p>
   			</div>
   			<div class="form-group">
        	 	<button type="submit" class="btn btn-default"><%=res.getString("submit")%></button>
        	</div>
    		</form>
        </div>
 	</div>
 	
 	<div class="container">
 		<div class="container-fluid">
 			<form method="get" class="form-horizontal" action="TranslationServlet" role="form">
 			<div class="form-group">
 				<input type="text" name="download" value="true" hidden="true">
				<button id="downloadButton" type="submit" class="btn btn-default" disabled><%=res.getString("download")%></button>
 			</div>
 			</form>
 		</div>
 	</div>
        
   	<div class="container">
    	<table data-toggle="table" id="translation">
     		<thead>
            <tr>
                <th data-field="language"><%=res.getString("language")%></th>
                <th data-field="key"><%=res.getString("key")%></th>
                <th data-field="value"><%=res.getString("value")%></th>
            </tr>
        	</thead>
        	<tbody>
        	</tbody>
      	</table>
	</div>
        
<script type="text/javascript">
<%@ include file="js/jquery-1.11.1.min.js" %>
<%@ include file="js/jquery.form.min.js" %>
<%@ include file="js/bootstrap.min.js" %>
<%@ include file="js/bootstrap-table.min.js" %>
<%@ include file="js/eventsource.min.js" %>
</script>

<script> 
	// wait for the DOM to be loaded 
	$(document).ready(function() { 
		var options = {
			success: showResponse,
			error: showError,
			dataType: 'json'
		};
	
    	// bind translationForm with the options 
    	$('#translateForm').ajaxForm(options); 
    }); 
    
    // This is called after we post the form data and uploaded the file
	function showResponse(responseText, statusText, xhr, $form)  {
		setupEventSource();
	}
	
	// This gets called if there is a problem with uploading the file
	function showError(response, status, err) {
		alert('<%=res.getString("java_error")%>');
	}
	
	function setupEventSource() {
        	if (typeof(EventSource) !== "undefined") {
        		$("#downloadButton").attr("disabled", "disabled");
        	
        		// clear the table
        		$('#translation tbody').empty();
            	$('#translation').bootstrapTable('load', []);
            	
            	// get the translated strings as server side events
          		var source = new EventSource("TranslationServlet?translate=true");

				// add each new translated string to the table
          		source.onmessage = function(event) {
            		var translation = JSON.parse(event.data);
            		$('#translation').bootstrapTable('append', [translation]);
          		};
          		
          		source.onerror = function(event) {
          			var message;
    				switch( event.target.readyState ){
        				case EventSource.CONNECTING:
            			message = '<%=res.getString("reconnect")%>';
            			break;
        				case EventSource.CLOSED:
            			message = '<%=res.getString("closed")%>';
            			break;
    				}
    				alert(message);
          		};

				// There are no more strings to add to the table
          		source.addEventListener('finished', function(event) {
            		source.close();
            		$('#downloadButton').removeAttr("disabled");
          		}, false);
        	}
        	else {
          		alert('<%=res.getString("sse_error")%>');
        	}
        	return false;
    }
</script>
</body>
</html>