var C26=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">26 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_3jed\"></a><a name=\"CHAPTER26\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Database Users</a></span></h1>\n" +
"\n" +
"<p>\n" +
"<a name=\"tbl-DatabaseUsers\" href='javascript:parent.parent.parent.showAnchor(\"tbl-DatabaseUsers\")' class=\"xref\"> Table 20</a>, describes the Database Users\n" +
"available in ENIQ.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE20\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 20 &nbsp;&nbsp; Database Users</caption>\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Username</strong></p></th>\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Database</strong></p></th>\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Use Purpose</strong></p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">dc</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>DWH</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>ETLC user to Data Warehouse database.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">dcpublic</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>DWH</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>SQL Interface user to Data Warehouse database. Limited\n" +
"user rights. Default password is <tt class=\"file-path\">dcpublic</tt>.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">dcbo</tt></p>\n" +
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
"<p><tt class=\"file-path\">dc </tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p> DWH </p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>NetAnServer user to Data Warehouse database </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">etlrep</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Repository</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>ETLC user to ETL repository database.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">dwhrep</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Repository</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>ETLC user to DWH repository database.</p>\n" +
"</td></tr></table>\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">During an upgrade ensure that there are no open connections\n" +
"running towards the database. </li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Only the <tt class=\"file-path\">dcbo</tt> user ID should\n" +
"be used for the EBID connection.</li></ul></dd></dl><br />\n" +
"<p>\n" +
"To identify open connections:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Using SQL client software logon to the ENIQ database server\n" +
"or in a multi-blade deployment logon to the <tt class=\"file-path\">dwh_reader_2\n" +
"blade</tt>. Database administrator rights are required to\n" +
"see open connections.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the iqconnecton procedure:<br /><tt class=\"input\"><b>sp_iqconnection</b></tt> </li></ol>\n" +
"\n"+
"</div>\n";

