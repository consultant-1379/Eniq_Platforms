<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Utils"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="java.util.regex.Matcher"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/busyhourcfg/fragments/PageHeader.jspf"%>
<%@ include file="/busyhourcfg/fragments/Header.jspf"%>
<%@ include file="/busyhourcfg/fragments/CurrentValues.jspf"%>
<%@ include file="/busyhourcfg/fragments/LeftMenu.jspf"%>

<%!

  String getActionAsImageTag(Placeholder placeholder) {
    String result = "<img src='../img/XXX' alt='YYY' />";
    String replace1 = "";
    String replace2 = "";
    if (placeholder.getEnabled()) {
      replace1 = "stop_task.png";
      replace2 = "Disable placeholder";
    } else {
      if (Utils.replaceNull(placeholder.getCriteria()).trim().equals("")) {
        replace1 = "start_disabled_task.png";
        replace2 = "Enable placeholder";
      } else {
        replace1 = "start_task.png";
        replace2 = "Enable placeholder";
      }
    }
    Pattern pattern1 = Pattern.compile("XXX");
    Matcher matcher1 = pattern1.matcher(result);
    result = matcher1.replaceAll(replace1);
    Pattern pattern2 = Pattern.compile("YYY");
    Matcher matcher2 = pattern2.matcher(result);
    result = matcher2.replaceAll(replace2);
    return result;
  }%>

<body>
<div id="main">

<div id="left_column">
<%
  TechPack tmpTechPack = currentTechPack;
  TargetTechPack tmpTargetTechPack = currentTargetTechPack;  
  BusyhourSupport tmpBusyhourSupport = currentBusyhourSupport;
  out.println(getLeftMenu(tmpTechPack, tmpTargetTechPack, tmpBusyhourSupport));
%>
</div>

<div id="main_column">
<form id="PlaceholdersView" action="PlaceholdersView" method="POST" onSubmit="return filterFields();"> 
<%
  if (request.getAttribute("errormessage") != null) {
    out.println("<div class='error_message'>" + request.getAttribute("errormessage") + "</div>");
  }

  if (request.getAttribute("infomessage") != null) {
    out.println("<div class='info_message'>" + request.getAttribute("infomessage") + "</div>");
  }
  
  if(currentTargetTechPack==null) {
	  final String targettp = request.getParameter("targettp");
	  if (targettp != null) {
	  	currentTargetTechPack = currentTechPack.getTargetTechPackByVersionId(targettp);
	  	session.setAttribute("targettp", currentTargetTechPack);
	  }
  }
  if(currentBusyhourSupport==null) {
	  final String bhsupport = request.getParameter("bhsupport");
	  if (bhsupport != null) {
	  	currentBusyhourSupport = currentTargetTechPack.getBusyhourSupportByBhlevel(bhsupport);
	  	session.setAttribute("bhsupport", currentBusyhourSupport);
	  }
  }
%> 
<div id='placeholder_table_title'>Product&nbsp;placeholders:</div>
<table id='placeholder_table'>
	<tr>
		<th>&nbsp;</th>
		<th>Type</th>
		<th>Description</th>
		<th>Busy Hour Type</th>
		<th>Enabled</th> 
	</tr>

	<%
	  if (currentBusyhourSupport != null) {
	      for (Placeholder placeholder : currentBusyhourSupport.getPlaceholders()) {
	        if (placeholder.isProduct()) {
	          boolean edited = Utils.integerToBoolean(placeholder.getReactivateviews());
	          final String ref1 = "PlaceholderView?targettp="
	              + URLEncoder.encode(currentTargetTechPack.getVersionId(), Constants.DEFAULT_ENCODING) + "&bhsupport="
	              + URLEncoder.encode(currentBusyhourSupport.getBhlevel(), Constants.DEFAULT_ENCODING) + "&bhtype="
	              + URLEncoder.encode(placeholder.getBhtype(), Constants.DEFAULT_ENCODING);
	          final String ref2 = "PlaceholderEnable?targettp="
	              + URLEncoder.encode(currentTargetTechPack.getVersionId(), Constants.DEFAULT_ENCODING) + "&bhsupport="
	              + URLEncoder.encode(currentBusyhourSupport.getBhlevel(), Constants.DEFAULT_ENCODING) + "&bhtype="
	              + URLEncoder.encode(placeholder.getBhtype(), Constants.DEFAULT_ENCODING) + "&enabled="
	              + URLEncoder.encode(Utils.booleanToString(!placeholder.getEnabled()), Constants.DEFAULT_ENCODING);
	          out.println("  <tr class='" + (edited ? "modified" : "unmodified") + "'>");
	          out.print("<td width='38px'>");
	          out.print("<a href='" + ref1
	              + "'><img src='../img/preferences.png' alt='View product placeholder' /></a>");
	          if (placeholder.getCriteria().trim().equals("")) {
		          out.print("<a href='#'>" + getActionAsImageTag(placeholder) + "</a>");
	          } else {
	              out.print("<a href='" + ref2 + "'>" + getActionAsImageTag(placeholder) + "</a>");
	          }
	          out.print("</td>");
	          out.println("    <td width='50px'>" + Utils.replaceNull(placeholder.getBhtype().trim()) + "</td>");
	          out.println("    <td>" + Utils.replaceNull(placeholder.getDescription()) + "</td>");
	          if (placeholder.isTimelimited()) {
	            out.println("    <td width='100px'>Timelimited</td>");
	          } else if (placeholder.isSlidingwindow()) {
	            out.println("    <td width='100px'>Slidingwindow</td>");
	          } else if (placeholder.isTimelimitedTimeconsistent()) {
	            out.println("    <td width='100px'>Timelimited + Timeconsistent</td>");
	          } else if (placeholder.isSlidingwindowTimeconsistent()) {
	            out.println("    <td width='100px'>Slidingwindow + Timeconsistent</td>");
	          } else if (placeholder.isPeakrop()) {
	            out.println("    <td width='100px'>Peakrop</td>");
	          } else {
	            out.println("    <td width='100px'></td>");
	          }
	          out.println("    <td width='50px'>" + Utils.booleanToString(placeholder.getEnabled()) + "</td>");
	          out.println("  </tr>");
	        }
	      }
	    }
	%>
