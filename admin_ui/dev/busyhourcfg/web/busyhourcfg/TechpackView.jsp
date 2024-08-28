<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.data.vo.TechPack"%>	
<%@ page import="com.ericsson.eniq.busyhourcfg.database.TechPackReader"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.database.DatabaseSession"%>
<%@ page import="java.util.List"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ include file="/busyhourcfg/fragments/PageHeader.jspf" %>
<%@ include file="/busyhourcfg/fragments/Header.jspf" %>

<body>
<div id="main">

<div id="left_column_first_page">
</div>

<div id="main_column">

<%
	if (request.getAttribute("errormessage") != null) {
	  %> 
      <div class='error_message'> <%= request.getAttribute("errormessage") %> </div>
	  <%
	}
%>

<form method="POST" action='TechpackView' onSubmit="return filterFields();">
<div id="whole_frame">
<input type="hidden" name = 'browserstatusmessage' value ="">
<input type="hidden" name = 'browserversionmessage' value ="">
<table id="helptext">
	<tr>
		<td>For Busy Hour Configuration, select a tech pack from the Source Tech Pack drop-down list and click Show. 
		<br>Then select a measurement type to see what Product/Custom placeholders are defined.</br>
		<br>See <a href="/adminui/manual/index.html#<%=helpTag%>" onClick="new_window(this.href, 'help');return false;">Help</a>&nbsp;for more information on Busy Hour Configuration.</br></td>
	</tr>
</table>
<table id="select_tp_table">
	<tr>
		<td colspan='2' class='parameter_title'>Source Tech Pack:</td>
	</tr>
	<tr>
		<td class="parameter_value"><select	name="select_techpacks" id="select_techpacks">
<%	
	try {
      final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
      final TechPack selectedTechPack = (TechPack) session.getAttribute("sourcetp");
      final TechPackReader factory = TechPackReaderFactory.getTechPackReader(databaseSession);
      List<String> versionids = factory.getAllActivatedPMTypeVersionIds();
      for (String versionid : versionids) {
        if ((selectedTechPack != null) && selectedTechPack.getVersionId().equals(versionid)) {
        	out.println("<option value=" + versionid + " selected>" + versionid + "</option>");
        } else {
        	out.println("<option value=" + versionid + ">" + versionid + "</option>");
        }
      }
	} catch (Exception e) {
	  // TODO handle errors
	}
%>      
		</select></td>
	</tr>
	<tr>
		<td colspan="2"><center><input type="submit" value="Show"></center></td>
	</tr>
</table>
</div>
</form>
</div>
</div>
</body>
</html>