var C26_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_3pcv\"></a><a name=\"CHAPTER26.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing Database User Passwords</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Use the <tt class=\"file-path\">change_db_password.bsh</tt> script\n" +
"to change the password of SAP IQ database users (<tt class=\"file-path\">dc</tt>, <tt class=\"file-path\">dba</tt>, <tt class=\"file-path\">dcbo</tt>, or <tt class=\"file-path\">dcpublic</tt>).</p>\n" +
"\n" +
"<p>\n" +
"Usage:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"file-path\">bash change_db_password.bsh -u &lt;<i class=\"var\">UserName</i>&gt; -p &lt;<i class=\"var\">DBA_Password</i>&gt; [-E &lt;<i class=\"var\">Y/N</i>&gt;] [ -l &lt;<i class=\"var\">path_to_logfile</i>&gt; ]</tt></p>\n" +
"\n" +
"<p>\n" +
"Options: </p>\n" +
"\n" +
"<p>\n" +
"-u  : Mandatory parameter.Specifies Username.Username is not case-sensitive.\n" +
" </p>\n" +
"\n" +
"<p>\n" +
"-p  : Mandatory parameter.Specifies DBA password.Password is case-sensitive.\n" +
" </p>\n" +
"\n" +
"<p>\n" +
"-E : Optional Parameter to indicate if new password should be encrypted\n" +
"while being set.</p>\n" +
"\n" +
"<p>\n" +
"Y or N are the only valid entries when the option is used.</p>\n" +
"\n" +
"<p>\n" +
" -l  : Optional Parameter.Specifies logfile name with absolute\n" +
"path. Default path /eniq/log/sw_log/iq.</p>\n" +
"\n" +
"<p>\n" +
"To change database user&rsquo;s passwords, run the commands on\n" +
"following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"> ENIQ Statistics Coordinator</li></ul>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Stop all services across the deployment.<p>\n" +
"<tt class=\"input\"><b>{root} #: bash /eniq/admin/bin/manage_deployment_services.bsh\n" +
"-a stop -s ALL</b></tt> </p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Start the <tt class=\"file-path\">repdb</tt> and <tt class=\"file-path\">dwhdb</tt> service on the coordinator blade.<p>\n" +
"<tt class=\"input\"><b>{root} #: bash /eniq/admin/bin/manage_eniq_services.bsh\n" +
"-a start -s dwhdb,repdb</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Change the password for a specific database user using <tt class=\"file-path\">change_db_password</tt> script.<p>\n" +
"<tt class=\"input\"><b>{root} #: cd /eniq/admin/bin</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>{root} #: bash change_db_password.bsh -u <i class=\"var\">&lt;UserName&gt;</i> -p <i class=\"var\">&lt;DBA_Password&gt;</i> </b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Start all services across the deployment.<p>\n" +
"<tt class=\"input\"><b>{root} #: bash /eniq/admin/bin/manage_deployment_services.bsh\n" +
"-a start -s ALL</b></tt></p>\n" +
"</li></ol>\n" +
"<div class=\"example\" style=\"margin-top: 12pt\"><p><i>Example 3 &nbsp; Changing Password for DCBO</i></p><pre class=\"prencd\">[eniq_stats]{root} #: bash change_db_password.bsh -u\n" +
"dcbo -p &lt;DBA_Password&gt; \n" +
"============================&lt; Password Change &gt;===========================\n" +
"\n" +
"Password Policies:\n" +
"\n" +
"** Minimum password length 2 characters.\n" +
"** Maximum password length 30 characters.\n" +
"** All alphanumeric characters allowed.\n" +
"** The following special characters are allowed # % ~ _ + - : ! * = { } , . /\n" +
"** No spaces allowed.\n" +
"\n" +
"\n" +
"Enter the new password for DCBO user:\n" +
"Please re-enter the new password to confirm the change:\n" +
"\n" +
"Are you sure you wish to change the SAP IQ password for DCBO user ? [Yes/No]: Yes\n" +
"\n" +
"\n" +
"====================&lt; Updating password for DCBO user &gt;===================\n" +
"\n" +
"2016-08-30_13.00.19 - Successfully updated the password for DCBO user in core install software.\n" +
"2016-08-30_13.00.21 - Successfully updated the password for DCBO user in SAP IQ.\n" +
"2016-08-30_13.00.23 - Successfully updated the password for DCBO user in SAP ASA.\n" +
"\n" +
"PASSWORD CHANGE SUCCESSFUL...\n" +
"\n" +
"The password for DCBO user has been successfully changed.\n" +
"</pre></div>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Contact the Ericsson support team if you encounter issues\n" +
"changing the DBA password or if you forget the DBA password.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">If you change database passwords you may need to update\n" +
"any third-party tools used to connect to the database.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">If &lsquo;dcbo&rsquo; password is changed, please follow\n" +
"the procedure in &ldquo;Update Parameters of Existing DSN&rdquo; in <a href='javascript:parent.parent.parent.showAnchor(\"Ebidinstall\")' class=\"xref\">Reference [18]</a>  for the updates to be done in Ericsson Business\n" +
"Intelligence Deployment.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">If password of dcpublic user of ENIQ-S DB is changed,\n" +
"password of dcpublic used by OSS-RC application has to be updated\n" +
"as well. Please refer section Password Administration of Account neuser\n" +
"in Overall Security Administration in OSS-RC <a href='javascript:parent.parent.parent.showAnchor(\"OSSChangePassword\")' class=\"xref\">Reference [28]</a> for changing password of dcpublic user\n" +
"on OSS-RC.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Change of dcuser password should be updated in TSS under\n" +
"OSS-RC as well. This will enable OSS-RC applications [NSD-EPC, NSD,\n" +
"RNO ] to have changed password to access  Eniq Stats. For procedure\n" +
"to change the password on OSS-RC, refer to section &ldquo;Using the\n" +
"TSS CLI for Password Administration&rdquo; in Telecom Security Services,\n" +
"TSS, System Administration Guide. <a href='javascript:parent.parent.parent.showAnchor(\"TSSsystemAdminGuide\")' class=\"xref\">Reference [30]</a></li></ul></dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

