<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Utils"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="java.util.regex.Matcher"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/busyhourcfg/fragments/PageHeader.jspf"%>
<%@ include file="/busyhourcfg/fragments/Header.jspf"%>
<%@ include file="/busyhourcfg/fragments/CurrentValues.jspf"%>
<%@ include file="/busyhourcfg/fragments/LeftMenu.jspf"%>

<body>
<div id="main">

<div id="left_column">
<%
  TechPack tmpTechPack = currentTechPack;
  TargetTechPack tmpTargetTechPack = null;
  BusyhourSupport tmpBusyhourSupport = null;
  out.println(getLeftMenu(tmpTechPack, tmpTargetTechPack, tmpBusyhourSupport));
%>
</div>

<div id="main_column">
<form id="BusyhourView" action="BusyhourView" method="POST">
<%
  if (request.getAttribute("errormessage") != null) {
    out.println("<div class='error_message'>" + request.getAttribute("errormessage") + "</div>");
  }

  if (request.getAttribute("infomessage") != null) {
    out.println("<div class='info_message'>" + request.getAttribute("infomessage") + "</div>");
  }
%>
<div>
<table id="helptext">
	<tr>
		<td>After custom placeholders have been modified, the changes will not be activated unless Update Busy Hours has been performed. 
		The Update Busy Hours button is enabled if there are pending changes for this Tech Pack. 
		Clicking the Update Busy Hours button starts the update process which may take a long time.  Please wait until the process is finished.</td>
	</tr>
</table>
<table id="button_area">
	<tr>
		<%
		  out.println("<td><input type='submit' name='cancel' value='<- Tech Pack Selection' /></td>");
		  if (currentTechPack.getModified() > 0) {
		    out.println("<td><input type='submit' name='activate' value='Update Busy Hours' /></td>");
		  } else {
		    out.println("<td><input type='submit' name='activate' value='Update Busy Hours' disabled='disabled' /></td>");
		  }
		%>
	</tr>
</table>
</div>
</form>
</div>

</div>
</body>
</html>