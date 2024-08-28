var C9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ruhd\"></a><a name=\"CHAPTER9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Updating OSS IP Address</a></span></h1>\n" +
"\n" +
"<p>\n" +
"If you need to change the IP Address of the OSS alias, run the\n" +
"commands below on the following server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator</li></ul>\n" +
"<p>\n" +
"<tt class=\"input\"><b>#[root] cd /eniq/admin/bin/ </b></tt></p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>#[root] bash ./manage_oss_ip.bsh -h  <i class=\"var\">&lt;eniq_alias&gt;</i> [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The &lt;eniq_alias&gt; is a parameter used to state what OSS\n" +
"host ENIQ is connected to. The specified hostname alias should be\n" +
"in the correct format. The alias information is located in <em class=\"LOWEMPH\">/etc/hosts.</em>The following are examples of the alias\n" +
"types. \n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">eniq_events_&lt;n&gt; or eniq_oss_&lt;n&gt;</li></ul></dd></dl><br />\n" +
"<p>\n" +
"When prompted enter the new IP address of the hostname alias. </p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>Enter the IP address associated with eniq_oss_1</b></tt></p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>Press enter for the default [1.1.1.1]</b></tt></p>\n" +
"\n" +
"<p>\n" +
"You are asked to confirm the details entered. Select &ldquo;Y&rdquo;\n" +
"or &ldquo;y&rdquo; to make the updates.	</p>\n" +
"\n" +
"<p><tt class=\"output\">Do you wish to continue to update the OSS Server details (Yy/Nn)</tt></p>\n" +
"<p>\n" +
"SPARC OSS systems must be remounted. Refer to chapter 6 \"Mount\n" +
"OSS_RC Master Server on ENIQ\" in <a href='javascript:parent.parent.parent.showAnchor(\"OSSRCConfigForEniq\")' class=\"xref\">Reference [23]</a> for instructions.</p>\n" +
"\n" +
"\n"+
"</div>\n";

