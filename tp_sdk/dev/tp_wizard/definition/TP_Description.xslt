<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes">
<xsl:output version="1.0" encoding="UTF-8" indent="no" omit-xml-declaration="no" media-type="text/html" />
<xsl:template match="/">
<html>
<head><title>Ericsson Network IQ Technology Package: <xsl:value-of select="document/tp_description/@name" /></title></head>
<body>
<h2><font size="+2">Technology Package Description</font></h2>
Technology Package: <xsl:value-of select="document/tp_description/@name" /> <br />
Supported Vendor Releases: <xsl:value-of select="document/tp_description/@vendor_releases" /> <br />
Product Number and Version: <xsl:value-of select="document/tp_description/@product" /> <br />
<br />
<br />
<br />
<h2><font size="+1">Fact tables</font></h2>
This chapter describes fact tables included in the Technology Package.<br />
<br />

<xsl:for-each select="document/facts/fact">
<xsl:if test="@fact_table">
<h3><font size="+1"><xsl:value-of select="@name" /></font></h3>
Fact Table: <xsl:value-of select="@name" /><br />
Size: <xsl:value-of select="@size" /><br />
Total Aggregation: <xsl:value-of select="@aggregation" /><br />
Object Busy Hour Support: <xsl:value-of select="@obj_bh_support" /><br />
Element Busy Hour Support: <xsl:value-of select="@elem_bh_support" /><br />
Delta Calculation: <xsl:value-of select="@delta_support" /><br />
<br />
<b>Keys</b> <br />
<table border="0" cellspacing="0" width="90%">
<tr><td width="20%">Name</td><td width="20%">Data Type</td><td width="60%">Duplicate Constraint</td></tr>
<xsl:for-each select="./key">
<tr><td><xsl:value-of select="@name" /></td><td><xsl:value-of select="@data_type" /></td><td><xsl:value-of select="@duplicate_constraint" /></td></tr>
</xsl:for-each>
</table>
<br />
<b>Counters</b> <br />
<table border="0" cellspacing="0" width="90%">
<tr><td width="20%">Name</td><td width="20%">Data Type</td><td width="20%">Time Aggregation</td><td width="20%">Group Aggregation</td><td width="20%">Type</td></tr>
<xsl:for-each select="./counter">
<tr><td><xsl:value-of select="@name" /></td><td><xsl:value-of select="@data_type" /></td><td><xsl:value-of select="@time_aggregation" /></td><td><xsl:value-of select="@group_aggregation" /></td><td><xsl:value-of select="@type" /></td></tr>
</xsl:for-each>
</table>
</xsl:if>
</xsl:for-each>
<br />
<br />
<br />
<h2><font size="+1">Busy Hours</font></h2>
This chapter describes busy hours included in the Technology Package.<br />
<br />

<xsl:for-each select="document/busyhours/busyhour">
<b><xsl:value-of select="@name" /></b><br />
Description: <xsl:value-of select="@description" /><br />
Criteria: <xsl:value-of select="@criteria" /><br />
Source: <xsl:value-of select="@source" /><br />
Where condition: <xsl:value-of select="@where" /><br />
<br />
</xsl:for-each>
<br />
<br />
<br />
<h2><font size="+1">Topology tables</font></h2>
This chapter describes topology tables included in the Technology Package.<br />
<br />

<xsl:for-each select="document/dimensions/dimension">
<h3><font size="+1"><xsl:value-of select="@name" /></font></h3>
Topology Table: <xsl:value-of select="@name" /><br />
Type: <xsl:value-of select="@type" /><br />
Update Method: <xsl:value-of select="@update" /><br />
<br />
<b>Columns</b> <br />
<table border="0" cellspacing="0" width="90%">
<tr><td width="20%">Name</td><td width="20%">Data Type</td><td width="60%">Included in updates</td></tr>
<xsl:for-each select="./column">
<tr><td><xsl:value-of select="@name" /></td><td><xsl:value-of select="@data_type" /></td><td><xsl:value-of select="@included_update" /></td></tr>
</xsl:for-each>
</table>
</xsl:for-each>
<br />
<br />
<br />
<h2><font size="+1">Interfaces</font></h2>
This chapter describes interfaces included in the Technology Package.<br />
<br />
<table border="0" cellspacing="0" width="90%">
<tr><td width="30%">Name</td><td width="70%">Type</td></tr>
<xsl:for-each select="document/interfaces/interface">
<tr><td><xsl:value-of select="@name" /></td><td><xsl:value-of select="@type" /></td></tr>
</xsl:for-each>
</table>
<br />
<br />
<br />
<h2><font size="+1">SQL Interface</font></h2>
This chapter describes SQL Interface included in the Technology Package.<br />
<br />

<xsl:for-each select="document/sql_interface/view">
<b><xsl:value-of select="@name" /></b><br />
Columns: <xsl:value-of select="@columns" /><br />
<br />
</xsl:for-each>

<br />
</body>
</html>
</xsl:template>
</xsl:stylesheet>
