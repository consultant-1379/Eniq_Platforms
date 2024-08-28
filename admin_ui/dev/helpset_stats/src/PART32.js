var C32=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">32 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"correctiveMain\"></a><a name=\"CHAPTER32\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Corrective Maintenance</a></span></h1>\n" +
"\n" +
"<p>\n" +
"Actions to take when a system error message is displayed.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C32_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">32.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_z2ho\"></a><a name=\"CHAPTER32.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Error Handling</a></span></h2>\n" +
"\n"+
"</div>\n";

var C32_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">32.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_x4a3\"></a><a name=\"CHAPTER32.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Engine does not start: \"ERROR : Fault starting svc:/eniq/engine\"</a></span></h3>\n" +
"\n" +
"<p class=\"titled-block\">Cause</p><p>\n" +
"Engine is in maintenance state, because:</p>\n" +
"<p>\n" +
"Engine script fails to work.</p>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">or</strong></p>\n" +
"<p>\n" +
"Database is full.</p>\n" +
"\n" +
"<p class=\"titled-block\">Action\n" +
"</p><ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Log in ENIQ server with SSH.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">To check maintenance state, enter the following command: <tt class=\"LITERALMONO\">svcs -a | grep eniq</tt></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">To check what has happened during the start of the Engine,\n" +
"see the latest <tt class=\"file-path\">start_engine</tt> logs from <tt class=\"file-path\">/eniq/log/sw_log/engine</tt>.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">When in maintenance state, check if the database is\n" +
"full. View the dwhdb-usage log file, <tt class=\"file-path\">/eniq/log/sw_log/iq/dwhdb_usage.log</tt>, to see if one of the most recent messages is: &quot;SEVERE: DWHDB\n" +
"is full!&quot;. <br />If it is, contact your local Ericsson Customer\n" +
"Support.<br /> If not, the problem is with the engine script. Contact\n" +
"your local Ericsson Customer Support.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">To reset Engine state, run command:  <tt class=\"LITERALMONO\">/eniq/smf/bin/eniq_service_start_stop.bsh -s engine -a clear</tt> </li></ul><p>\n" +
"The filling ratio log file is created the first time the ratio\n" +
"exceeds 85%. If there is no <tt class=\"file-path\">dwhdb_usage.log</tt>, the ratio has never exceeded 85%. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C32_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">32.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_7vk8\"></a><a name=\"CHAPTER32.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Parsing errors \"org.xml.sax.SAXParseException:\n" +
"XML document structures must start and end within the same entity.\"</a></span></h3>\n" +
"\n" +
"<p class=\"titled-block\">Cause</p><p>\n" +
"PM data is corrupted or invalid.</p>\n" +
"\n" +
"<p class=\"titled-block\">Action</p><p>\n" +
"Ensure that the data is valid.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C32_1_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">32.1.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_29to\"></a><a name=\"CHAPTER32.1.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Engine error log: \"Can't find table\n" +
"for DC_E_XXX_YYYY:RAW @ YYYY-MM-DD\", but dateString is between current\n" +
"date (YYYY-MM-DD) and last partition endtime (YYYY-MM-DD)</a></span></h3>\n" +
"\n" +
"<p class=\"titled-block\">Cause</p><p>\n" +
"There is no partition for the date selected.</p>\n" +
"\n" +
"<p class=\"titled-block\">Action</p><p>\n" +
"Wait until the Data Warehouse database is updated and a new partition\n" +
"has been created.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C32_1_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">32.1.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_xqih\"></a><a name=\"CHAPTER32.1.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Topology Loaders getting ERRORS\n" +
"because of: \"SybSQLException: SQL Anywhere Error -1009134: Insufficient\n" +
"buffers for 'Sort' \"</a></span></h3>\n" +
"\n" +
"<p class=\"titled-block\">Cause</p><p>\n" +
"It is a SYBASE database error on insufficient buffer, because of\n" +
"high usage of resources like temp_cache, loader sets stops abruptly\n" +
"during the execution.</p>\n" +
"\n" +
"<p class=\"titled-block\">Action</p><p>\n" +
"Reload the topology loader file using below steps:</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Check if recent file is available in &ldquo;failed&rdquo;\n" +
"directory<br /><tt class=\"LITERALMONO\">/eniq/data/&lt;ETLDATA&gt;/&lt;DIM_MT&gt;/failed/ </tt><br />Where,<br /><tt class=\"LITERALMONO\">&lt;ETLDATA&gt;</tt> is <tt class=\"LITERALMONO\">etldata</tt> in case of ZFS and <tt class=\"LITERALMONO\">/etldata_/&lt;SUB_DIR&gt;/</tt> where <tt class=\"LITERALMONO\">&lt;SUB_DIR&gt;</tt> can be <tt class=\"LITERALMONO\">00</tt> or <tt class=\"LITERALMONO\">01</tt> or <tt class=\"LITERALMONO\">02</tt> or <tt class=\"LITERALMONO\">03</tt>, in case of raw. <br /><tt class=\"LITERALMONO\">&lt;DIM_MT&gt;</tt> is topology measurement type for\n" +
"which loader got failed.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">	If recent failed file available, move the file to its\n" +
"corresponding &ldquo;raw&rdquo; directory. Use below command, <br /><br /><tt class=\"input\"><b># mv /eniq/data/&lt;ETLDATA&gt;/&lt;DIM_MT&gt;/failed/'&lt;FAILED_FILE&gt;'\n" +
"/eniq/data/&lt;ETLDATA&gt;/&lt;DIM_MT&gt;/raw/</b></tt><br /><br />Where,<br /><tt class=\"LITERALMONO\">&lt;ETLDATA&gt;</tt> is <tt class=\"LITERALMONO\">etldata</tt> in\n" +
"case of ZFS and <tt class=\"LITERALMONO\">/etldata_/&lt;SUB_DIR&gt;/</tt> where <tt class=\"LITERALMONO\">&lt;SUB_DIR&gt;</tt> can be <tt class=\"LITERALMONO\">00</tt> or <tt class=\"LITERALMONO\">01</tt> or <tt class=\"LITERALMONO\">02</tt> or <tt class=\"LITERALMONO\">03</tt>, in case of raw.<br />`<tt class=\"LITERALMONO\">&lt;DIM_MT&gt;</tt> is topology measurement type for\n" +
"which loader got failed.<br /><tt class=\"LITERALMONO\">&lt;FAILED_FILE&gt;</tt> is\n" +
"the recent failed loader file.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">After moving the failed file successfully, trigger the\n" +
"corresponding topology loader set. To load the topology data, follow\n" +
"the steps below: <br /><br /> &bull;	Log in to AdminUI.<br /> &bull;	Click\n" +
"on &ldquo;ETLC Set Scheduling&rdquo;.<br /> &bull;	Select &ldquo;Techpack&rdquo;\n" +
"for <strong class=\"MEDEMPH\">Set Type</strong>.<br /> &bull;	For <strong class=\"MEDEMPH\">Package</strong>, select corresponding topology techpack\n" +
"for which topology loader needs to be triggered.<br /> &bull;	Trigger\n" +
"the corresponding topology loader set from the above result. </li></ul>\n" +
"\n"+
"</div>\n";

var C32_1_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">32.1.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ld57\"></a><a name=\"CHAPTER32.1.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Loaders failing with the ERROR\n" +
"\"SybSQLException: SQL Anywhere Error -1009134: Insufficient buffers\n" +
"for 'Sort' \"</a></span></h3>\n" +
"\n" +
"<p class=\"titled-block\">Cause</p><p>\n" +
"It&rsquo;s a SYBASE database error on insufficient buffer, due\n" +
"to high usage of resources like temp_cache, loader sets stops abruptly\n" +
"during the execution.</p>\n" +
"\n" +
"<p class=\"titled-block\">Action</p><p>\n" +
"The parsed files which failed to load will be retained as it is\n" +
"and will be picked by the loader in the next cycle.</p>\n" +
"\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

