<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/busyhourcfg/fragments/PageHeader.jspf"%>
<%@ include file="/busyhourcfg/fragments/Header.jspf"%>



<body>
<div id="main">

<div id="left_column_first_page">
</div>

<div id="main_column">
<%
if (request.getAttribute("errormessage") != null) {
  out.print("<div class='error_message'>" + request.getAttribute("errormessage") + "</div>");
} else {
  out.print("<div class='error_message'>An error occurred which prevented the action from completing correctly.</div>");
}

if (request.getAttribute("error") != null) {
  out.println("<div class='error'>"+ request.getAttribute("error") +"</div>");
}

%>
</div>
</div>
</body>
</html>