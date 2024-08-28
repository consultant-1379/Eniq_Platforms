<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="refresh" content="5;url=ActivationStatusView" />

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
<div class='info_message'></table><img src="../img/running.gif" width="16" height="16" border="0" alt="Running" />&nbsp;Busy Hour Update is running. Please wait.</div>
</div>
</div>
</body>
</html>