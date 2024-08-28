<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Utils"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Constants"%>
<%@ page
	import="com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/busyhourcfg/fragments/PageHeader.jspf"%>
<%@ include file="/busyhourcfg/fragments/Header.jspf"%>
<%@ include file="/busyhourcfg/fragments/CurrentValues.jspf"%>
<%@ include file="/busyhourcfg/fragments/LeftMenu.jspf"%>

<%
  DefaultPlaceholder defaultPlaceholder = (DefaultPlaceholder) currentPlaceholder;
%>

<script type="text/javascript">
var validate = 0;
function validate_form(thisform) {
  var flagValidate = true;
  if (validate) {
    with (thisform) {
      if (validate_required(type, "Type can not be empty") == false) {
	    type.focus();
	    flagValidate = false;
  	  }
  	  if (type.value=="<%=DefaultPlaceholder.RANKBH_TIMELIMITED%>") {	  	  
       	if (validate_empty(lookback, "Lookback can not have value") == false) {
       		lookback.focus();
   	    	flagValidate = false;
      	}
       	if (validate_empty(pthreshold, "Positive Threshold can not have value") == false) {
       		pthreshold.focus();
   	    	flagValidate = false;
      	}
       	if (validate_empty(nthreshold, "Negative Threshold can not have value") == false) {
       		nthreshold.focus();
   	    	flagValidate = false;
      	}
  	  }
  	  if (type.value=="<%=DefaultPlaceholder.RANKBH_SLIDINGWINDOW%>") {
       	if (validate_empty(lookback, "Lookback can not have value") == false) {
       		lookback.focus();
   	    	flagValidate = false;
      	}
       	if (validate_empty(pthreshold, "Positive Threshold can not have value") == false) {
       		pthreshold.focus();
   	    	flagValidate = false;
      	}
       	if (validate_empty(nthreshold, "Negative Threshold can not have value") == false) {
       		nthreshold.focus();
   	    	flagValidate = false;
      	}
  	  }
  	  if (type.value=="<%=DefaultPlaceholder.RANKBH_TIMECONSISTENT%>") {
       	if (validate_required(lookback, "Lookback can not be empty") == false) {
       		lookback.focus();
   	    	flagValidate = false;
      	}
       	if (validate_required(pthreshold, "Positive Threshold can not be empty") == false) {
       		pthreshold.focus();
  	    	flagValidate = false;
       	}
       	if (validate_required(nthreshold, "Negative Threshold can not be empty") == false) {
       		nthreshold.focus();
   	    	flagValidate = false;
      	}
  	  }
  	  if (type.value=="<%=DefaultPlaceholder.RANKBH_TIMECONSISTENT_SLIDINGWINDOW%>") {
       	if (validate_required(lookback, "Lookback can not be empty") == false) {
       		lookback.focus();
   	    	flagValidate = false;
      	}
      	if (validate_required(pthreshold, "Positive Threshold can not be empty") == false) {
       		pthreshold.focus();
  	    	flagValidate = false;
       	}
       	if (validate_required(nthreshold, "Negative Threshold can not be empty") == false) {
       		nthreshold.focus();
   	    	flagValidate = false;
       	}
  	  }
  	  if (type.value=="<%=DefaultPlaceholder.RANKBH_PEAKROP%>") {
       	if (validate_empty(lookback, "Lookback can not have value") == false) {
       		lookback.focus();
   	    	flagValidate = false;
      	}
       	if (validate_empty(pthreshold, "Positive Threshold can not have value") == false) {
       		pthreshold.focus();
   	    	flagValidate = false;
      	}
       	if (validate_empty(nthreshold, "Negative Threshold can not have value") == false) {
       		nthreshold.focus();
   	    	flagValidate = false;
      	}
  	  }
      if (validate_numeric(lookback, true, "Invalid Lookback value") == false) {
  	    type.focus();
        flagValidate = false;
  	  }
      if (validate_maxvalue(lookback, 90, "Lookback can not be greater than 90 days") == false) {
        lookback.focus();
        flagValidate = false;
      }
      if (validate_numeric(pthreshold, true, "Invalid Positive Threshold value") == false) {
    	type.focus();
        flagValidate = false;
      }
      if (validate_numeric(nthreshold, true, "Invalid Negative Threshold value") == false) {
      	type.focus();
        flagValidate = false;
      }
    }
  }
  var xssFlag = filterFields();
  return (flagValidate && xssFlag); 
}


