<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Utils"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/busyhourcfg/fragments/PageHeader.jspf"%>
<%@ include file="/busyhourcfg/fragments/Header.jspf"%>
<%@ include file="/busyhourcfg/fragments/CurrentValues.jspf"%>
<%@ include file="/busyhourcfg/fragments/LeftMenu.jspf"%>

<script type="text/javascript">
var validate = 0;
function validate_form(thisform) {
    var flagValidate = true;
	if (validate){
    	with (thisform) {
    		var i=0;
    		var ii=0;
    		for (i=0;i<<%=currentPlaceholder.getSources().size()%>;i=i+1)
    		{
    		    if (validate_required(typename[i], "Typename can not be empty") == false) {
    		    	  typename[i].focus();
    		  	      flagValidate = false;
    		    }
        		for (ii=i+1;ii<<%=currentPlaceholder.getSources().size()%>;ii=ii+1)
        		{
					if (typename[i].value == typename[ii].value){
	        		    alert("Duplicate value in Typename [" + typename[i].value + "]. Typename must be unique.");
        		    	flagValidate = false;
					}
				}
    		}
    	}
  	}
  	
  	var xssFlag = filterFields();
    return (flagValidate && xssFlag); 
}
</script>
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
  out.println("<div class=\"error_message\">" + request.getAttribute("errormessage") + "</div>");
}

if (request.getAttribute("infomessage") != null) {
  out.println("<div class=\"info_message\">" + request.getAttribute("infomessage") + "</div>");
}
%>
<div id="placeholder_table_title">Placeholder&nbsp;<%=currentPlaceholder.getBhtype()%>&nbsp;Sources:</div>
<form id="PlaceholderSourceView" action="PlaceholderSourceEdit"
	onsubmit="return validate_form(this)" method="POST">
<table id="placeholderdetail_table">
	<tr>
		<th>Typename</th>
		<th></th>
		<th></th>
	</tr>
	<%
int count = 0;
for (Source source : currentPlaceholder.getSources()) {
  final String ref = "#";
  out.println("  <tr>");
  out.println("    <td><textarea id=\"typename\" name=\"typename." + count + "\" rows=\"1\" cols=\"30\" " + (currentPlaceholder.isProduct() ? " readonly " : "") + ">" + Utils.replaceNull(source.getTypename()) + "</textarea>&nbsp;</td>");
  if (currentPlaceholder.isCustom()) {
  	out.println("    <td><button type=\"submit\" name=\"edittypename." + count + "\" value=\"true\" onclick=\"validate=0;\" >...</button></td>");
  	out.println("    <td><input type=\"submit\" name=\"delete." + count + "\" value=\"Delete\"/ onclick=\"validate=0;\" ></td>");
  }
  out.println("  </tr>");
  count++;
}
%>
	<tr>
		<% if (currentPlaceholder.isCustom()) {  %>
		<td colspan="3"><input type="submit" name="add"
			value="New Source" /></td>
		<% } %>
	</tr>

</table>
<div><input type="hidden" name="count" value="" />
<table id="button_area">
	<tr>
		<td><input type="submit" name="cancel" value="Cancel"
			onclick="validate=0;" /> <input type="<%=(currentPlaceholder.isProduct() ? "hidden" : "submit")%>" name="save" value="OK"
			onclick="validate=1;" /></td>
</table>
</div>
</form>
</div>
</div>
</body>
</html>