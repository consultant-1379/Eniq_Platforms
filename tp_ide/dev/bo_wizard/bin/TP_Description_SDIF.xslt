<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes">
<xsl:output version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes" media-type="text/sdif" />
<xsl:template match="/">

<doc version="XSEIF R5">
<xsl:text>&#xA;</xsl:text>
<xsl:text>&#xA;</xsl:text>

<meta-data><?Pub Dtl?>
<confidentiality class="ericsson-internal"/>
<doc-name>DESCRIPTION</doc-name>
<doc-info></doc-info>
<doc-id>
<doc-no type="registration">2/1550/<xsl:value-of select="document/tp_description/@product" /></doc-no><language code="en"/>
<rev><xsl:value-of select="document/tp_description/@release" /></rev>
<date><y>yyyy</y><m>MM</m><d>dd</d></date>
</doc-id>
<company-id>
<business-unit>LMF/M</business-unit>
<company-name>Ericsson AB</company-name>
<company-symbol logotype="ericsson"></company-symbol>
</company-id>
<title>Technology Package Description, <xsl:value-of select="document/tp_description/@name" /></title>
<drafted-by>
<person>
<name>EANTPOH</name><signature>EANTPOH</signature>
<location>LA</location><company></company><department></department>
<phone><ecn-code></ecn-code><extension></extension></phone>
</person>
</drafted-by>
<!--When approved change the attribute to approved="yes".-->
<!--When checked change the attribute to checked="yes".-->
<approved-by approved="yes">
<person>
<name> Antti Pohja</name><signature></signature>
<location>LA</location><company> LMF</company><department> LMF/M</department>
<phone><ecn-code></ecn-code><extension></extension></phone>
</person>
</approved-by>
<checked-by checked="no">
<person>
<name></name><signature></signature>
<location></location><company></company><department></department>
</person>
</checked-by>
<factual-supervisor responsible="department">
<person>
<name></name><signature></signature>
<location>&amp;lt;LA></location><company>LMF</company><department>&amp;lt;Department/Section></department>
</person>
</factual-supervisor>
</meta-data>

<front>
<title-page>
<copyright>
<!--To change copyright year go to "Entities", "Text".-->
<p> Ericsson AB 1999, 2004, 2005 - All Rights Reserved</p>
</copyright>
<disclaimer><p>No part of this document may be reproduced in any form without the written permission of the copyright owner.</p><p>The contents of this document are subject to revision without notice due to continued progress in methodology, design, and manufacturing. Ericsson shall have no liability for any error or damage of any kind resulting from the use of this document.</p></disclaimer>
<trademark-list>
<tm-item><trademark>Ericsson</trademark><owner>is a trademark owned by Telefonaktiebolaget
LM Ericsson.</owner></tm-item>
<tm-item><trademark></trademark><owner>All other product or service names
mentioned in this User Description are trademarks of their respective companies.</owner>
</tm-item>
</trademark-list>
</title-page></front>

<body>
<chl1><title>Introduction</title>
<p>Technology Package: <xsl:value-of select="document/tp_description/@name" /></p>
<p>Supported Vendor Releases: <xsl:value-of select="document/tp_description/@vendor_releases" /></p>
<p>Product Number and Version: <xsl:value-of select="document/tp_description/@product" /> <xsl:value-of select="document/tp_description/@release" /></p>
</chl1>
<chl1>
<title>Fact tables</title>
<p>This chapter describes fact tables included in the Technology Package.</p>

<xsl:for-each select="document/facts/fact">
<xsl:if test="@fact_table">
<chl2>
<title><xsl:value-of select="@name" /></title>
<p>Fact Table: <xsl:value-of select="@name" /></p>
<p>Size: <xsl:value-of select="@size" /></p>
<p>Total Aggregation: <xsl:value-of select="@aggregation" /></p>
<p>Object Busy Hour Support: <xsl:value-of select="@obj_bh_support" /></p>
<p>Element Busy Hour Support: <xsl:value-of select="@elem_bh_support" /></p>
<p>Delta Calculation: <xsl:value-of select="@delta_support" /></p>

<table>
<caption>Keys for <xsl:value-of select="@name" /></caption>
<tgroup cols="3">
<tbody>
<row>
<entry valign="top"><tp><emph>Name</emph></tp></entry>
<entry valign="top"><tp><emph>Data Type</emph></tp></entry>
<entry valign="top"><tp><emph>Duplicate Constraint</emph></tp></entry>
</row>

<xsl:for-each select="./key">
<row>
<entry><tp><xsl:value-of select="@name" /></tp></entry>
<entry><tp><xsl:value-of select="@data_type" /></tp></entry>
<entry><tp><xsl:value-of select="@duplicate_constraint" /></tp></entry>
</row>
</xsl:for-each>

</tbody>
</tgroup>
</table>

<p></p>

