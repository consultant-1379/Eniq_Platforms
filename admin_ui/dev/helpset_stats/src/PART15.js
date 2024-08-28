var C15=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">15 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rp88\"></a><a name=\"CHAPTER15\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Migrating ENIQ to IPv6 and Probe Based\n" +
"IPMP</a></span></h1>\n" +
"\n" +
"<p>\n" +
"ENIQ supports IPv6 for PM Services VLAN .</p>\n" +
"\n" +
"<p>\n" +
" The following steps describe how to add an IPv6 address to ENIQ\n" +
"Blade for PM Services VLAN or how to migrate the existing IPv4 Addresses\n" +
"to Probe Based IPMP.</p>\n" +
"\n" +
"<p>\n" +
"To enable IPv6 on ENIQ or to migrate to Probe Based IPMP run the\n" +
"following commands:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># cd /eniq/installation/core_install/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b> # ./ipv6_ipmp_upgrade.bsh</b></tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd></dd></dl><br />\n" +
"<p>\n" +
"The <tt class=\"file-path\">ipv6_ipmp_upgrade.bsh</tt> script migrates\n" +
"the IPMP configuration from Link Based to Probe Based. If IPv6 is\n" +
"not currently configured, the following output is received</p>\n" +
"\n" +
"<p><tt class=\"output\">Do you want to Configure IPv6 (Y|N)?</tt></p>\n" +
"<p>\n" +
"Select <tt class=\"input\"><b>Y</b></tt> to configure IPv6</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"output\">Do you want to enable Probe Based IPMP (Y|N)?</tt></p>\n" +
"\n" +
"<p>\n" +
"Select <tt class=\"input\"><b>Y</b></tt> as this is the supported Ericsson Configuration\n" +
"for ENIQ.</p>\n" +
"\n" +
"<p>\n" +
"For all other IP questions, refer to the IPMP Configuration section\n" +
"in the relevant ENIQ Installation Instructions, <a href='javascript:parent.parent.parent.showAnchor(\"bladeinstall\")' class=\"xref\">Reference [21]</a></p>\n" +
"\n" +
"<p>\n" +
"A reboot is required before this update takes effect</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Ignore the following error messages if they are received\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"output\">ifconfig: unplumb: SIOCLIFREMOVEIF:</tt></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><tt class=\"output\">ifconfig: unplumb: SIOCLIFREMOVEIF: bnxe4:1:\n" +
"no such interface 8</tt></li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

