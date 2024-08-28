var C22=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">22 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_habh\"></a><a name=\"CHAPTER22\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Licensing</a></span></h1>\n" +
"\n" +
"<p>\n" +
"A basic starter license is required with the desired capacity (based\n" +
"on core) and at least one feature TP license is needed  to run ENIQ.\n" +
"The various features and tech packs require licenses to be installed\n" +
"before they can be used. The validity of the licenses is checked every\n" +
"time ENIQ engine is started.</p>\n" +
"\n" +
"<p>\n" +
"ENIQ has a separate license server which, by default, exists on\n" +
"the same server as ENIQ. The license server can be changed to any\n" +
"license server that exists on the same network as ENIQ. ENIQ does\n" +
"not check the validity of licenses directly from the license server,\n" +
"but from a separate process called license manager. The license manager\n" +
"of ENIQ always exists on the same server as ENIQ. The commands to\n" +
"control license server and license manager are described in the following\n" +
"chapters. For an overview of ENIQ licensing, see <a href='javascript:parent.parent.parent.showAnchor(\"licensingoverviewfigure\")' class=\"xref\"> Figure 63</a>.</p>\n" +
"\n" +
"<div style=\"margin-top: 12pt\"><a name=\"licensingoverviewfigure\"></a><a name=\"fig-licoverview\"></a><p align=\"left\" class=\"image\"><a name=\"FIGURE63\"><img src=\"5_1543-CSA11363_1Uen.M-Overview_of_ENIQ_Licensing.png.png\" title=\"\" border=\"0\" width=\"600\" height=\"58\" class=\"tab2\" /></a></p><p align=\"left\"><i>Figure 63 &nbsp; Overview of ENIQ Licensing</i></p></div>\n" +
"\n"+
"</div>\n";

