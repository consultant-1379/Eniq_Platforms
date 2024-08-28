var C13=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">13 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rp88\"></a><a name=\"CHAPTER13\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Migrating ENIQ to IPv6 and Probe Based\n" +
"IPMP</a></span></h1>\n" +
"\n" +
"<p>\n" +
"To enable IPv6 on ENIQ or to migrate to Probe Based IPMP run the\n" +
"following commands:</p>\n" +
"\n" +
"<p><tt class=\"file-path\"># cd /eniq/installation/core_install/bin</tt></p>\n" +
"<p><tt class=\"file-path\"> # ./ipv6_ipmp_upgrade.bsh</tt></p>\n" +
"<p>\n" +
"Note: Running the following script migrates the IPMP configuration\n" +
"from Link Based to Probe Based. If IPv6 is not currently configured,\n" +
"the following exchange occurs:</p>\n" +
"\n" +
"<p><tt class=\"file-path\">Do you want to Configure IPv6 (Y|N)?</tt></p>\n" +
"<p>\n" +
"Select Y to configure IPv6</p>\n" +
"\n" +
"<p><tt class=\"file-path\">Do you want to enable Probe Based IPMP (Y|N)?</tt></p>\n" +
"<p>\n" +
"Select Y as this is the supported Ericsson Configuration for ENIQ.</p>\n" +
"\n" +
"<p>\n" +
"For all other IP questions, refer to the IPMP Configuration section\n" +
"in the relevant ENIQ Installation Instructions, <a href='javascript:parent.parent.parent.showAnchor(\"bladeinstall\")' class=\"xref\">Reference [20]</a></p>\n" +
"\n" +
"<p>\n" +
"A reboot will be required before this update takes effect</p>\n" +
"\n" +
"<p>\n" +
"Note: The following error message(s), if displayed, can be ignored:</p>\n" +
"\n" +
"<p><tt class=\"file-path\">ifconfig: unplumb: SIOCLIFREMOVEIF:</tt></p>\n" +
"<p>\n" +
"Example:</p>\n" +
"\n" +
"<p><tt class=\"file-path\">ifconfig: unplumb: SIOCLIFREMOVEIF: bnxe4:1: no such\n" +
"interface 8</tt></p>\n" +
"\n"+
"</div>\n";

