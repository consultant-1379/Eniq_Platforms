<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Utils"%>

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
  out.println(getLeftMenu(currentTechPack, currentTargetTechPack, currentBusyhourSupport));
%>
</div>

<div id="main_column">
<%
if (request.getAttribute("errormessage") != null) {
  out.println("<div class='error_message'>" + request.getAttribute("errormessage") + "</div>");
}

if (request.getAttribute("infomessage") != null) {
  out.println("<div class='info_message'>" + request.getAttribute("infomessage") + "</div>");
}
%>
<div id='placeholder_table_title'>Key Source Table:</div>
<form id="PlaceholderRankKeySourceView" action="PlaceholderRankKeySourceEdit" method="POST" onSubmit="return filterFields();">
<table id='placeholder_table'>
	<tr>
		<th>Source Table</th>
	</tr>
<%
final String ref = "#";
out.println("  <tr>");
out.println("<td><select	name=\"source_table\" id=\"source_table\" " + (currentPlaceholder.isProduct() ? "disabled" : "") + ">");
int count = 0;
for (Source source : currentPlaceholder.getSources()) {
	    out.println("<option value=\""+ source.getTypename() + "\">" + source.getTypename() + "</option>");
}
  out.println("</select></td>");
  out.println("  </tr>");
  count++;
%>
	
</table>
<div>
<input type="hidden" name="count" value="" />
<table id="button_area">
	<tr>
		<td>
			<input type="submit" name="cancel" value="<- Previous page" />
			<input type="<%=(currentPlaceholder.isProduct() ? "hidden" : "submit")%>" name="save" value="Select" />
		</td>
</table>
</div>
</form>
</div>
</div>
</body>
</html>