var C22_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0djv\"></a><a name=\"CHAPTER22.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing License Server</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To change license server</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">root</tt>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Open the <tt class=\"file-path\">sentinel.env</tt> configuration\n" +
"file:<br /> <tt class=\"input\"><b># vi /eniq/sentinel/etc/sentinel.env</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Change the value of the <tt class=\"file-path\">LSHOST</tt> variable to the license server or servers you want to use. For example:<br /><tt class=\"file-path\">LSHOST=&lt;<i class=\"var\">LICENSE_SERVER_IP_ADDRESS&gt;</i></tt><br /> Where <tt class=\"file-path\"><i class=\"var\">&lt;LICENSE_SERVER_IP_ADDRESS&gt;</i></tt> is the IP address of the license server to be used.<br /> If you are using more than one license server, separate the server\n" +
"addresses with colons. For example:<br /><tt class=\"file-path\"><i class=\"var\">&lt;LSHOST=LICENSE_SERVER_IP_ADDRESS_ONE&gt;</i>:<i class=\"var\">&lt;SECOND_LICENSE_SERVER_IP_ADDRESS_TWO&gt;</i></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C22_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_2iot\"></a><a name=\"CHAPTER22.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Checking Installed Licenses</a></span></h2>\n" +
"\n" +
"<p>\n" +
"You can check the ENIQ licenses using either AdminUI or command\n" +
"line.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To check installed licenses using AdminUI</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">In AdminUI, click <b>System Status</b>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">If License server service is running, click <b>Show\n" +
"installed licenses</b>.<p>\n" +
"Installed licenses are listed in a table. Click the corresponding\n" +
"links to view the tech packs and interfaces associated with a license.\n" +
"See <a href='javascript:parent.parent.parent.showAnchor(\"InstalledLicenseList\")' class=\"xref\"> Figure 64</a>, for an example of a license\n" +
"list.</p>\n" +
"</li></ol>\n" +
"<div style=\"margin-top: 12pt\"><a name=\"InstalledLicenseList\"></a><a name=\"fig-licenses\"></a><p align=\"left\" class=\"image\"><a name=\"FIGURE64\"><img src=\"5_1543-CSA11363_1Uen.M-Listing_Installed_Licenses.png.png\" title=\"\" border=\"0\" width=\"578\" height=\"582\" class=\"tab0\" /></a></p><p align=\"left\"><i>Figure 64 &nbsp; Listing Installed Licenses</i></p></div>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To check installed licenses from command line</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/licmgr -getlicinfo</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C22_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_199v\"></a><a name=\"CHAPTER22.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Installing Licenses from File</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To install licenses from a file</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Copy your licenses file to ENIQ.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Use the following command to install licenses from the\n" +
"file you copied:<br /> <tt class=\"file-path\"># cd /eniq/sw/bin <br /># ./licmgr\n" +
"-install <i class=\"var\">&lt;PATH_TO_LICENSES_FILE&gt;</i></tt><br /> Where <tt class=\"file-path\"><i class=\"var\">&lt;PATH_TO_LICENSES_FILE&gt;</i></tt> is the directory\n" +
"path and filename of the file containing the licenses.</li></ol>\n" +
"<p>\n" +
"After installation of the licenses, the features associated with\n" +
"the installed licenses can be used immediately.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C22_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_3ygs\"></a><a name=\"CHAPTER22.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Checking License Manager and License\n" +
"Server Status</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Statuses of license manager and license server can be checked from\n" +
"AdminUI, or by using command-line commands.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To check statuses using AdminUI</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">In AdminUI, click <b>System Status</b>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">At the bottom of the screen, two traffic lights are shown,\n" +
"one for license server and one for license manager. If the color is\n" +
"green, the corresponding service is operational. If the color is red,\n" +
"the service is not running.</li></ol>\n" +
"<div style=\"margin-top: 12pt\"><p align=\"left\" class=\"image\"><a name=\"FIGURE65\"><img src=\"5_1543-CSA11363_1Uen.M-Viewing_License_Info.png.png\" title=\"\" border=\"0\" width=\"434\" height=\"169\" class=\"tab0\" /></a></p><p align=\"left\"><i>Figure 65 &nbsp; Viewing License Info</i></p></div>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To check the status of the license server from command line</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Use the following commands to check the status of the\n" +
"license server:<br /> <tt class=\"LITERALMONO\"># cd /eniq/sw/bin </tt><br />\n" +
"<tt class=\"LITERALMONO\"># ./licmgr -serverstatus</tt></li></ol>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To check the status of the license manager from command line</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Use the following commands to check the status of the\n" +
"license manager:<br /> <tt class=\"LITERALMONO\"># cd /eniq/sw/bin </tt><br />\n" +
"<tt class=\"LITERALMONO\"># ./licmgr -status</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C22_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_1jpv\"></a><a name=\"CHAPTER22.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Viewing Licensing Logs</a></span></h2>\n" +
"\n" +
"<p>\n" +
"You can view licensing log files using AdminUI, or command-line\n" +
"commands.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To view log files using AdminUI</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">In AdminUI, click <b>System Status</b>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">On the <b>Systems Status</b> page, click <b>Show License Logs</b>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Select the date of the logs you want to view and click\n" +
"the <b>Search</b> button.</li></ol>\n" +
"<div style=\"margin-top: 12pt\"><p align=\"left\" class=\"image\"><a name=\"FIGURE66\"><img src=\"5_1543-CSA11363_1Uen.M-Viewing_License_Logs.png.png\" title=\"\" border=\"0\" width=\"444\" height=\"188\" class=\"tab0\" /></a></p><p align=\"left\"><i>Figure 66 &nbsp; Viewing License Logs</i></p></div>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To view log files using command line</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Enter the following to print the contents of the logs\n" +
"for a given date:<br /><tt class=\"file-path\"># cd /eniq/log/sw_log/licensemanager/<br /># cat licensemanager-<i class=\"var\">&lt;year&gt;</i>_<i class=\"var\">&lt;month&gt;</i>_<i class=\"var\">&lt;day&gt;</i>.log</tt><br />Where <tt class=\"file-path\"><i class=\"var\">&lt;year&gt;</i></tt>, <tt class=\"file-path\"><i class=\"var\">&lt;month&gt;</i></tt>,\n" +
"and <tt class=\"file-path\"><i class=\"var\">&lt;day&gt;</i></tt> are the required\n" +
"date values.</li></ol>\n" +
"\n"+
"</div>\n";

