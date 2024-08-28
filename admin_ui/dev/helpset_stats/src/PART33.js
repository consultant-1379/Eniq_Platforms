var C33=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">33 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rf2k\"></a><a name=\"CHAPTER33\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Migrating from DU to Baseband Radio Nodes</a></span></h1>\n" +
"\n" +
"<p>\n" +
"This is only applicable for customers who are migrating their (LTE\n" +
"or WCDMA) DU Radio Nodes to Baseband Radio nodes and who want to keep\n" +
"the same node names. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In OSS-RC, if a customer deletes an existing (LTE or WCDMA)\n" +
"DU Radio Node and adds a new Baseband Radio Node with the same name\n" +
"this results in duplicate topology data coming to ENIQ and results\n" +
"in duplicate rows in the KPI reports.<p>\n" +
"Before adding the new (LTE or WCDMA) Baseband Radio Node in the\n" +
"OSS-RC run the <tt class=\"file-path\">delete_duplicate_preventive.bsh</tt> script on the ENIQ server to delete the existing topology entries.</p>\n" +
"</dd></dl><br />\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to the ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH. <p>\n" +
"(In a multi-blade deployment, run all the steps on the <tt class=\"file-path\">coordinator</tt> blade.) </p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Go to the following directory:<p>\n" +
"<tt class=\"file-path\"># cd /eniq/sw/installer</tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the following script:<p>\n" +
"<tt class=\"input\"><b>#./delete_duplicate_preventive.bsh  <i class=\"var\">&lt;Techpackname&gt;</i> <i class=\"var\">&lt;&lt;NODE&gt;_node.txt&gt;</i></b></tt><br />where <tt class=\"file-path\"><i class=\"var\">&lt;Techpackname&gt;</i></tt> is either WCDMA or LTE. <br /><tt class=\"file-path\">&lt;<i class=\"var\">&lt;NODE&gt;</i>&gt;_node.txt</tt> is\n" +
"its corresponding <tt class=\"file-path\">WCDMA_node.txt</tt> or <tt class=\"file-path\">LTE_node.txt</tt> file which has lists of Node Names.</p>\n" +
"</li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The <tt class=\"file-path\">delete_duplicate_preventive.bsh</tt> script is applicable for both LTE and WCDMA features.</dd></dl><br />\n" +
"\n"+
"</div>\n";

