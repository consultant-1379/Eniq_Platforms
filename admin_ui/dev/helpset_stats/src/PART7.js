var C7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><a name=\"BusinessIntellServer\"></a><span class=\"CHAPNUMBER\">7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_uk7p\"></a><a name=\"CHAPTER7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Business\n" +
"Intelligence Server</a></span></h1>\n" +
"\n" +
"<p>\n" +
"Ericsson Network IQ Web Portal Graphical User Interface is used\n" +
"by end users to access the Ericsson Network IQ database through Business\n" +
"Intelligence Server.</p>\n" +
"\n" +
"<p>\n" +
"ENIQ Web Portal brings the following additional features to BusinessObjects\n" +
"BILaunchPad:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Storing of prompt values</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Navigating and grouping reports</li></ul>\n" +
"<p>\n" +
"For more information on standard BusinessObjects functionality,\n" +
"see <a href='javascript:parent.parent.parent.showAnchor(\"rf-BO\")' class=\"xref\">Reference [5]</a>.</p>\n" +
"\n" +
"<p>\n" +
"For more information on navigating and grouping reports, see <a href='javascript:parent.parent.parent.showAnchor(\"rf-ENIQUG\")' class=\"xref\">Reference [6]</a>.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C7_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">7.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_cbbd\"></a><a name=\"CHAPTER7.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Setting Date Prompt Rules</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The date rules settings file is an ASCII configuration file. By\n" +
"default, the file is at:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"file-path\">&lt;Tomcat_Installation_Dir&gt;\\webapps<br />\\AnalyticalReporting\\webportal\\prompt\\defaultdate.properties</tt></p>\n" +
"\n" +
"<p>\n" +
"The rules are listed as name and value pairs in consecutive lines.\n" +
"You can edit the default rules in the file using any operating system\n" +
"text editor. Rule format is:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b><i class=\"var\">&lt;Prompt name&gt;</i>= <i class=\"var\">&lt;Rule&gt;</i></b></tt></p>\n" +
"\n" +
"<p>\n" +
"Where <tt class=\"file-path\">Rule</tt>is a string that includes <tt class=\"file-path\">lastworkingday</tt>keyword and/or a counting rule. <tt class=\"LITERALMONO\">lastworkingday</tt> denotes the last working day calculated\n" +
"backwards from the current day. The counting rule is:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>[-]&lt;number&gt;[d,w,m]</b></tt></p>\n" +
"\n" +
"<p>\n" +
"The rule specifies how many days, weeks, or months from the current\n" +
"day (or the last working day if the keyword <tt class=\"LITERALMONO\">lastworkingday</tt> is used) are calculated. This new date is the default prompt value.\n" +
"For example:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>Date:=lastworkingday(-2w)</b></tt></p>\n" +
"\n" +
"<p>\n" +
"Means that the default value of the <b>Date:</b> prompt\n" +
"is two weeks back from the last working day.</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>Next date:=(+2d)</b></tt></p>\n" +
"\n" +
"<p>\n" +
"Means that the default value of the <b>Next date:</b> prompt is two days forward from the current date.</p>\n" +
"\n" +
"<p>\n" +
"The date rule file can also include a special name value pair:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>weekend=&lt;SATURDAY,SUNDAY&gt;</b></tt></p>\n" +
"\n" +
"<p>\n" +
"This lists the non-working days that are skipped when the last\n" +
"working day is calculated. The possible values are:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>SUNDAY<br />MONDAY<br />TUESDAY<br />WEDNESDAY<br />THURSDAY<br />FRIDAY<br />SATURDAY</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If the weekend definition row is missing from the date rule settings\n" +
"file, the keyword <tt class=\"LITERALMONO\">lastworkingday</tt> always means the\n" +
"previous day.</p>\n" +
"\n" +
"<p>\n" +
"The default date rules setting file is: </p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"LITERALMONO\">weekend = SATURDAY, SUNDAY</tt><br />\n" +
"<tt class=\"LITERALMONO\">Date:=lastworkingday</tt><br />\n" +
"<tt class=\"LITERALMONO\">Date(s):=lastworkingday</tt><br />\n" +
"<tt class=\"LITERALMONO\">First date:=lastworkingday(-1w)</tt><br />\n" +
"<tt class=\"LITERALMONO\">Last Date:=lastworkingday</tt><br />\n" +
"<tt class=\"LITERALMONO\">First Date (3m):=lastworkingday(-3m)</tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C7_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">7.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_pfei\"></a><a name=\"CHAPTER7.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Uninstalling Webportal</a></span></h2>\n" +
"\n" +
"<p>\n" +
"From 16A onwards webportal is no longer supported. The following\n" +
"procedure describes how to uninstall webportal.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"> To uninstall ENIQ Web Portal, run <tt class=\"file-path\">uninsXXX.exe</tt> which by default is found in <tt class=\"file-path\">%ProgramFiles%\\Ericsson\\NetworkIQ\\</tt> folder.</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Uninstalling the application removes the files which were\n" +
"modified during the installation from the web application folder.</dd></dl><br />\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Once the application gets uninstalled, the <tt class=\"file-path\">web.xml</tt> files has to be restored back in the below\n" +
"paths.<br /><b>%ProgramFiles%\\SAPBusinessObjects\\tomcat\\webapps\\BOE\\WEB-INF\\eclipse\\plugins\\webpath.InfoView\\web\\WEB-INF\\ </b><b>%ProgramFiles%\\SAPBusinessObjects\\tomcat\\webapps\\BOE\\WEB-INF\\eclipse\\plugins\\webpath.PlatformServices\\web\\WEB-INF\\ </b><b>%ProgramFiles%\\SAPBusinessObjects\\tomcat\\webapps\\BOE\\WEB-INF\\eclipse\\plugins\\webpath.AnalyticalReporting\\web\\WEB-INF\\</b><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The backup file is the one which was used before Web Portal\n" +
"was installed. Rename the <tt class=\"file-path\">web.xml.backup <i class=\"var\">&lt;TIMESTAMP&gt;</i></tt> to <tt class=\"file-path\">web.xml</tt>.</dd></dl><br /></li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Stop the service, <p>\n" +
"To stop Tomcat:</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">In the Windows Start menu, Programs-&gt;SAP Business Intelligence-&gt;SAP\n" +
"BusinessObjects BI platform 4-&gt;Central Configuration Manager</li></ul><br />\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Select &ldquo; Apache Tomcat for BI 4&rdquo; and click\n" +
"the stop button</li></ul></li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Go to &lt;&lt;Tomcat Installation Directory &gt;&gt; - &gt;work-&gt;Catalina-&gt;localhost\n" +
"and delete the BOE folder which removes the BOE cache.</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Delete the cache details from the browser, which would\n" +
"be used in launching web portal (for example, IE, Firefox, and Chrome). </li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">After the uninstallation is complete start the service<p>\n" +
"To start Tomcat:</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">In the Windows Start menu, Programs-&gt;SAP Business Intelligence-\n" +
"&gt;SAP BusinessObjects BI platform 4-&gt;Central Configuration Manager</li></ul><br />\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Select &ldquo;Apache Tomcat for BI 4&rdquo; and click\n" +
"the start button</li></ul><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>This might take a while to actually start even though the\n" +
"status is shown as &ldquo;running&rdquo;. The service might not be\n" +
"actually running yet.</dd></dl><br /></li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Check that uninstallation worked. Open browser and test\n" +
"the installation (<tt class=\"file-path\">http://localhost:8080/BOE/BI</tt>). Add this site to the &ldquo;Trusted sites&rdquo; list. </li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Cache rebuild should be completed before launching the BI\n" +
"Launch Pad. If you receive an error while launching the ENIQ Web portal\n" +
"it means that the cache is not built yet. Wait for some time and retry\n" +
"to see the logon page</dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

