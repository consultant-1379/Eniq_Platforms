<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output version="1.0" encoding="UTF-8" indent="no" omit-xml-declaration="no" media-type="text/html" />
<xsl:template match="/">
<html>
<head><title>Ericsson Network IQ Technology Package: <xsl:value-of select="document/tp_description/@description" /></title></head>
<body>
<h2><font size="+2">Technology Package Reporting</font></h2>
Reporting Universe is <xsl:value-of select="document/tp_description/@name" /> (<xsl:value-of select="document/tp_description/@filename" />.unv).
<br />
<br />
<br />
<h2><font size="+1">Universe Hierarchy</font></h2>
This chapter describes relations between measurement objects and topology objects. It defines which topology can be used with each measurement.<br />
<br />

<xsl:for-each select="document/tp_object_hierarchy/class">
<br/><b><xsl:value-of select="@link" /></b><br/>
<xsl:if test="object = true()"><xsl:value-of select="@name" /></xsl:if>
<ul><xsl:for-each select="./object"><li><xsl:value-of select="@name" /></li></xsl:for-each></ul>

<xsl:for-each select="./class">
<xsl:if test="@link != ../@link"><b><xsl:value-of select="@link" /></b><br/></xsl:if>
<xsl:if test="object = true()"><xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></xsl:if>
<ul><xsl:for-each select="./object"><li><xsl:value-of select="@name" /></li></xsl:for-each></ul>

<xsl:for-each select="./class">
<xsl:if test="@link != ../@link"><b><xsl:value-of select="@link" /></b><br/></xsl:if>
<xsl:if test="object = true()"><xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></xsl:if>
<ul><xsl:for-each select="./object"><li><xsl:value-of select="@name" /></li></xsl:for-each></ul>

<xsl:for-each select="./class">
<xsl:if test="@link != ../@link"><b><xsl:value-of select="@link" /></b><br/></xsl:if>
<xsl:if test="object = true()"><xsl:value-of select="../../../@name" /> / <xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></xsl:if>
<ul><xsl:for-each select="./object"><li><xsl:value-of select="@name" /></li></xsl:for-each></ul>
</xsl:for-each>

</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
<br />
<br />
<br />



<h2><font size="+1">Universe Objects</font></h2>
This chapter describes the objects in the universe.<br />
<table border="0" cellspacing="0" width="90%">

<xsl:for-each select="document/tp_objects/class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="20%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top" width="10%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="3"><b><xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@aggregation = '1'">SUM</xsl:if>
  <xsl:if test="@aggregation = '2'">MAX</xsl:if>
  <xsl:if test="@aggregation = '3'">MIN</xsl:if>
  <xsl:if test="@aggregation = '4'">AVG</xsl:if>
  <xsl:if test="@aggregation = '5'">COUNT</xsl:if>
  <xsl:if test="@aggregation = '6'"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:if>
</td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>

<xsl:for-each select="./class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="20%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top" width="10%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="3"><b><xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@aggregation = '1'">SUM</xsl:if>
  <xsl:if test="@aggregation = '2'">MAX</xsl:if>
  <xsl:if test="@aggregation = '3'">MIN</xsl:if>
  <xsl:if test="@aggregation = '4'">AVG</xsl:if>
  <xsl:if test="@aggregation = '5'">COUNT</xsl:if>
  <xsl:if test="@aggregation = '6'"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:if>
</td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>

<xsl:for-each select="./class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="20%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top" width="10%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="3"><b><xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@aggregation = '1'">SUM</xsl:if>
  <xsl:if test="@aggregation = '2'">MAX</xsl:if>
  <xsl:if test="@aggregation = '3'">MIN</xsl:if>
  <xsl:if test="@aggregation = '4'">AVG</xsl:if>
  <xsl:if test="@aggregation = '5'">COUNT</xsl:if>
  <xsl:if test="@aggregation = '6'"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:if>
</td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>

<xsl:for-each select="./class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="20%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top" width="10%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="3"><b><xsl:value-of select="../../../@name" /> / <xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@aggregation = '1'">SUM</xsl:if>
  <xsl:if test="@aggregation = '2'">MAX</xsl:if>
  <xsl:if test="@aggregation = '3'">MIN</xsl:if>
  <xsl:if test="@aggregation = '4'">AVG</xsl:if>
  <xsl:if test="@aggregation = '5'">COUNT</xsl:if>
  <xsl:if test="@aggregation = '6'"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:if>
</td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
</table>
<br />
<br />
<br />



<h2><font size="+1">Universe Conditions</font></h2>
This chapter describes the conditions in the universe.<br />
<table border="0" cellspacing="0" width="90%">

<xsl:for-each select="document/tp_conditions/class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="20%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="2"><b><xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>

<xsl:for-each select="./class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="30%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="2"><b><xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>

<xsl:for-each select="./class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="30%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="2"><b><xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>

<xsl:for-each select="./class">
<xsl:if test="object = true()">
<tr>
<td valign="top" width="30%"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
<td valign="top"><b><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></b></td>
</tr>
<tr>
<td valign="top" colspan="2"><b><xsl:value-of select="../../../@name" /> / <xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></b></td>
</tr>
</xsl:if>
<xsl:for-each select="./object">
<tr>
<td valign="top"><xsl:value-of select="@name" /></td>
<td valign="top">
  <xsl:if test="@description != ''"><xsl:value-of select="@description" /></xsl:if>
  <xsl:if test="@description = ''">-</xsl:if>
</td>
</tr>
</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
</table>
<br/>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