var C22_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_o2oc\"></a><a name=\"CHAPTER22.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Updating License Manager</a></span></h2>\n" +
"\n" +
"<p>\n" +
"It is possible to update the contents of the license manager (for\n" +
"example, if the licenses in the license manager do not match the licenses\n" +
"installed on the server). This may happen if licenses have been installed\n" +
"without using the <tt class=\"LITERALMONO\">licmgr</tt> script. If the license\n" +
"manager is out of synch, it can be refreshed by following the steps\n" +
"below.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To update the license manager </strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute the following to update the license manager:<br /> <tt class=\"input\"><b># /eniq/sw/bin/licmgr &ndash;update</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C22_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_7ym6\"></a><a name=\"CHAPTER22.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Viewing Feature Mapping Information\n" +
"using licmgr</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Feature names (CXC numbers) can be mapped to descriptions, feature\n" +
"identifiers, and interfaces. To view the mapping for a given feature\n" +
"name, use the command-line tool <tt class=\"LITERALMONO\">licmgr</tt> .</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To view feature name mappings </strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute the following to retrieve the mapping for a feature\n" +
"name:<br /> <tt class=\"file-path\"># /eniq/sw/bin/licmgr &ndash;map <i class=\"var\">&lt;type&gt;\n" +
"&lt;feature name&gt;</i></tt><br /> Where <tt class=\"file-path\"><i class=\"var\">&lt;feature name&gt;</i></tt> is the CXC number and <tt class=\"file-path\"><i class=\"var\">&lt;type&gt;</i></tt> is one of the following three\n" +
"values:<tt class=\"LITERALMONO\">&nbsp;interface</tt>, <tt class=\"LITERALMONO\">faj</tt> or <tt class=\"LITERALMONO\">description</tt>.</li></ol>\n" +
"\n"+
"</div>\n";

var C22_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">22.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ise9\"></a><a name=\"CHAPTER22.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Overview of licmgr Commands</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The command-line tool <tt class=\"LITERALMONO\">licmgr</tt> can control a large\n" +
"portion of the license manager. Below is a brief overview of the capabilities\n" +
"of this script. The script is executed in the following manner:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as <tt class=\"file-path\">dcuser</tt> using SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute the following:<br /> <tt class=\"file-path\"># /eniq/sw/bin/licmgr <i class=\"var\">&lt;operation&gt;</i></tt></li></ol>\n" +
"<p>\n" +
"Where <tt class=\"file-path\"><i class=\"var\">&lt;operation&gt;</i></tt> is one\n" +
"of the operations listed in <a name=\"tbl-operations\" href='javascript:parent.parent.parent.showAnchor(\"tbl-operations\")' class=\"xref\"> Table 19</a>.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE19\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 19 &nbsp;&nbsp; License Manager Operations</caption>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-decode <i class=\"var\">&lt;file&gt;</i></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Decodes the given license file. This prints a detailed\n" +
"list of properties for each entry of the given license file.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-getlicinfo</tt> </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Print basic information for currently installed licenses.\n" +
"All currently installed licenses are printed, along with expiration\n" +
"dates and capacities if applicable.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-getlockcode</tt> </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Prints the machine&rsquo;s lock code.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-install <i class=\"var\">&lt;file&gt;</i></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Install new licenses from the specified file.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-isvalid <i class=\"var\">&lt;feature&gt;</i></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Check if the given feature name has a valid license. The\n" +
"script returns 0 if the license is valid and 1 otherwise.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-listserv</tt> </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>List all servers that were found on the local subnet. This\n" +
"may include servers that are not used by ENIQ. See the section Changing\n" +
"license server for more information on how to select the active license\n" +
"server.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">&ndash;map <i class=\"var\">&lt;type&gt; &lt;feature name&gt;</i></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Prints the mappings of the given feature name onto the\n" +
"type that was given. Type should be one of: <tt class=\"input\"><b>faj</b></tt>, <tt class=\"input\"><b>description</b></tt>, <tt class=\"input\"><b>interface</b></tt>. Multiple features\n" +
"can be checked with a space separated list when the mapping type is\n" +
"set to <tt class=\"input\"><b>interface</b></tt>.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-restart</tt> </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Restarts the license manager.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-serverstatus</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Prints the status of the servers currently in use.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-start</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Starts the license manager.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-status</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Prints the status of the license manager. </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-stop</tt> </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Stops the license manager</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-uninstall <i class=\"var\">&lt;feature&gt;</i></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Temporarily removes the given feature from the license\n" +
"server. The license reappears if and when the license server is restarted.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p><tt class=\"file-path\">-update</tt> </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"60%\">\n" +
"<p>Updates all licenses from the server(s) and saves them\n" +
"in the license manager. Updates are done automatically when installing\n" +
"new licenses but might be needed to be explicitly run if licenses\n" +
"have been installed or uninstalled using other than the recommended\n" +
"methods.</p>\n" +
"</td></tr></table>\n" +
"\n" +
"\n"+
"</div>\n";

