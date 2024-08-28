var C20=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">20 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_a0b4\"></a><a name=\"CHAPTER20\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Customization of Historical CM for ENIQ\n" +
"Statistics</a></span></h1>\n" +
"\n" +
"<p>\n" +
"The optional Historical CM Tech Pack feature in ENIQ Statistics\n" +
"requires that BCG exports are initiated on OSS-RC/ENM and the resulting\n" +
"export files are transferred to ENIQ.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C20_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">20.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rqwc\"></a><a name=\"CHAPTER20.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">OSS-RC</a></span></h2>\n" +
"\n" +
"<p>\n" +
" ENIQ-M in OSS-RC provides a script /opt/ericsson/eniqm/bin/extract_eniqcm.sh\n" +
" which will initiate an export in BCG and transfer the export file\n" +
"to ENIQ. By default this script is to be scheduled in crontab in OSS-RC\n" +
"to run on a daily basis. There are a number of customisations that\n" +
"may be required to this system however, which are described below.\n" +
"They all require modifications on OSS-RC. </p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">Re-use of an existing scheduled BCG export</strong></p>\n" +
"\n" +
"<p>\n" +
"If there is already an existing full BCG export scheduled in OSS-RC\n" +
"then it is preferred that the output of that export is used instead\n" +
"of initiating a new BCG export. This requires a modification to the\n" +
"extract_eniqcm.sh  script however. Before editing create a backup\n" +
"version of the script in directory <tt class=\"output\">/opt/ericsson/eniqm/bin</tt> on OSS-RC. Change the FILEPATH and FILESTAMP parameters to match\n" +
"the output of the existing scheduled export. The \"Export\" section\n" +
"of the script must be commented out.</p>\n" +
"\n" +
"<p>\n" +
"It is required the output of the existing scheduled export is not\n" +
"deleted by another process. It is also required that the scheduling\n" +
"of the extract_eniqcm.sh script in crontab is such that the output\n" +
"of the export shall be available.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">Scheduling on a weekly basis</strong></p>\n" +
"\n" +
"<p><a name=\"ReportDevelopersGuide\"></a>\n" +
"For heavily loaded ENIQ systems\n" +
"it may be preferred that Historical CM is based on a weekly export\n" +
"rather than a daily export. The timing values for /opt/ericsson/eniqm/bin/extract_eniqcm.sh\n" +
" in the OSS-RC crontab should be changed to a weekly basis rather\n" +
"than a daily basis. Refer to the ENIQ Report Developer Guide (see <a href='javascript:parent.parent.parent.showAnchor(\"ReportDevelopersGuideRef\")' class=\"xref\">Reference [16]</a>) on how reports can be run a\n" +
"weekly basis.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">Export of a subset of Managed Objects with BCG</strong></p>\n" +
"\n" +
"<p>\n" +
"The BCG export defined in script 'extract_eniqcm.sh' is of type\n" +
"'both'. This means that all Radio and Transport Managed Objects supported\n" +
"by BCG will be exported. To ease workload on BCG and ENIQ it may be\n" +
"seen that a subset of Managed Objects should be exported instead.\n" +
"This requires a modification to the extract_eniqcm.sh  script however.\n" +
"Before editing create a backup version of the script in directory\n" +
"/opt/ericsson/eniqm/bin on OSS-RC. It is possible to choose to export\n" +
"only Radio or Transport Managed Objects by specifying 'r' or 't' instead\n" +
"of 'both' after <tt class=\"output\">/opt/ericsson/nms_umts_wran_bcg/bin/start_rah_export.sh</tt>in section \"Export\".  </p>\n" +
"\n" +
"<p>\n" +
"Furthermore it is possible to specify a BCG custom or user-defined\n" +
"filter. There are predefined custom filter files in <tt class=\"output\">/opt/ericsson/nms_umts_bcg_meta/dat/customfilters</tt> such as BCR_UtranCell.xml, which can be used to create a BCG export\n" +
"of UtranCell Managed Objects. To use such a custom filter the line\n" +
"containing /opt/ericsson/nms_umts_wran_bcg/bin/start_rah_export.sh\n" +
"in section \"Export\" of extract_eniqcm.sh should be commented out.\n" +
"It would be replaced with <tt class=\"output\">`/opt/ericsson/nms_umts_wran_bcg/bin/bcgtool.sh\n" +
"-e $FILESTAMP -d BCR_UtranCell.xml -n SubNetwork=ONRM_ROOT_MO_R&gt;/dev/null\n" +
"2&gt;&amp;1`</tt> It is also possible to create user defined filters.\n" +
"Please refer to the BCG User Guide (see <a href='javascript:parent.parent.parent.showAnchor(\"BCGUserGuide\")' class=\"xref\">Reference [15]</a>) on how to create and specify user defined filters.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C20_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">20.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0wg0\"></a><a name=\"CHAPTER20.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENM</a></span></h2>\n" +
"\n" +
"<p>\n" +
"See the ENM section in the chapter <strong class=\"MEDEMPH\">Configuration of Historical\n" +
"CM for ENIQ Statistics</strong> in <a href='javascript:parent.parent.parent.showAnchor(\"OSSRCConfigForEniq\")' class=\"xref\">Reference [22]</a> for details of how to configure the export on ENM.</p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

