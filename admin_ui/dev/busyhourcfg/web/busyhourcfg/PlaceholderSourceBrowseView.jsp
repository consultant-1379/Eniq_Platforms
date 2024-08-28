<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Utils"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.database.DatabaseSession"%>
<%@ page import="java.util.Map"%>

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
   	      if (validate_required(select_basetable, "Tablename can not be empty") == false) {
   	  	   flagValidate = false;
    	  }
    	}
  	}
  	var xssFlag = filterFields();
    return (flagValidate && xssFlag); 
}

function clear(box) {
	while (box.length > 0) {
		box.remove(0);
	}
	box.disabled = true;
}

function populate(menu) {
	var techpacks = document.getElementById('select_techpack');
	var basetables = document.getElementById('select_basetable');
	switch (menu) {
	  default:
	  case "techpack":
		clear(basetables);
		show('<%=request.getContextPath()%>/ajaxservlet/BusyhourSourceList?getSourceTechpacks', 'select_techpack');
		break;
	  case "basetable":
		clear(basetables);
		var techpack = document.getElementById('select_techpack').value;
		show('<%=request.getContextPath()%>/ajaxservlet/BusyhourSourceList?getSourceBasetables=' + techpack, 'select_basetable');
		break;
	}
}
</script>
<body onload="populate('techpack')">
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
<div id='placeholder_table_title'>Basetable:</div>
<form id="PlaceholderSourceBrowseView" action="PlaceholderSourceBrowseEdit" 
    onsubmit="var result=validate_form(this);return result;" method="POST">
<table id='placeholder_table'>
	<tr>
		<td class="parameter_desc">From Tech Pack:</td>
		<td class="parameter_value"><select onchange="populate('basetable');"
			name="select_techpack" id="select_techpack">
		</select></td>
	</tr>
	<tr>
		<td class="parameter_desc">Tablename:</td>
		<td class="parameter_value"><select  
			name="select_basetable" id="select_basetable">
		</select></td>
	</tr>
</table>
<div>
<input type="hidden" name="count" value="" />
<table id="button_area">
	<tr>
		<td>
			<input type="submit" name="cancel" value="<- Previous page" onclick="validate=0;" />
			<input type="submit" name="save" value="OK" onclick="validate=1;" />
		</td>
</table>
</div>
</form>
</div>
</div>
</body>
</html>