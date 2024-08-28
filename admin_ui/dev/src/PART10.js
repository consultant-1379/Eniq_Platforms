var C10=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">10 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4fxr\"></a><a name=\"CHAPTER10\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Manage ENIQ Mounts</a></span></h1>\n" +
"\n"+
"</div>\n";

var C10_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">10.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_og34\"></a><a name=\"CHAPTER10.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Update the current mounted filesystems</a></span></h2>\n" +
"\n" +
"<p>\n" +
"It is possible to update ENIQ so that it mounts different filesystems\n" +
"on the OSS. This functionality will allow administrators to mount\n" +
"up different network types and parse the data, without having to stop\n" +
"ENIQ's normal operation</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"To update the filesystems that ENIQ mounts, complete the following\n" +
"commands as user '<em class=\"LOWEMPH\">root</em>'</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\"># cd /eniq/connectd/bin</strong></p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\"># bash ./manage_mountpoints.bsh -a update -o\n" +
"&lt;eniq_oss_alias&gt; [-l &lt;path_to_logfile&gt;]</strong></p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Select from the displayed options, which filesystem(s) to mount</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The list below is an example only, depending on your installation\n" +
"(rack or blade), the displayed file systems will be a different format</dd></dl><br />\n" +
"<p>\n" +
"<tt class=\"LITERALMONO\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Description &nbsp; &nbsp; &nbsp;Filesystem</tt><br />\n" +
"<tt class=\"LITERALMONO\">[1] &nbsp; &nbsp; &nbsp;NWS_SGw &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;/ossrc/data/sgw/sgwcg &nbsp; &nbsp; &nbsp; (*)</tt><br />\n" +
"<tt class=\"LITERALMONO\">[2] &nbsp; &nbsp; &nbsp;NWS_PDM &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;/ossrc/ericsson</tt><br />\n" +
"<tt class=\"LITERALMONO\">[3] &nbsp; &nbsp; &nbsp;NWS_PMS &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;/ossrc/data/pms/segment1</tt><br />\n" +
"<tt class=\"LITERALMONO\">[4] &nbsp; &nbsp; &nbsp;EBA_EBSS &nbsp; &nbsp; &nbsp; &nbsp; /ossrc/data/eba/eba_ebss</tt><br />\n" +
"<tt class=\"LITERALMONO\">[5] &nbsp; &nbsp; &nbsp;EBA_EBSW &nbsp; &nbsp; &nbsp; &nbsp; /ossrc/data/eba/eba_ebsw</tt><br />\n" +
"<tt class=\"LITERALMONO\">[6] &nbsp; &nbsp; &nbsp;EBA_EBSG &nbsp; &nbsp; &nbsp; &nbsp; /ossrc/data/eba/eba_rsdm</tt><br />\n" +
"<tt class=\"LITERALMONO\"></tt><br />\n" +
"<tt class=\"LITERALMONO\">(*) indicates the filesystem is already mounted</tt><br />\n" +
"<tt class=\"LITERALMONO\">Already mounted filesystems will be unmounted if not re-selected</tt><br />\n" +
"<tt class=\"LITERALMONO\"></tt><br />\n" +
"<tt class=\"LITERALMONO\">Select range of filesystems you wish to mount [n,n-n,n...n]</tt><br />\n" +
"<tt class=\"LITERALMONO\">&nbsp; &nbsp; &nbsp; &nbsp; E.G. 1,3-5,6</tt></p>\n" +
"\n" +
"<p>\n" +
"Select 'Y' or 'y' on the confirmation screen if you are satisfied\n" +
"with the selections made. </p>\n" +
"\n" +
"<p>\n" +
"To check if the new filesystems have mounted, monitor the logs\n" +
"stored in - <strong class=\"MEDEMPH\">/eniq/local_logs/connectd/</strong></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C10_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">10.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_foq0\"></a><a name=\"CHAPTER10.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Manage mount system</a></span></h2>\n" +
"\n" +
"<p>\n" +
"ENIQ's default mount system is to mount the OSS filesystems directly\n" +
"via NFS. However, it is possible to change this setup and mount through\n" +
"an ssh tunnel. This scenario gives increased security to the data\n" +
"as it is encrypted, but the amount of WRAN data that can be collected\n" +
"is limited. </p>\n" +
"\n" +
"<p>\n" +
"To enable the ssh tunnel for a particular OSS, complete the following\n" +
"steps as user 'root'</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>For Blade and Multi Blade deployments, see <a href='javascript:parent.parent.parent.showAnchor(\"ConnectdMonitor\")' class=\"xref\"> Section  22</a>Chapter \"Connectd Monitor Daemon\" for information\n" +
"on how the OSS is mounted.</dd></dl><br />\n" +
"<p><tt class=\"input\"><b># cd /eniq/connectd/mount_info/</b></tt></p>\n" +
"<p><tt class=\"input\"><b># /usr/bin/touch &lt;eniq_oss_alias&gt;/disable_OSS</b></tt></p>\n" +
"<p>\n" +
"Please wait for the system to unmount the filesystems before continuing</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /usr/bin/touch &lt;eniq_oss_alias&gt;/use_tunnel</b></tt></p>\n" +
"<p><tt class=\"input\"><b># /usr/bin/rm &lt;eniq_oss_alias&gt;/disable_OSS</b></tt></p>\n" +
"<p>\n" +
"To disable an ssh tunnel for a particular OSS, complete the following\n" +
"steps as user 'root'</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># cd /eniq/connectd/mount_info/</b></tt></p>\n" +
"<p><tt class=\"input\"><b># /usr/bin/touch &lt;eniq_oss_alias&gt;/disable_OSS</b></tt></p>\n" +
"<p>\n" +
"Please wait for the system to unmount the filesystems before continuing</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /usr/bin/rm &lt;eniq_oss_alias&gt;/use_tunnel</b></tt></p>\n" +
"<p><tt class=\"input\"><b># /usr/bin/rm &lt;eniq_oss_alias&gt;/disable_OSS</b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