function Disab(val) {
	frm = document.forms[0];
	if (val == "RANKBH_TIMELIMITED") {
		frm.lookback.value = '';
		frm.pthreshold.value = '';
		frm.nthreshold.value = '';
		frm.lookback.disabled = true;	
		frm.pthreshold.disabled = true;
		frm.nthreshold.disabled = true;
	}
	if (val == "RANKBH_SLIDINGWINDOW" || val == "RANKBH_PEAKROP") {
		frm.lookback.value = '';
		frm.pthreshold.value = '';
		frm.nthreshold.value = '';
		frm.lookback.disabled = true;
		frm.pthreshold.disabled = true;
		frm.nthreshold.disabled = true;
	}
	if (val == "RANKBH_TIMECONSISTENT") {
		frm.lookback.disabled = false;
		frm.pthreshold.disabled = false;
		frm.nthreshold.disabled = false;
	}
	if (val == "RANKBH_TIMECONSISTENT_SLIDINGWINDOW") {
		frm.lookback.disabled = false;
		frm.pthreshold.disabled = false;
		frm.nthreshold.disabled = false;
	}
}

</script>

<body>
<div id="main">

<div id="left_column">
<%
  out.println(getLeftMenu(currentTechPack, currentTargetTechPack,
					currentBusyhourSupport));
%>
</div>

<div id="main_column">
<%
  if (request.getAttribute("errormessage") != null) {
	out.println("<div class='error_message'>"
		+ request.getAttribute("errormessage") + "</div>");
  }

  if (request.getAttribute("infomessage") != null) {
	out.println("<div class='info_message'>"
		+ request.getAttribute("infomessage") + "</div>");
  }
%>
<div id="placeholder_area_title">Placeholder&nbsp;<%= currentPlaceholder.getBhtype()%>&nbsp;Busy&nbsp;Hour&nbsp;Type:</div>
<form id="PlaceholderTypeView" action="PlaceholderTypeEdit"
	onsubmit="return validate_form(this)" method="POST">
<table id="placeholderdetail_editor">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>Busy Hour Type</td>
		<td><select name="type" id="type" onChange="Disab(this.value)"
			<%=(defaultPlaceholder.isProduct() ? "disabled" : "")%>>
			<option value="<%=DefaultPlaceholder.RANKBH_TIMELIMITED%>"
				<%=(defaultPlaceholder.isTimelimited()
							? "selected"
							: "")%>>Timelimited</option>
			<option value="<%=DefaultPlaceholder.RANKBH_SLIDINGWINDOW%>"
				<%=(defaultPlaceholder.isSlidingwindow()
							? "selected"
							: "")%>>Slidingwindow</option>
			<option value="<%=DefaultPlaceholder.RANKBH_TIMECONSISTENT%>"
				<%=(defaultPlaceholder.isTimelimitedTimeconsistent()
							? "selected"
							: "")%>>Timelimited&nbsp;+&nbsp;Timeconsistent</option>
			<option
				value="<%=DefaultPlaceholder.RANKBH_TIMECONSISTENT_SLIDINGWINDOW%>"
				<%=(defaultPlaceholder.isSlidingwindowTimeconsistent()
							? "selected"
							: "")%>>Slidingwindow&nbsp;+&nbsp;Timeconsistent</option>
			<option value="<%=DefaultPlaceholder.RANKBH_PEAKROP%>"
				<%=(defaultPlaceholder.isPeakrop()
							? "selected"
							: "")%>>Peakrop</option>
		</select></td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td>Lookback</td>
		<td><textarea id="lookback" name="lookback" rows="1" cols="10"
			<%=(defaultPlaceholder.isProduct() ? "readonly" : "")%><%=(defaultPlaceholder.isLookbackDisabled() ? "disabled" : "")%><%=(currentPlaceholder.isProduct() ? " readonly " : "")%>><%=(defaultPlaceholder.getLookback() == null
							? ""
							: defaultPlaceholder.getLookback())%></textarea>&nbsp;</td>
	</tr>
	<tr>
		<td>Positive Threshold</td>
		<td><textarea id="pthreshold" name="pthreshold" rows="1"
			cols="10" <%=(defaultPlaceholder.isProduct() ? "readonly" : "")%><%=(defaultPlaceholder.isPthresholdDisabled() ? "disabled" : "")%><%=(currentPlaceholder.isProduct() ? " readonly " : "")%>><%=(defaultPlaceholder.getPThreshold() == null
							? ""
							: defaultPlaceholder.getPThreshold())%></textarea>&nbsp;</td>
	</tr>
	<tr>
		<td>Negative Threshold</td>
		<td><textarea id="nthreshold" name="nthreshold" rows="1"
			cols="10" <%=(defaultPlaceholder.isProduct() ? "readonly" : "")%><%=(defaultPlaceholder.isNthresholdDisabled() ? "disabled" : "")%><%=(currentPlaceholder.isProduct() ? " readonly " : "")%>><%=(defaultPlaceholder.getNThreshold() == null
							? ""
							: defaultPlaceholder.getNThreshold())%></textarea>&nbsp;</td>
	</tr>
    </table>
<div>
<table id="button_area">
	<tr>
		<td><input type="submit" name="cancel" value="Cancel"
			onclick="validate=0;" /> <input type="<%=(currentPlaceholder.isProduct() ? "hidden" : "submit")%>" name="save"
			value="OK" onclick="validate=1;" /></td>
</table>
</div>
</form>
</div>
</div>
</body>
</html>