<table>
<caption>Counters for <xsl:value-of select="@name" /></caption>
<tgroup cols="5">
<tbody>
<row>
<entry valign="top"><tp><emph>Name</emph></tp></entry>
<entry valign="top"><tp><emph>Data Type</emph></tp></entry>
<entry valign="top"><tp><emph>Time Aggregation</emph></tp></entry>
<entry valign="top"><tp><emph>Group Aggregation</emph></tp></entry>
<entry valign="top"><tp><emph>Type</emph></tp></entry>
</row>

<xsl:for-each select="./counter">
<row>
<entry><tp><xsl:value-of select="@name" /></tp></entry>
<entry><tp><xsl:value-of select="@data_type" /></tp></entry>
<entry><tp><xsl:value-of select="@time_aggregation" /></tp></entry>
<entry><tp><xsl:value-of select="@group_aggregation" /></tp></entry>
<entry><tp><xsl:value-of select="@type" /></tp></entry>
</row>
</xsl:for-each>

</tbody>
</tgroup>
</table>
</chl2>
</xsl:if>
</xsl:for-each>
</chl1>

<chl1><title>Busy Hours</title>
<p>This chapter describes busy hours included in the Technology Package.</p>
<p></p>
<xsl:for-each select="document/busyhours/busyhour">
<p><emph><xsl:value-of select="@name" /></emph></p>
<p>Description: <xsl:value-of select="@description" /></p>
<p>Criteria: <xsl:value-of select="@criteria" /></p>
<p>Source: <xsl:value-of select="@source" /></p>
<p>Where condition: <xsl:value-of select="@where" /></p>
<p></p>
</xsl:for-each>
</chl1>

<chl1><title>Topology tables</title>
<p>This chapter describes topology tables included in the Technology Package.</p>
<p></p>

<xsl:for-each select="document/dimensions/dimension">
<chl2>
<title><xsl:value-of select="@name" /></title>
<p>Topology Table: <xsl:value-of select="@name" /></p>
<p>Type: <xsl:value-of select="@type" /></p>
<p>Update Method: <xsl:value-of select="@update" /></p>

<table>
<caption>Columns for <xsl:value-of select="@name" /></caption>
<tgroup cols="3">
<tbody>
<row>
<entry valign="top"><tp><emph>Name</emph></tp></entry>
<entry valign="top"><tp><emph>Data Type</emph></tp></entry>
<entry valign="top"><tp><emph>Included in updates</emph></tp></entry>
</row>

<xsl:for-each select="./column">
<row>
<entry><tp><xsl:value-of select="@name" /></tp></entry>
<entry><tp><xsl:value-of select="@data_type" /></tp></entry>
<entry><tp><xsl:value-of select="@included_update" /></tp></entry>
</row>
</xsl:for-each>

</tbody>
</tgroup>
</table>
</chl2>
</xsl:for-each>
</chl1>

<chl1>
<title>Interfaces</title>
<p>This chapter describes interfaces included in the Technology Package.</p>

<table>
<caption>Interfaces</caption>
<tgroup cols="2">
<tbody>
<row>
<entry valign="top"><tp><emph>Name</emph></tp></entry>
<entry valign="top"><tp><emph>Type</emph></tp></entry>
</row>

<xsl:for-each select="document/interfaces/interface">
<row>
<entry><tp><xsl:value-of select="@name" /></tp></entry>
<entry><tp><xsl:value-of select="@type" /></tp></entry>
</row>
</xsl:for-each>

</tbody>
</tgroup>
</table>
</chl1>

<chl1>
<title>SQL Interface</title>
<p>This chapter describes SQL Interface included in the Technology Package.</p>
<p></p>

<xsl:for-each select="document/sql_interface/view">
<xsl:if test="columns/column">
<p><emph><xsl:value-of select="@name" /></emph></p>
<table>
<caption>Columns</caption>
<tgroup cols="2">
<tbody>
<row>
<entry valign="top"><tp><emph>Name</emph></tp></entry>
<entry valign="top"><tp><emph>Data Type</emph></tp></entry>
</row>

<xsl:for-each select="columns">
<xsl:for-each select="column">
<row>
<entry><tp><xsl:value-of select="@name" /></tp></entry>
<entry><tp><xsl:value-of select="@datatype" /></tp></entry>
</row>
</xsl:for-each>
</xsl:for-each>

</tbody>
</tgroup>
</table>
<p></p>
</xsl:if>
</xsl:for-each>
</chl1>

</body>
<back>
<glossary>
<glossary-list>
<gl-item>
<term>ENIQ</term>
<expansion><p>Ericsson Network IQ</p>
</expansion>
</gl-item>
</glossary-list>
</glossary>
<reference>
<reference-list>
<!--Add details of references made to other documents.  
-->
<rf-subsection></rf-subsection>
<rf-item xml:id="rf-OSSGlossary">
<rf-title><ulink xlink:type="simple" xlink:href="urn:x-ericsson:r2:reg-doc:*0033-*:*:*?title=Operations Support System (OSS) Glossary"
xlink:title="Operations Support System (OSS) Glossary" xmlns:xlink="http://www.w3.org/1999/xlink"></ulink></rf-title>
</rf-item>
</reference-list></reference>

</back>
</doc>

</xsl:template>
</xsl:stylesheet>
