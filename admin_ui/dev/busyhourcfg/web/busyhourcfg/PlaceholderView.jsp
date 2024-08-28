<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.common.Utils"%>
<%@ page import="com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder"%>
<%@ page import="java.util.ArrayList"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/busyhourcfg/fragments/PageHeader.jspf"%>
<%@ include file="/busyhourcfg/fragments/Header.jspf"%>
<%@ include file="/busyhourcfg/fragments/CurrentValues.jspf"%>
<%@ include file="/busyhourcfg/fragments/LeftMenu.jspf"%>

<%
  DefaultPlaceholder defaultPlaceholder = (DefaultPlaceholder) currentPlaceholder;
  String typetext = "";
  if (defaultPlaceholder.isTimelimited()) {
    typetext = "Timelimited";
  } else if (defaultPlaceholder.isSlidingwindow()) {
    typetext = "Slidingwindow";
  } else if (defaultPlaceholder.isTimelimitedTimeconsistent()) {
    typetext = "Timelimited&nbsp;+&nbsp;Timeconsistent";
  } else if (defaultPlaceholder.isSlidingwindowTimeconsistent()) {
    typetext = "Slidingwindow&nbsp;+&nbsp;Timeconsistent";
  } else if (defaultPlaceholder.isPeakrop()) {
    typetext = "Peakrop";
  }
%>

<script type="text/javascript">
var ids = new Array('description', 'source', 'where', 'criteria', 'keys', 'aggregationtype', 'mappedtypes');
var values = new Array('', '', '', '', '', '', '', '');
var needToConfirm = true;

window.onbeforeunload = confirmExit;

function populateArrays()
{
  // assign the default values to the items in the values array
  for (var i = 0; i < ids.length; i++)
  {
    var elem = document.getElementById(ids[i]);
    if (elem)
      if (elem.type == 'checkbox' || elem.type == 'radio')
        values[i] = elem.checked;
      else
        values[i] = elem.value;
  }      
}

function confirmExit()
{
  if (needToConfirm)
  {
    // check to see if any changes to the data entry fields have been made
    for (var i = 0; i < values.length; i++)
    {
      var elem = document.getElementById(ids[i]);
      if (elem)
        if ((elem.type == 'checkbox' || elem.type == 'radio') && values[i] != elem.checked) {
          return 'All changes made will be lost.';
        } else if (!(elem.type == 'checkbox' || elem.type == 'radio') && elem.value != values[i]) {
          return 'All changes made will be lost.';
        }
    }

    // no changes - return nothing      
  }
}

function confirmItemClear() {
    var yes = confirm('Are you sure you want to clear all data for this placeholder?');
    if (yes)
       return true;
    else
       return false;
}
</script>

<body>
<div id="main">
<%!
public static List<String> getSourcesAsString(List<Source> sources) {
  List<String> result = new ArrayList<String>();
  if ((sources != null) && (sources.size() > 0)) {
    for (Source source : sources) {
      result.add(source.getTypename());
    }
  }
  return result;
}

public static List<String> getKeysAsString(List<Key> keys) {
  List<String> result = new ArrayList<String>();
  if ((keys != null) && (keys.size() > 0)) {
    for (Key key : keys) {
      result.add(key.getKeyName());
    }
  }
  return result;
}

public static List<String> getMappedtypesAsString(List<MappedType> types) {
  List<String> result = new ArrayList<String>();
  if ((types != null) && (types.size() > 0)) {
    for (MappedType type : types) {
      result.add(type.getBhTargettype() + "(" + (type.getEnabled() ? "Enabled" : "Disabled") + ")");
    }
  }
  return result;
}

%>
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
<div id="placeholder_area_title">Placeholder&nbsp;<%= currentPlaceholder.getBhtype()%>&nbsp;Details:</div>
<form id="PlaceholderView" action="PlaceholderEdit" method="POST" onSubmit="return filterFields();">
<input type="hidden" name="targettp" value="<%= defaultPlaceholder.getTargetversionid() %>">
<input type="hidden" name="bhsupport" value="<%= defaultPlaceholder.getBhlevel() %>">
<input type="hidden" name="bhtype" value="<%= defaultPlaceholder.getBhtype() %>">


<table>
	<tr><td colspan="3">&nbsp;</td></tr>


	<tr>
		<td>Description</td>
		<td colspan="2"><textarea id="description" name="description" rows="2" cols="60" <%=(defaultPlaceholder.isProduct() ? "readonly" : "")%>><%=Utils.replaceNull(defaultPlaceholder.getDescription())%></textarea>&nbsp;</td>

	</tr>
	<tr>
		<td>Source(s)</td>
		<td><textarea id="source" name="source" rows="2" cols="60" readonly><%=Utils.stringListToString(getSourcesAsString(defaultPlaceholder.getSources()))%></textarea>&nbsp;</td>
		<td><button type="submit" name="editsource" value="true" onclick="needToConfirm = false;">...</button></td>
	</tr>
	<tr>
		<td>Where</td>
		<td colspan="2"><textarea id="where" name="where" rows="3" cols="60" <%=(defaultPlaceholder.isProduct() ? "readonly" : "")%>><%=Utils.replaceNull(defaultPlaceholder.getWhere())%></textarea>&nbsp;</td>

	</tr>
	<tr>
		<td>Formula</td>
		<td colspan="2"><textarea id="criteria" name="criteria" rows="6" cols="60" <%=(defaultPlaceholder.isProduct() ? "readonly" : "")%>><%=Utils.replaceNull(defaultPlaceholder.getCriteria())%></textarea>&nbsp;</td>

	</tr>
	<tr>
		<td>Keys</td>
		<td><textarea id="keys" name="keys" rows="4" cols="60" readonly><%=Utils.stringListToString(getKeysAsString(defaultPlaceholder.getKeys()))%></textarea>&nbsp;</td>
		<td><button type="submit" name="editkeys" value="true" onclick="needToConfirm = false;">...</button></td>
	</tr>
	<tr>
		<td>Busy Hour Type</td>
		<td><textarea id="aggregationtype" name="aggregationtype" rows="1" cols="60" readonly><%=typetext%></textarea>&nbsp;</td>
		<td><button type="submit" name="edittype" value="true" onclick="needToConfirm = false;">...</button></td>
	</tr>
	<tr>
		<td>Mapped Type(s)</td>
		<td><textarea id="mappedtypes" name="mappedtypes" rows="2" cols="60" readonly><%=Utils.stringListToString(getMappedtypesAsString(defaultPlaceholder.getMappedTypes()))%></textarea>&nbsp;</td>
		<td><button type="submit" name="editmappedtype" value="true" onclick="needToConfirm = false;">...</button></td>

	</tr>
	<tr>
		<td>Enabled</td>
		<td colspan="2"><%=Utils.booleanToString(defaultPlaceholder.getEnabled()) %></td>		
	</tr>
	<tr><td>&nbsp;</td></tr>

</table>
<div>
<table id="button_area">
	<tr>
		<td>
			<input type="submit" name="cancel" value="Cancel" onclick="needToConfirm = false;" >
			<% if (currentPlaceholder.isCustom()) { %>
			   <input type="submit" name="clear" value="Clear placeholder" onclick="needToConfirm = false;return confirmItemClear();" >
			   <input type="submit" name="save" value="Save placeholder" onclick="needToConfirm = false;">
			<% } %>	
		</td>
</table>
</div>
</form>

<script type="text/javascript">
  populateArrays();
</script>


</div>
</div>

</body>
</html>