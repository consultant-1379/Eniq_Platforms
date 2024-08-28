
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output version="1.0" doctype-public="-//ERICSSON//DTD XSEIF 1/FAD 110 05 R5//EN"
		doctype-system="XSEIF_R5.dtd" indent="yes" omit-xml-declaration="no" media-type="text/sdif" />
	<xsl:template match="/">
		<doc version="XSEIF R5" xmlns="urn:x-ericsson:r2:reg-doc:1551-fad.110.05:en:*">
		<?Pub Caret?>
				<meta-data>	<?Pub Dtl?>	
					<confidentiality class="ericsson-internal" />
					<doc-name>DESCRIPTION</doc-name>
					<doc-info></doc-info>
					<doc-id>
					<doc-no type="registration">
						<xsl:value-of select="document/tp_description/@product" />
					</doc-no>
					<language code="en" />
					<rev>
						<xsl:value-of select="document/tp_description/@release" />
					</rev>
					<date>
						<y>
						<xsl:value-of select="document/tp_description/@modyear" />
						</y>
						<m><xsl:value-of select="document/tp_description/@modmonth" />
						</m>
						<d><xsl:value-of select="document/tp_description/@moddate" />
						</d>
					</date>
				</doc-id>
				<company-id>
					<business-unit>LMF/M</business-unit>
					<company-name>Ericsson AB</company-name>
					<company-symbol logotype="ericsson"></company-symbol>
				</company-id>
				<title>Technology Package Universe Reference, <xsl:value-of select="document/tp_description/@name" /></title>
				<drafted-by>
					<person>
						<name>EANTPOH</name>
						<signature>EANTPOH</signature>
						<location>LA</location>
						<company></company>
						<department></department>
						<phone>
							<ecn-code></ecn-code>
							<extension></extension>
						</phone>
					</person>
				</drafted-by>
<!--When approved change the attribute to approved="yes".-->
<!--When checked change the attribute to checked="yes".-->
				<approved-by approved="yes">
					<person>
						<name> Antti Pohja</name>
						<signature></signature>
						<location>LA</location>
						<company>LMF</company>
						<department>LMF/M</department>
						<phone>
							<ecn-code></ecn-code>
							<extension></extension>
						</phone>
					</person>
				</approved-by>
				<checked-by checked="no">
					<person>
						<name></name>
						<signature></signature>
						<location></location>
						<company></company>
						<department></department>
					</person>
				</checked-by>
				<factual-supervisor responsible="department">
					<person>
						<name></name>
						<signature></signature>
						<location></location>
						<company>LMF</company>
						<department></department>
					</person>
				</factual-supervisor>
</meta-data>
			<front><?Pub Dtl?>
				<title-page>
					<copyright>
						<!--To change copyright year go to "Entities", "Text".-->
						<p> Ericsson AB 1999, 2004, 2005 - All Rights Reserved</p>
					</copyright>
					<disclaimer>
						<p>No part of this document may be reproduced in any form without the written permission of the copyright owner.</p>
						<p>The contents of this document are subject to revision without notice due to continued progress in methodology, design, and manufacturing. Ericsson shall have no liability for any error or damage of any kind resulting from the use of this document.</p>
					</disclaimer>
					<trademark-list>
						<tm-item>
							<trademark>Ericsson</trademark>
							<owner>is a trademark owned by Telefonaktiebolaget LM Ericsson.</owner>
						</tm-item>
						<tm-item>
							<trademark></trademark>
							<owner>All other product or service names
mentioned in this User Description are trademarks of their respective companies.</owner>
						</tm-item>
					</trademark-list>
				</title-page>
			</front>
			
			<body><?Pub Dtl?>
				<chl1>
					<title>Introduction</title>
					<p>Technology Package: <xsl:value-of select="document/tp_description/@description" /></p>
					<p>Reporting Universe: <xsl:value-of select="document/tp_description/@name" /></p>
					<p>File name: <xsl:value-of select="document/tp_description/@filename" />.unv</p>
				</chl1>
				<chl1><?Pub Caret?>
					<title>Universe Hierarchy</title>
					<p>This chapter describes relations between measurement objects and topology objects. 
