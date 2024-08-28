var C22=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><a name=\"ConnectdMonitor\"></a><span class=\"CHAPNUMBER\">22 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_hmev\"></a><a name=\"CHAPTER22\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Connectd Monitor\n" +
"Daemon</a></span></h1>\n" +
"\n" +
"<p>\n" +
"<tt class=\"file-path\">Connectd</tt>, is an SMF service which monitors\n" +
"the NFS Mounts between ENIQ and each OSS/SFS. <tt class=\"file-path\">Connectd</tt> runs in the background and constantly checks for any OSS that has\n" +
"been added to the  ENIQ server, or, in the case of a multi blade deployment\n" +
"on the engine blade.</p>\n" +
"\n" +
"<p>\n" +
"Each OSS setup on the system has a sub-directory in <tt class=\"file-path\">/eniq/connectd/mount_info/</tt>. This sub-directory contains\n" +
"configuration data for that OSS. <tt class=\"file-path\">Connectd</tt> cycles through each OSS, and enables or disables a  NFS monitoring\n" +
"script for each one.</p>\n" +
"\n" +
"<p>\n" +
"The monitoring script sets up and monitors the NFS Mounts between\n" +
"ENIQ and  each OSS/SFS. The script sets up and uses an SSH tunnel\n" +
"(if indicated in the configuration data for that OSS).</p>\n" +
"\n" +
"<p>\n" +
" If the connection to the OSS is lost, for example, because of\n" +
"network problems or OSS reboot, <tt class=\"file-path\">connectd</tt> unmounts the file systems and shuts down the SSH tunnel (if one\n" +
"was initially setup). </p>\n" +
"\n" +
"<p>\n" +
"When connectivity is restored, <tt class=\"file-path\">connectd</tt> mounts the file systems, and sets up SSH tunnel (if required). If\n" +
"ENIQ cannot see the OSS file systems or mount points, check the log\n" +
"files in <tt class=\"file-path\">/eniq/local_logs/connectd</tt> to verify\n" +
"the file systems are correctly mounted.</p>\n" +
"\n" +
"\n"+
"</div>\n";

