var C25=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">25 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_a0b4\"></a><a name=\"CHAPTER25\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Customization of Historical CM for ENIQ\n" +
"Statistics</a></span></h1>\n" +
"\n" +
"<p>\n" +
"In ENIQ Statistics the optional Historical CM Tech Pack feature\n" +
"requires that BCG exports are initiated on OSS-RC/ENM and that the\n" +
"resulting export files are transferred to ENIQ.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C25_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">25.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rqwc\"></a><a name=\"CHAPTER25.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">OSS-RC</a></span></h2>\n" +
"\n" +
"<p>\n" +
" ENIQ-M in OSS-RC provides the <tt class=\"file-path\">/opt/ericsson/eniqm/bin/extract_eniqcm.sh</tt> script which initiates an export in BCG and then transfers the export\n" +
"file to ENIQ. By default, in the OSS-RC <tt class=\"file-path\">crontab</tt> the <tt class=\"file-path\">extract_eniqcm.sh</tt> script is scheduled\n" +
"to run daily. </p>\n" +
"\n" +
"<p>\n" +
"The following system customizations maybe be required (these all\n" +
"require modifications on the OSS-RC):</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><strong class=\"MEDEMPH\">Reusing an Existing Scheduled BCG Export</strong><p>\n" +
"If a full scheduled BCG export already exists in OSS-RC, then,\n" +
"it is preferred that the output of that export is used instead of\n" +
"initiating a new BCG export. This requires the modifications to the <tt class=\"file-path\">extract_eniqcm.sh</tt> script.</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\"> Before editing the <tt class=\"file-path\">extract_eniqcm.sh</tt> script create a backup version of the script in directory <tt class=\"output\">/opt/ericsson/eniqm/bin</tt> on OSS-RC. </li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Change the <tt class=\"file-path\">FILEPATH</tt> and <tt class=\"file-path\">FILESTAMP</tt> parameters to match the output of the existing\n" +
"scheduled export. </li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Comment out the <tt class=\"file-path\">Export</tt> section\n" +
"of the script.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Ensure that the output of the existing scheduled export\n" +
"is not deleted by another process. <p>\n" +
"Ensure, that in the <tt class=\"file-path\">crontab</tt> that the <tt class=\"file-path\">extract_eniqcm.sh</tt> script is scheduled so that the output\n" +
"of the export is available.</p>\n" +
"</li></ul></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><strong class=\"MEDEMPH\">Scheduling on a Weekly Basis</strong><p>\n" +
"For heavily loaded ENIQ systems, it may be preferred that Historical\n" +
"CM is based on a weekly export rather than a daily export. The timing\n" +
"values for <tt class=\"file-path\">/opt/ericsson/eniqm/bin/extract_eniqcm.sh</tt> in the OSS-RC <tt class=\"file-path\">crontab</tt> can be changed\n" +
"to a weekly basis rather than a daily basis. </p>\n" +
"<p>\n" +
"For information on running reports on a weekly basis refer to the <em class=\"LOWEMPH\">ENIQ, Ericsson Network IQ Report Developer Guide,</em>, <a href='javascript:parent.parent.parent.showAnchor(\"ReportDevelopersGuideRef\")' class=\"xref\">Reference [16]</a>.</p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><strong class=\"MEDEMPH\">Exporting a Subset of Managed Objects with BCG</strong><p>\n" +
"The BCG export defined in the <tt class=\"file-path\">extract_eniqcm.sh</tt>script is of type <tt class=\"file-path\">both</tt>. This means that\n" +
"all Radio and Transport Managed Objects supported by BCG are exported. </p>\n" +
"<p>\n" +
"To reduce the workload on BCG and ENIQ it is possible to export\n" +
"only a subset of the Managed Objects (either the Radio or the Transport\n" +
"Managed Objects). This requires a modification to the <tt class=\"file-path\">extract_eniqcm.sh</tt> script.</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Before editing  the <tt class=\"file-path\">extract_eniqcm.sh</tt>script create a backup version of the script in directory <tt class=\"file-path\">/opt/ericsson/eniqm/bin</tt> on OSS-RC.  </li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">To export both the Radio and the Transport Managed Objects\n" +
"specify <tt class=\"file-path\">both</tt> after <tt class=\"output\">/opt/ericsson/nms_umts_wran_bcg/bin/start_rah_export.sh</tt>in section <tt class=\"file-path\">Export</tt>. <p>\n" +
"To export only Radio Managed Objects specify <tt class=\"file-path\">r</tt> instead of <tt class=\"file-path\">both</tt>. </p>\n" +
"<p>\n" +
"To export only the Transport Managed Objects specify <tt class=\"file-path\">t</tt> instead of <tt class=\"file-path\">both</tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">It is also possible to specify BCG custom or user-defined\n" +
"filters. <p>\n" +
"Predefined BCG custom filter files are available in <tt class=\"output\">/opt/ericsson/nms_umts_bcg_meta/dat/customfilters</tt>. For example, <tt class=\"file-path\">BCR_UtranCell.xml</tt> creates\n" +
"a BCG export of UtranCell Managed Objects. </p>\n" +
"<p>\n" +
"To use a custom filter, in the <tt class=\"file-path\">extract_eniqcm.sh</tt> script comment out the line containing <tt class=\"file-path\">/opt/ericsson/nms_umts_wran_bcg/bin/start_rah_export.sh</tt> in the section <tt class=\"file-path\">\"Export\"</tt>. Replace the\n" +
"line with<br /> <tt class=\"input\"><b>`/opt/ericsson/nms_umts_wran_bcg/bin/bcgtool.sh\n" +
"-e $FILESTAMP -d BCR_UtranCell.xml -n SubNetwork=ONRM_ROOT_MO_R&gt;/dev/null\n" +
"2&gt;&amp;1`</b></tt> </p>\n" +
"<p>\n" +
"For information on creating and specifying user-defined filters\n" +
"refer to the <em class=\"LOWEMPH\">BCG User Guide</em>, <a href='javascript:parent.parent.parent.showAnchor(\"BCGUserGuide\")' class=\"xref\">Reference [15]</a>.</p>\n" +
"</li></ul></li></ul>\n" +
"\n"+
"</div>\n";

var C25_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">25.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0wg0\"></a><a name=\"CHAPTER25.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENM</a></span></h2>\n" +
"\n" +
"<p>\n" +
"See the ENM section in the chapter <strong class=\"MEDEMPH\">Configuration of Historical\n" +
"CM for ENIQ Statistics</strong> in <a href='javascript:parent.parent.parent.showAnchor(\"OSSRCConfigForEniq\")' class=\"xref\">Reference [23]</a> for details of how to configure the export on ENM.</p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