</table>
<div id='placeholder_table_title'>Custom&nbsp;placeholders:</div>
<table id='placeholder_table'>
	<tr>
		<th>&nbsp;</th>
		<th>Type</th>
		<th>Description</th>
		<th>Busy Hour Type</th>
		<th>Enabled</th>
	</tr>

	<%
	  if (currentBusyhourSupport != null) {
	      for (Placeholder placeholder : currentBusyhourSupport.getPlaceholders()) {
	        if (placeholder.isCustom()) {
	          boolean edited = Utils.integerToBoolean(placeholder.getReactivateviews());
	          final String ref1 = "PlaceholderView?targettp="
	              + URLEncoder.encode(currentTargetTechPack.getVersionId(), Constants.DEFAULT_ENCODING) + "&bhsupport="
	              + URLEncoder.encode(currentBusyhourSupport.getBhlevel(), Constants.DEFAULT_ENCODING) + "&bhtype="
	              + URLEncoder.encode(placeholder.getBhtype(), Constants.DEFAULT_ENCODING);
	          final String ref2 = "PlaceholderEnable?targettp="
	              + URLEncoder.encode(currentTargetTechPack.getVersionId(), Constants.DEFAULT_ENCODING) + "&bhsupport="
	              + URLEncoder.encode(currentBusyhourSupport.getBhlevel(), Constants.DEFAULT_ENCODING) + "&bhtype="
	              + URLEncoder.encode(placeholder.getBhtype(), Constants.DEFAULT_ENCODING) + "&enabled="
	              + URLEncoder.encode(Utils.booleanToString(!placeholder.getEnabled()), Constants.DEFAULT_ENCODING);
	          out.println("  <tr class='" + (edited ? "modified" : "unmodified") + "'>");
	          out.print("<td width='38px'>");
	          out.print("<a href='" + ref1 + "'><img src='../img/preferences.png' alt='View custom placeholder' /></a>");
	          if (Utils.replaceNull(placeholder.getCriteria()).trim().equals("")) {
		          out.print("<a href='#'>" + getActionAsImageTag(placeholder) + "</a>");
	          } else {
	              out.print("<a href='" + ref2 + "'>" + getActionAsImageTag(placeholder) + "</a>");
	          }
	          out.print("</td>");
	          out.println("    <td width='50px'>" + Utils.replaceNull(placeholder.getBhtype().trim()) + "</td>");
	          out.println("    <td>" + Utils.replaceNull(placeholder.getDescription()) + "</td>");

	          if (placeholder.isTimelimited()) {
	            out.println("    <td width='100px'>Timelimited</td>");
	          } else if (placeholder.isSlidingwindow()) {
	            out.println("    <td width='100px'>Slidingwindow</td>");
	          } else if (placeholder.isTimelimitedTimeconsistent()) {
	            out.println("    <td width='100px'>Timelimited + Timeconsistent</td>");
	          } else if (placeholder.isSlidingwindowTimeconsistent()) {
	            out.println("    <td width='100px'>Slidingwindow + Timeconsistent</td>");
	          } else if (placeholder.isPeakrop()) {
	            out.println("    <td width='100px'>Peakrop</td>");
	          } else {
	            out.println("    <td width='100px'></td>");
	          }
	          out.println("    <td width='50px'>" + Utils.booleanToString(placeholder.getEnabled()) + "</td>");
	          out.println("  </tr>");
	        }
	      }
	    }
	%>
</table>

<div>
<table id="button_area">
	<tr>
		<td><input type="submit" name="cancel" value="<- Tech Pack Update" /></td>
	</tr>
</table>
</div>
</form>
</div>

</div>
</body>
</html>