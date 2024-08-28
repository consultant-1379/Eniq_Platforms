var C19=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">19 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rot7\"></a><a name=\"CHAPTER19\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Counter Volume</a></span></h1>\n" +
"\n" +
"<p>\n" +
"The DWH_Monitor report provides information on the number of counters\n" +
"loaded for each measurement type for a given ROP; it can be found\n" +
"in a top level folder called <tt class=\"file-path\">ENIQ System</tt> under <tt class=\"file-path\">Public Folders</tt> in the CMS.  The <tt class=\"file-path\">DWH Monitor</tt> universe must be exported to a Business\n" +
"Objects server before the report can be run. This can be found in\n" +
"the <tt class=\"file-path\">BO_DWH_MONITOR</tt> tech pack.</p>\n" +
"\n" +
"<p>\n" +
"The report allows the user to select a single ROP time and shows\n" +
"all measurements types that were loaded for that ROP and the number\n" +
"of counters and rows loaded for each of those measurement types. The\n" +
"report queries the <tt class=\"LITERALMONO\">LOG_SESSSION_ADAPTER</tt>  table\n" +
"in the <tt class=\"file-path\">dwhdb</tt> database using the query shown\n" +
"in the following example:</p>\n" +
"\n" +
"<div class=\"example\" style=\"margin-top: 12pt\"><p><i>Example 1 &nbsp; Sample query</i></p><pre class=\"prencd\">SELECT\n" +
"  DC.LOG_SESSION_ADAPTER.NUM_OF_COUNTERS,\n" +
"  DC.LOG_SESSION_ADAPTER.NUM_OF_ROWS,\n" +
"  DC.LOG_SESSION_ADAPTER.TYPENAME,\n" +
"  DC.DIM_DATE.DATE_ID\n" +
"FROM\n" +
"  DC.LOG_SESSION_ADAPTER,\n" +
"  DC.DIM_DATE\n" +
"WHERE\n" +
"  ( DC.LOG_SESSION_ADAPTER.DATE_ID=DC.DIM_DATE.DATE_ID  )\n" +
"  AND  \n" +
"  ( ( DC.LOG_SESSION_ADAPTER.ROP_STARTTIME ) = &lt;ROP TIME&gt;</pre></div>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><tt class=\"LITERALMONO\">&lt;ROP TIME&gt;</tt> should be replaced with\n" +
"an appropriate datetime stamp that corresponds to the ROP time that\n" +
"is to be retrieved.</dd></dl><br />\n" +
"\n"+
"</div>\n";

