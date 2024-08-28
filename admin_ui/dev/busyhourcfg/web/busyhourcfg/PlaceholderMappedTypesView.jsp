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
<div id='placeholder_table_title'>Placeholder&nbsp;<%=currentPlaceholder.getBhtype()%>&nbsp;Mapped&nbsp;Types:</div>
<form id="PlaceholderMappedTypesView" action="PlaceholderMappedTypesEdit" method="POST" onSubmit="return filterFields();">
<table id="placeholderdetail_table">
	<tr>
		<th>Target type</th>
		<th>Enable</th>
	</tr>
<%
int count = 0;
for (MappedType mappedType : currentPlaceholder.getMappedTypes()) {
  final String ref = "#";
  out.println("  <tr>");
  out.println("    <td><textarea name=\"targettype." + count + "\" rows=\"1\" cols=\"30\" readonly>" + Utils.replaceNull(mappedType.getBhTargettype()) + "</textarea></td>");
  out.println("    <td><input type=\"checkbox\" name=\"enabled." + count + "\" value=\""+ (mappedType.getEnabled() ? "checked": "unchecked") +"\" " + (mappedType.getEnabled() ? "checked": "") + "" + (currentPlaceholder.isProduct() ? " disabled" : "") + "></td>");		
  out.println("  </tr>");
  count++;
}
%>
	
</table>
<div>
<input type="hidden" name="count" value="" />
<table id="button_area">
	<tr>
		<td>
			<input type="submit" name="cancel" value="Cancel" />
			<input type="<%=(currentPlaceholder.isProduct() ? "hidden" : "submit")%>" name="save" value="OK" />
		</td>
</table>
</div>
</form>
</div>
</div>
</body>
</html>