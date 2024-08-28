<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.crystaldecisions.sdk.framework.CrystalEnterprise" %>
<%@ page import="com.crystaldecisions.sdk.framework.ISessionMgr" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ include file="/fragments/PageHeader.jspf" %>

<body onload = 'detectBrowser()'>
<div id="main">

<%@ include file="/fragments/Header.jspf" %>

<div id="left_column_first_page">
</div>

<div id="main_column">

<%

	if (request.getAttribute("login_error") != null) {
	  %> 
	  <div class="error"> <%= request.getAttribute("login_error") %> </div> 
	  <%
	}

	if (request.getAttribute("message") != null) {
  		out.print(request.getAttribute("message"));
	}

%>
<form method="POST" name = 'loginform' action='LoginPage' onSubmit="return filterFields();">
<input type="hidden" name = 'browserstatusmessage' value ="">
<input type="hidden" name = 'browserversionmessage' value ="">
<div id="whole_frame">
<table id="login_table">
	<tr>
		<td class="description">System:</td>
		<td class="value"><input type="text" name="cms"></td>
	</tr>
	<tr>
		<td class="description">User Name:</td>
		<td class="value"><input type="text" name="username"></td>
	</tr>
	<tr>
		<td class="description">Password:</td>
		<td class="value"><input type="password" name="password"></td>
	</tr>
	<tr>
		<td class="description">Authentication:</td>
		<td class="value">
			<select name="authtype">
<% 

final ISessionMgr sessionManager = CrystalEnterprise.getSessionMgr();
String[] installedAuthIDs = sessionManager.getInstalledAuthIDs(); 
boolean selected = true; 

for (String installedAuthID : installedAuthIDs) { 
	String installedAuthName = installedAuthID.substring(3); 
	out.print("<option value='" + installedAuthID + (selected ? "' SELECTED>" : "'>") + installedAuthName + "</option>");
	selected = false; 
} 

%>
			</select>
		</td>
	</tr>	
	<tr>
		<td colspan="2"><center><input type="submit" value="Login"></center></td>
	</tr>
</table>
</div>
</form>
</div>
</div>
</body>
</html>