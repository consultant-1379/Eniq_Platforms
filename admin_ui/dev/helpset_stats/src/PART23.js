var C23=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">23 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_bioq\"></a><a name=\"CHAPTER23\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">External SQL Interface</a></span></h1>\n" +
"\n" +
"<p>\n" +
"Ericsson Network IQ provides an external SQL interface for third-party\n" +
"access to database measurement tables. The external SQL interface\n" +
"is offered as a reporting interface and it contains all available\n" +
"measurement tables of each measurement type.</p>\n" +
"\n" +
"<p>\n" +
"The external SQL interface is installed automatically during ENIQ\n" +
"installation. To use it, you need:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">A database connection tool</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">A configured connection to database</li></ul>\n" +
"<p>\n" +
"Ericsson Network IQ external SQL interface user is always <tt class=\"file-path\">dcpublic</tt>, see <a href='javascript:parent.parent.parent.showAnchor(\"tbl-DatabaseUsers\")' class=\"xref\"> Table 20</a>.\n" +
"No other users are supported by Ericsson.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Do not execute stored procedures or large queries that may\n" +
"affect performance. Executing stored procedures or running large queries\n" +
"may cause problems with ENIQ and may have an adverse effect on ENIQ\n" +
"performance.</dd></dl><br />\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In a multi-blade deployment access the <tt class=\"file-path\">dwhdb</tt> using <tt class=\"file-path\">dwh_reader_2</tt> blade. For <tt class=\"file-path\">repdb</tt>, access through the <tt class=\"file-path\">coordinator</tt> blade.</dd></dl><br />\n" +
"\n"+
"</div>\n";

