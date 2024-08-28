var C21=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">21 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_3jed\"></a><a name=\"CHAPTER21\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Database Users</a></span></h1>\n" +
"\n" +
"<p><a name=\"tbl-DatabaseUsers\"></a>\n" +
"The following database users are available:</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE18\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 18 &nbsp;&nbsp; Database Users</caption>\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">User Name</strong></p></th>\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Database</strong></p></th>\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Use Purpose</strong></p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>dc</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>DWH</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>ETLC user to Data Warehouse database.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>dcpublic</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>DWH</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>SQL Interface user to Data Warehouse database. Limited\n" +
"user rights. Default password is &quot;dcpublic&quot;.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>dcbo</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>DWH</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Business Object (BO) user to Data Warehouse database. Limited\n" +
"user rights.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p> dc </p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p> DWH </p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>NetAnServer user to Data Warehouse database </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>etlrep</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Repository</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>ETLC user to ETL repository database.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>dwhrep</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Repository</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>ETLC user to DWH repository database.</p>\n" +
"</td></tr></table>\n" +
"<p>\n" +
"Contact your local Ericsson Customer Support, if you want to change\n" +
"your SAP IQ password.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>No open connections should be running towards the database\n" +
"during an upgrade. Only  the <tt class=\"file-path\"> dcbo</tt> user\n" +
"ID should be used for the EBID connection.</dd></dl><br />\n" +
"<p>\n" +
"Open connections can be listed as follows:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to the ENIQ database server or in the case of a\n" +
"multi blade deployment on the dwh_reader_2 blade with your SQL client\n" +
"software . Database administrator rights are required to see open\n" +
"connections.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the iqconnecton procedure:<br /><tt class=\"input\"><b>sp_iqconnection</b></tt> </li></ol>\n" +
"\n"+
"</div>\n";

