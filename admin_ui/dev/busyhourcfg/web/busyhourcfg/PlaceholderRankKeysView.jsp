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
    		for (i=0;i<<%=currentPlaceholder.getKeys().size()%>;i=i+1)
    		{

				if (keyname[i].value.length == 0){
        		    alert("Key Name can not be empty.");
    		    	flagValidate = false;
				}
				
				if (keyvalue[i].value.length == 0){
        		    alert("Key Value can not be empty.");
    		    	flagValidate = false;
				}
       		
        		for (ii=i+1;ii<<%=currentPlaceholder.getKeys().size()%>;ii=ii+1)
        		{
					if (keyname[i].value == keyname[ii].value){
	        		    alert("Duplicate value in Key Name ["+keyname[i].value + "]. Key name must be unique.");
        		    	flagValidate = false;
					}
				}
				 		
        		if (source[i].value.trim() && keyvalue[i].value.indexOf("'")==0 && keyvalue[i].value.lastIndexOf("'") == keyvalue[i].value.length - 1){
        		    alert("Source Table needs to be empty if Key Value is a string");
        		    flagValidate = false;
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
  out.println("<div class='error_message'>" + request.getAttribute("errormessage") + "</div>");
}

if (request.getAttribute("infomessage") != null) {
  out.println("<div class='info_message'>" + request.getAttribute("infomessage") + "</div>");
}
%>
<div id='placeholder_table_title'>Placeholder&nbsp;<%=currentPlaceholder.getBhtype()%>&nbsp;Keys:</div>
<form id="PlaceholderRankKeysView" action="PlaceholderRankKeysEdit" onsubmit="return validate_form(this)" method="POST">
<table id="placeholderdetail_table">
	<tr>
		<th>Key Name</th>
		<th>Source Table</th>
		  <% if (currentPlaceholder.isCustom()) { %>
		<th></th>
		<% }%>
		<th>Key Value</th>
		<% if (currentPlaceholder.isCustom()) {  %>
		<th></th>
		<% }%>
	</tr>
<%
int count = 0;
for (Key key : currentPlaceholder.getKeys()) {
  final String ref = "#";
  out.println("  <tr>");
  out.println("    <td><textarea id=\"keyname\" name=\"keyname." + count + "\" rows=\"1\" cols=\"24\"" + (currentPlaceholder.isProduct() ? " readonly " : "") + ">" + Utils.replaceNull(key.getKeyName()) + "</textarea></td>");
  out.println("    <td><textarea id=\"source\" name=\"source." + count + "\" rows=\"1\" cols=\"24\"" + (currentPlaceholder.isProduct() ? " readonly " : "") + ">" + Utils.replaceNull(key.getSource()) + "</textarea></td>"); 
  if (currentPlaceholder.isCustom()) {  
  	out.println("    <td><button type=\"submit\" name=\"editsourcetable." + count + "\" value=\"true\" onclick=\"validate=0;\">...</button></td>");
  }
  out.println("    <td><textarea id=\"keyvalue\" name=\"keyvalue." + count + "\" rows=\"1\" cols=\"24\"" + (currentPlaceholder.isProduct() ? " readonly " : "") + ">" + Utils.replaceNull(key.getKeyValue()) + "</textarea></td>");
  if (currentPlaceholder.isCustom()) {  
	out.println("    <td><input type=\"submit\" name=\"delete." + count + "\" value=\"Delete\" onclick=\"validate=0;\"/></td>");
  } 
  out.println("  </tr>");
  count++;
}
%>
  <tr>
  	 <% if (currentPlaceholder.isCustom()) {  %>
     <td colspan="3"><input type="submit" name="add" value="New Key" onclick="validate=0;" /></td>
     <% } %>
  </tr>
</table>
<div>
<input type="hidden" name="count" value="" />
<table id="button_area">
	<tr>
		<td>
			<input type="submit" name="cancel" value="Cancel" onclick="validate=0;"/>
			<input type="<%=(currentPlaceholder.isProduct() ? "hidden" : "submit")%>" name="save" value="OK" onclick="validate=1;"/>
		</td>
</table>
</div>
</form>
</div>
</div>
</body>
</html>