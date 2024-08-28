var C28=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">28 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_etah\"></a><a name=\"CHAPTER28\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Adding New Features</a></span></h1>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>This is an optional procedure.</dd></dl><br />\n" +
"<p>\n" +
"If Customer wants to add new features to the installed/upgraded\n" +
"server, run the following commands on the server types below:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics Standalone Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Download the feature software which user wants to install\n" +
"and copy it to <tt class=\"LITERALMONO\">&lt;path_to_feature_sw&gt;</tt> on MWS. <p>\n" +
"For information on how to set up the <tt class=\"file-path\">feature_sw</tt> path refer MWS SAG document <a href='javascript:parent.parent.parent.showAnchor(\"mws\")' class=\"xref\">Reference [27]</a></p>\n" +
"</dd></dl><br />\n" +
"<p>\n" +
"<tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./manage_eniq_oss.bsh -a update [-h <i class=\"var\">&lt;eniq_alias&gt;</i>] -d <i class=\"var\">&lt;path_to_feature_sw&gt;</i> [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><tt class=\"file-path\">eniq_alias</tt> refers to the given\n" +
"name of the OSS server by ENIQ Statistics. This is in the form <tt class=\"file-path\">eniq_oss_&lt;n&gt;</tt><p>\n" +
"This script must be run for each managed OSS if you wish to add\n" +
"additional interfaces for all OSS servers managed</p>\n" +
"<p>\n" +
"<tt class=\"file-path\">&lt;path_to_feature_sw&gt;</tt> refers to the\n" +
"location on the MWS where features are downloaded.</p>\n" +
"</dd></dl><br />\n" +
"\n"+
"</div>\n";

