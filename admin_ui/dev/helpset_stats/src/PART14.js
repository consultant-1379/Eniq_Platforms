var C14=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">14 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ew9n\"></a><a name=\"CHAPTER14\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing IPMP Settings for the ENIQ \n" +
"Statistics - ENM Storage VLAN</a></span></h1>\n" +
"\n" +
"<p>\n" +
"If ENM is integrated with ENIQ Statistics besides OSS-RC, an extra\n" +
"IPMP interface is configured on the  ENIQ blade to connect to the\n" +
"ENM storage VLAN.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>To configure the new interface refer to Section 5.1.4 in <em class=\"LOWEMPH\">OSS-RC Configuration for ENIQ</em>, <a href='javascript:parent.parent.parent.showAnchor(\"OSSRCConfigForEniq\")' class=\"xref\">Reference [23]</a>. </dd></dl><br />\n" +
"<p>\n" +
"If any changes are required in IPMP settings (ip or netmask), it\n" +
"is essential to rollback the current IPMP settings and reconfigure\n" +
"the new interface. For this, following steps should be performed:</p>\n" +
"\n" +
"<p>\n" +
"Log on as <tt class=\"file-path\">root</tt> user on the ENIQ blade\n" +
"having the issue.</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run below steps for Solaris 10 Servers\n" +
"<ol type=\"a\">\n" +
"\n" +
"<li class=\"substep\">Remove hostname. <tt class=\"file-path\">&lt;interface&gt;</tt> files of the interfaces used to configure ENM Storage group from\n" +
"the following path:<br /> <tt class=\"input\"><b>/etc/</b></tt>. <tt class=\"input\"><b>hostname.bnxe6</b></tt> is an example of the <tt class=\"file-path\">hostname.&lt;interface&gt;</tt> file.\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\">Unplumb the particular interfaces used for configuring\n" +
"ENM Storage group.\n" +
"<p>\n" +
"<tt class=\"input\"><b># ifconfig &lt;<i class=\"var\">interface_name</i>&gt; unplumb</b></tt></p>\n" +
"\n" +
"</li>\n" +
"</ol></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run below step in case of Solaris 11 Servers\n" +
"<ol type=\"a\">\n" +
"\n" +
"<li class=\"substep\">Unplumb the particular interfaces used for configuring\n" +
"ENM Storage group.\n" +
"<p>\n" +
"<tt class=\"input\"><b># ipadm delete-ip &lt;<i class=\"var\">interface_name</i>></b></tt></p>\n" +
"\n" +
"</li>\n" +
"</ol></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Following are the steps to Rollback configurations from\n" +
"ipmp.ini file:\n" +
"<ol type=\"a\">\n" +
"\n" +
"<li class=\"substep\"><tt class=\"input\"><b># mkdir /tmp/ipmp_rollback</b></tt>\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\"><tt class=\"input\"><b># cp /eniq/installation/config/ipmp.ini /tmp/ipmp_rollback/ipmp.ini</b></tt>\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\"><tt class=\"input\"><b># /eniq/installation/core_install/lib/inidel.pl\n" +
"-p PROBE_BASED_IPMP_ENM -i /tmp/ipmp_rollback/ipmp.ini -o /tmp/ipmp_rollback/ipmp.ini.1</b></tt>\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\"><tt class=\"input\"><b># /eniq/installation/core_install/lib/inidel.pl\n" +
"-p ENM_STOR_NETMASK -i /tmp/ipmp_rollback/ipmp.ini.1 -o /tmp/ipmp_rollback/ipmp.ini.2</b></tt>\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\"><tt class=\"input\"><b># /eniq/installation/core_install/lib/inidel.pl\n" +
"-g IPMP -p IPMP_INTF_3 -i /tmp/ipmp_rollback/ipmp.ini.2 -o /tmp/ipmp_rollback/ipmp.ini.3</b></tt>\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\"><tt class=\"input\"><b># mv /tmp/ipmp_rollback/ipmp.ini.3 /eniq/installation/config/ipmp.ini</b></tt>\n" +
"</li>\n" +
"</ol></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">To reconfigure the new interface, execute the steps as\n" +
"mentioned in Section 5.1.4 in <em class=\"LOWEMPH\">OSS-RC Configuration\n" +
"for ENIQ.</em>, <a href='javascript:parent.parent.parent.showAnchor(\"OSSRCConfigForEniq\")' class=\"xref\">Reference [23]</a></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"> After the new interface is configured successfully, run\n" +
"following command to remove backup ipmp.ini file.<p>\n" +
"<tt class=\"input\"><b># rm -rf /tmp/ipmp_rollback </b></tt></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