It defines which topology can be used with each measurement.</p>
					
					<xsl:for-each select="document/tp_object_hierarchy/class">
						<xsl:if test="object = true()">
							<p>
								<emph>
									<xsl:value-of select="@link" />
								</emph>
							</p>
							<table>
								<caption></caption>
								<tgroup cols="1">								
									<tbody>
										<xsl:if test="object = true()">
											<row>
												<entry valign="top">
													<tp>
														<xsl:value-of select="@name" />
													</tp>
												</entry>
											</row>
										</xsl:if>
										<xsl:for-each select="./object">
											<row>
												<entry valign="top">
													<tp>- <xsl:value-of select="@name" /></tp>
												</entry>
											</row>
										</xsl:for-each>
										<xsl:for-each select="./class">
											<xsl:if test="@link != ../@link">
												<row>
													<entry valign="top">
														<tp>
															<emph>
																<xsl:value-of select="@link" />
															</emph>
														</tp>
													</entry>
												</row>
											</xsl:if>
											<xsl:if test="object = true()">
												<row>
													<entry valign="top">
														<tp><xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></tp>
													</entry>
												</row>
											</xsl:if>
											<xsl:for-each select="./object">
												<row>
													<entry valign="top">
														<tp>- <xsl:value-of select="@name" /></tp>
													</entry>
												</row>
											</xsl:for-each>
											<xsl:for-each select="./class">
												<xsl:if test="@link != ../@link">
													<row>
														<entry valign="top">
															<tp>
																<emph>
																	<xsl:value-of select="@link" />
																</emph>
															</tp>
														</entry>
													</row>
												</xsl:if>
												<xsl:if test="object = true()">
													<row>
														<entry valign="top">
															<tp><xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></tp>
														</entry>
													</row>
												</xsl:if>
												<xsl:for-each select="./object">
													<row>
														<entry valign="top">
															<tp>- <xsl:value-of select="@name" /></tp>
														</entry>
													</row>
												</xsl:for-each>
												<xsl:for-each select="./class">
													<xsl:if test="@link != ../@link">
														<row>
															<entry valign="top">
																<tp>
																	<emph>
																		<xsl:value-of select="@link" />
																	</emph>
																</tp>
															</entry>
														</row>
													</xsl:if>
													<xsl:if test="object = true()">
														<row>
															<entry valign="top">
																<tp><xsl:value-of select="../../../@name" /> / <xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></tp>
															</entry>
														</row>
													</xsl:if>
													<xsl:for-each select="./object">
														<row>
															<entry valign="top">
																<tp>- <xsl:value-of select="@name" /></tp>
															</entry>
														</row>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:for-each>
										</xsl:for-each>
									</tbody>
								</tgroup>
							</table>
						</xsl:if>
					</xsl:for-each>
				</chl1>
				<chl1>
					<title>Universe Objects</title>
					<p>This chapter describes the objects in the universe.</p>
					<table>
						<caption>Objects</caption>
						<tgroup cols="3">
							<tbody>
								<xsl:for-each select="document/tp_objects/class">
									<xsl:if test="object = true()">
										<row>
											<entry valign="top">
												<tp>
													<emph>
														<xsl:value-of select="@name" />
													</emph>
												</tp>
											</entry>
											<entry valign="top">
												<tp>
													<xsl:value-of select="@description" />
												</tp>
											</entry>
											<entry valign="top">
												<tp>
													<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
												</tp>
											</entry>
										</row>
									</xsl:if>
									<xsl:for-each select="./object">
										<row>
											<entry valign="top">
												<tp>
													<xsl:value-of select="@name" />
												</tp>
											</entry>
											<entry valign="top">
												<tp>
													<xsl:if test="@description != ''">
														<xsl:value-of select="@description" />
													</xsl:if>
													<xsl:if test="@description = ''">-</xsl:if>
												</tp>
											</entry>
											<entry valign="top">
												<tp>
													<xsl:if test="@aggregation = '1'">SUM</xsl:if>
													<xsl:if test="@aggregation = '2'">MAX</xsl:if>
													<xsl:if test="@aggregation = '4'">AVG</xsl:if>
													<xsl:if test="@aggregation = '6'">
														<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
													</xsl:if>
												</tp>
											</entry>
										</row>
									</xsl:for-each>
									<xsl:for-each select="./class">
										<xsl:if test="object = true()">
											<row>
												<entry valign="top">
													<tp>
														<emph>
															<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
														</emph>
													</tp>
												</entry>
												<entry valign="top">
													<tp>
														<emph>
															<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
														</emph>
													</tp>
												</entry>
												<entry valign="top">
													<tp>
														<emph>
															<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
														</emph>
													</tp>
												</entry>
											</row>
											<row>
												<entry valign="top">
													<tp>
														<emph><xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></emph>
													</tp>
												</entry>
											</row>
										</xsl:if>
										<xsl:for-each select="./object">
											<row>
												<entry valign="top">
													<tp>
														<xsl:value-of select="@name" />
													</tp>
												</entry>
												<entry valign="top">
													<tp>
														<xsl:if test="@description != ''">
															<xsl:value-of select="@description" />
														</xsl:if>
														<xsl:if test="@description = ''">-</xsl:if>
													</tp>
												</entry>
												<entry valign="top">
													<tp>
														<xsl:if test="@aggregation = '1'">SUM</xsl:if>
														<xsl:if test="@aggregation = '2'">MAX</xsl:if>
														<xsl:if test="@aggregation = '4'">AVG</xsl:if>
														<xsl:if test="@aggregation = '6'">
															<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
														</xsl:if>
													</tp>
												</entry>
											</row>
										</xsl:for-each>
										<xsl:for-each select="./class">
											<xsl:if test="object = true()">
												<row>
													<entry valign="top">
														<tp>
															<emph>
																<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
															</emph>
														</tp>
													</entry>
													<entry valign="top">
														<tp>
															<emph>
																<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
															</emph>
														</tp>
													</entry>
													<entry valign="top">
														<tp>
															<emph>
																<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
															</emph>
														</tp>
													</entry>
												</row>
												<row>
													<entry valign="top">
														<tp>
															<emph><xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></emph>
														</tp>
													</entry>
												</row>
											</xsl:if>
											<xsl:for-each select="./object">
												<row>
													<entry valign="top">
														<tp>
															<xsl:value-of select="@name" />
														</tp>
													</entry>
													<entry valign="top">
														<tp>
															<xsl:if test="@description != ''">
																<xsl:value-of select="@description" />
															</xsl:if>
															<xsl:if test="@description = ''">-</xsl:if>
														</tp>
													</entry>
													<entry valign="top">
														<tp>
															<xsl:if test="@aggregation = '1'">SUM</xsl:if>
															<xsl:if test="@aggregation = '2'">MAX</xsl:if>
															<xsl:if test="@aggregation = '4'">AVG</xsl:if>
															<xsl:if test="@aggregation = '6'">
																<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
															</xsl:if>
														</tp>
													</entry>
												</row>
											</xsl:for-each>
											<xsl:for-each select="./class">
												<xsl:if test="object = true()">
													<row>
														<entry valign="top">
															<tp>
																<emph>
																	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
																</emph>
															</tp>
														</entry>
														<entry valign="top">
															<tp>
																<emph>
																	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
																</emph>
															</tp>
														</entry>
														<entry valign="top">
															<tp>
																<emph>
																	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
																</emph>
															</tp>
														</entry>
													</row>
													<row>
														<entry valign="top">
															<tp>
																<emph><xsl:value-of select="../../../@name" /> / <xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></emph>
															</tp>
														</entry>
													</row>
												</xsl:if>
												<xsl:for-each select="./object">
													<row>
														<entry valign="top">
															<tp>
																<xsl:value-of select="@name" />
															</tp>
														</entry>
														<entry valign="top">
															<tp>
																<xsl:if test="@description != ''">
																	<xsl:value-of select="@description" />
																</xsl:if>
																<xsl:if test="@description = ''">-</xsl:if>
															</tp>
														</entry>
														<entry valign="top">
															<tp>
																<xsl:if test="@aggregation = '1'">SUM</xsl:if>
																<xsl:if test="@aggregation = '2'">MAX</xsl:if>
																<xsl:if test="@aggregation = '4'">AVG</xsl:if>
																<xsl:if test="@aggregation = '6'">
																	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
																</xsl:if>
															</tp>
														</entry>
													</row>
												</xsl:for-each>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:for-each>
							</tbody>
						</tgroup>
					</table>
				</chl1>
				<chl1>
					<title>Universe Conditions</title>
					<p>This chapter describes the conditions in the universe.</p>
					<table>
						<caption>Conditions</caption>
						<tgroup cols="2">
							<tbody>
								<xsl:for-each select="document/tp_conditions/class">
									<xsl:if test="object = true()">
										<row>
											<entry valign="top">
												<tp>
													<emph>
														<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
													</emph>
												</tp>
											</entry>
											<entry valign="top">
												<tp>
													<emph>
														<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
													</emph>
												</tp>
											</entry>
										</row>
										<row>
											<entry valign="top">
												<tp>
													<emph>
														<xsl:value-of select="@name" />
													</emph>
												</tp>
											</entry>
											<entry valign="top">
												<tp>
													<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
												</tp>
											</entry>
										</row>
									</xsl:if>
									<xsl:for-each select="./object">
										<row>
											<entry valign="top">
												<tp>
													<xsl:value-of select="@name" />
												</tp>
											</entry>
											<entry valign="top">
												<tp>
													<xsl:if test="@description != ''">
														<xsl:value-of select="@description" />
													</xsl:if>
													<xsl:if test="@description = ''">-</xsl:if>
												</tp>
											</entry>
										</row>
									</xsl:for-each>
									<xsl:for-each select="./class">
										<xsl:if test="object = true()">
											<row>
												<entry valign="top">
													<tp>
														<emph>
															<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
														</emph>
													</tp>
												</entry>
												<entry valign="top">
													<tp>
														<emph>
															<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
														</emph>
													</tp>
												</entry>
											</row>
											<row>
												<entry valign="top">
													<tp>
														<emph><xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></emph>
													</tp>
												</entry>
												<entry valign="top">
													<tp>
														<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
													</tp>
												</entry>
											</row>
										</xsl:if>
										<xsl:for-each select="./object">
											<row>
												<entry valign="top">
													<tp>
														<xsl:value-of select="@name" />
													</tp>
												</entry>
												<entry valign="top">
													<tp>
														<xsl:if test="@description != ''">
															<xsl:value-of select="@description" />
														</xsl:if>
														<xsl:if test="@description = ''">-</xsl:if>
													</tp>
												</entry>
											</row>
										</xsl:for-each>
										<xsl:for-each select="./class">
											<xsl:if test="object = true()">
												<row>
													<entry valign="top">
														<tp>
															<emph>
																<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
															</emph>
														</tp>
													</entry>
													<entry valign="top">
														<tp>
															<emph>
																<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
															</emph>
														</tp>
													</entry>
												</row>
												<row>
													<entry valign="top">
														<tp>
															<emph><xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></emph>
														</tp>
													</entry>
													<entry valign="top">
														<tp>
															<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
														</tp>
													</entry>
												</row>
											</xsl:if>
											<xsl:for-each select="./object">
												<row>
													<entry valign="top">
														<tp>
															<xsl:value-of select="@name" />
														</tp>
													</entry>
													<entry valign="top">
														<tp>
															<xsl:if test="@description != ''">
																<xsl:value-of select="@description" />
															</xsl:if>
															<xsl:if test="@description = ''">-</xsl:if>
														</tp>
													</entry>
												</row>
											</xsl:for-each>
											<xsl:for-each select="./class">
												<xsl:if test="object = true()">
													<row>
														<entry valign="top">
															<tp>
																<emph>
																	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
																</emph>
															</tp>
														</entry>
														<entry valign="top">
															<tp>
																<emph>
																	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
																</emph>
															</tp>
														</entry>
													</row>
													<row>
														<entry valign="top">
															<tp>
																<emph><xsl:value-of select="../../../@name" /> / <xsl:value-of select="../../@name" /> / <xsl:value-of select="../@name" /> / <xsl:value-of select="@name" /></emph>
															</tp>
														</entry>
														<entry valign="top">
															<tp>
																<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
															</tp>
														</entry>
													</row>
												</xsl:if>
												<xsl:for-each select="./object">
													<row>
														<entry valign="top">
															<tp>
																<xsl:value-of select="@name" />
															</tp>
														</entry>
														<entry valign="top">
															<tp>
																<xsl:if test="@description != ''">
																	<xsl:value-of select="@description" />
																</xsl:if>
																<xsl:if test="@description = ''">-</xsl:if>
															</tp>
														</entry>
													</row>
												</xsl:for-each>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:for-each>
							</tbody>
						</tgroup>
					</table>
				</chl1>
			</body>
			<back><?Pub Dtl?>
				<glossary>
					<glossary-list>
						<gl-item>
							<term>ENIQ</term>
							<expansion>
								<p>Ericsson Network IQ</p>
							</expansion>
						</gl-item>
					</glossary-list>
				</glossary>
				<reference>
					<reference-list>
						<rf-subsection></rf-subsection>
						<rf-item xml:id="rf-OSSGlossary">
							<rf-title>
								<ulink xlink:type="simple" xlink:href="urn:x-ericsson:r2:reg-doc:*0033-*:*:*?title=Operations Support System (OSS) Glossary"
									xlink:title="Operations Support System (OSS) Glossary" xmlns:xlink="http://www.w3.org/1999/xlink"></ulink>
							</rf-title>
						</rf-item>
					</reference-list>
				</reference>
			</back>
			</doc>
		<?Pub Caret?>
<?Pub *0000587307 0?>
	</xsl:template>

</xsl:stylesheet>
