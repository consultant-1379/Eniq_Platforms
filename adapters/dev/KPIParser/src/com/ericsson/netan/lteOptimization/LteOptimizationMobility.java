package com.ericsson.netan.lteOptimization;

public class LteOptimizationMobility {

	
	public static String MobilityCellKPI = "SELECT " + 
			"EUTRANCELLRELATION.NODE_NAME as NODE_NAME, EUTRANCELLRELATION.CELL_NAME as CELL_NAME, " +  
			"EUTRANCELLRELATION.TIMELEVEL, EUTRANCELLRELATION.DC_RELEASE,  " +  
			"EUTRANCELLRELATION.DATETIME_ID AS DATETIME_ID, " +  

			"(EUTRANCELL.pmCellHoPrepSuccLteIntraF + EUTRANCELL.pmCellHoPrepSuccLteInterF  " +  
			" + ISNULL(UTRANCELLRELATION.pmHoPrepSucc, 0)  + ISNULL(GERANCELLRELATION.pmHoPrepSucc, 0)  " +  
			" + ISNULL(CDMA20001XRTTCELLRELATION.pmHoPrepSucc1xRttSrvcc, 0)  - ISNULL(EUTRANCELLRELATION.pmHoPrepSuccNonMob, 0)  " +  
			" - ISNULL(UTRANCELLRELATION.pmHoPrepSuccNonMob, 0) ) AS PrepSucc,  " +  
		  
			"(EUTRANCELL.pmCellHoPrepAttLteIntraF + EUTRANCELL.pmCellHoPrepAttLteInterF  " +  
			" + ISNULL(UTRANCELLRELATION.pmHoPrepAtt, 0)  + ISNULL(GERANCELLRELATION.pmHoPrepAtt, 0)  " +  
			" + ISNULL(CDMA20001XRTTCELLRELATION.pmHoPrepAtt1xRttSrvcc, 0)  - ISNULL(EUTRANCELLRELATION.pmHoPrepAttNonMob, 0)  " +  
			" - ISNULL(UTRANCELLRELATION.pmHoPrepAttNonMob, 0)  - EUTRANCELL.pmHoPrepRejInUlThres) AS PrepAtt,  " +  
		  
			" (EUTRANCELL.pmCellHoExeSuccLteIntraF + EUTRANCELL.pmCellHoExeSuccLteInterF  " +  
			" + ISNULL(UTRANCELLRELATION.pmHoExeSucc, 0)  + ISNULL(GERANCELLRELATION.pmHoExeSucc, 0)  " +  
			" - ISNULL(EUTRANCELLRELATION.pmHoExeSuccNonMob, 0)  - ISNULL(UTRANCELLRELATION.pmHoExeSuccNonMob, 0) ) AS ExeSucc, " +   
		  
			" (EUTRANCELL.pmCellHoExeAttLteIntraF + EUTRANCELL.pmCellHoExeAttLteInterF  " +  
			" + ISNULL(UTRANCELLRELATION.pmHoExeAtt, 0)  + ISNULL(GERANCELLRELATION.pmHoExeAtt, 0)  " +  
			" - ISNULL(EUTRANCELLRELATION.pmHoExeAttNonMob, 0)  - ISNULL(UTRANCELLRELATION.pmHoExeAttNonMob, 0) ) AS ExeAtt,  " +  
		  
			" (100 * ((PrepSucc / PrepAtt) * (ExeSucc / ExeAtt))) AS KPI_13 " +  
		    " FROM " +  
		" (SELECT " +  
			" ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, " +  
			"TIMELEVEL, DC_RELEASE, DATETIME_ID , OSS_ID, " +  
			"SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob, SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob, " +    
			" SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob,SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " +  
		 " FROM DC_E_ERBS_EUTRANCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
        "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN ( $ERBSLIST  ) " +  
		" GROUP BY NODE_NAME, CELL_NAME, TIMELEVEL, DC_RELEASE, DATETIME_ID ,  OSS_ID" +  
		" UNION " +  
		" SELECT " +  
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, " +  
		"	TIMELEVEL, DC_RELEASE, DATETIME_ID, OSS_ID," +  
		"	SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob, SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,   " +  
		"	SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob,SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " +  
		" FROM DC_E_ERBSG2_EUTRANCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
        "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN ( $ERBSLIST  ) " +  
		" GROUP BY NODE_NAME, CELL_NAME, TIMELEVEL, DC_RELEASE, DATETIME_ID , OSS_ID) EUTRANCELLRELATION " +  
		" LEFT JOIN " +  
		" (SELECT  " +  
		"	ERBS as NODE_NAME, EUTRANCELLFDD as CELL_NAME, OSS_ID," +  
		"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF, " +  
		"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +  
		"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +  
		" FROM DC_E_ERBS_EUTRANCELLFDD_RAW RAW" + 
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
        "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN (  $ERBSLIST   ) " +  
		" UNION " +  
		" SELECT " +  
		"	ERBS as NODE_NAME, EUTRANCELLTDD as CELL_NAME,  OSS_ID," +  
		"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF, " +  
		"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +  
		"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +  
		" FROM DC_E_ERBS_EUTRANCELLTDD_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
        "    AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN (  $ERBSLIST  ) " +  
		" UNION " +  
		" SELECT " +  
		"	ERBS as NODE_NAME, EUTRANCELLFDD as CELL_NAME, OSS_ID," +  
		"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF, " +  
		"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +  
		"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +  
		" FROM DC_E_ERBSG2_EUTRANCELLFDD_RAW RAW" +  
		" WHERE UTC_DATETIME_ID= '$DATETIMEID' " + 
        "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN ($ERBSLIST   ) " +  
		" UNION " +  
		" SELECT " +  
		"	ERBS as NODE_NAME, EUTRANCELLTDD as CELL_NAME, OSS_ID," +  
		"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF, " +  
		"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +  
		"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +  
		" FROM DC_E_ERBSG2_EUTRANCELLTDD_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
        "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN (  $ERBSLIST   ) ) EUTRANCELL " +  
		"	ON EUTRANCELL.NODE_NAME = EUTRANCELLRELATION.NODE_NAME  " +  
		"	AND EUTRANCELL.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " +  
		"	AND EUTRANCELL.OSS_ID = EUTRANCELLRELATION.OSS_ID " +  
		" LEFT JOIN " +  
		" (SELECT " +  
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID," +  
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob,  " +  
		"	SUM(pmHoPrepAtt)  AS pmHoPrepAtt,  SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,   " +  
		"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob, " +  
		"	SUM(pmHoExeAtt)  AS pmHoExeAtt, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob  " +  
		" FROM DC_E_ERBS_UTRANCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " + 
		 "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN ( $ERBSLIST   ) " +  
		" GROUP BY NODE_NAME, CELL_NAME, OSS_ID " +  
		" UNION " +  
		" SELECT " +  
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, " +  
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob,  " +  
		"	SUM(pmHoPrepAtt)  AS pmHoPrepAtt,  SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,   " +  
		"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob,  " +  
		"	SUM(pmHoExeAtt)  AS pmHoExeAtt, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob  " +  
		" FROM DC_E_ERBSG2_UTRANCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
		   "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN ( $ERBSLIST  ) " +  
		" GROUP BY NODE_NAME, CELL_NAME ,OSS_ID) UTRANCELLRELATION " +  
		" ON UTRANCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME " +  
		" AND UTRANCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " +  
		"	AND UTRANCELLRELATION.OSS_ID = UTRANCELLRELATION.OSS_ID " +  
		" LEFT JOIN " +  
		" (SELECT " +  
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, " +  
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepAtt) AS pmHoPrepAtt,   " +  
		"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeAtt)  AS pmHoExeAtt " +  
		" FROM DC_E_ERBS_GERANCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID= '$DATETIMEID' " + 
		   "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN (  $ERBSLIST   ) " +  
		" GROUP BY NODE_NAME, CELL_NAME, OSS_ID" +  
		" UNION " +  
		" SELECT " +  
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME,OSS_ID, " +  
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepAtt) AS pmHoPrepAtt,   " +  
		"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeAtt)  AS pmHoExeAtt " +  
		" FROM DC_E_ERBSG2_GERANCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
		   "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN ( $ERBSLIST   ) " +  
		" GROUP BY NODE_NAME, CELL_NAME,OSS_ID ) GERANCELLRELATION " +  

		" ON GERANCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME  " +  
		" AND GERANCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " +  
		"	AND GERANCELLRELATION.OSS_ID = GERANCELLRELATION.OSS_ID " +  
		" LEFT JOIN " +  

		" (SELECT " +  
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID," +  
		"	SUM(pmHoPrepSucc1xRttSrvcc) AS pmHoPrepSucc1xRttSrvcc,  SUM(pmHoPrepAtt1xRttSrvcc) AS pmHoPrepAtt1xRttSrvcc " +  
		" FROM DC_E_ERBS_CDMA20001XRTTCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
		   "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN (  $ERBSLIST   ) " +  
		" GROUP BY NODE_NAME, CELL_NAME, OSS_ID" +  
		" UNION " +  
		" SELECT " +  
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID," +   
		"	SUM(pmHoPrepSucc1xRttSrvcc) AS pmHoPrepSucc1xRttSrvcc,  SUM(pmHoPrepAtt1xRttSrvcc) AS pmHoPrepAtt1xRttSrvcc " +  
		" FROM DC_E_ERBSG2_CDMA20001XRTTCELLRELATION_RAW RAW" +  
		" WHERE UTC_DATETIME_ID = '$DATETIMEID' " +  
		   "   AND OSS_ID = '$OSS'    " + 
		"	AND ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') AND NODE_NAME IN (  $ERBSLIST   ) " +  
		" GROUP BY NODE_NAME, CELL_NAME ,OSS_ID ) CDMA20001XRTTCELLRELATION " +  

		" ON CDMA20001XRTTCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME  " +  
		" AND CDMA20001XRTTCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " +
		"	AND CDMA20001XRTTCELLRELATION.OSS_ID = CDMA20001XRTTCELLRELATION.OSS_ID ";
	
	public static String MobilityCellHOKPI = "SELECT top(100) KPI_VALUE, PrepSucc, PrepAtt, ExeSucc, ExeAtt, NODE_NAME, CELL_NAME, TIMELEVEL, DC_RELEASE, DATE_ID, OSS_ID FROM (  " +

	"SELECT  " +
	"	EUTRANCELLRELATION.NODE_NAME as NODE_NAME, EUTRANCELLRELATION.CELL_NAME as CELL_NAME,EUTRANCELLRELATION.OSS_ID as OSS_ID," +
	"	EUTRANCELLRELATION.TIMELEVEL, EUTRANCELLRELATION.DC_RELEASE,   " +
	"	EUTRANCELLRELATION.DATE_ID AS DATE_ID, " +
	"	(EUTRANCELL.pmCellHoPrepSuccLteIntraF + EUTRANCELL.pmCellHoPrepSuccLteInterF   " +
	"	+ ISNULL(UTRANCELLRELATION.pmHoPrepSucc, 0)  + ISNULL(GERANCELLRELATION.pmHoPrepSucc, 0)   " +
	"	+ ISNULL(CDMA20001XRTTCELLRELATION.pmHoPrepSucc1xRttSrvcc, 0)  - ISNULL(EUTRANCELLRELATION.pmHoPrepSuccNonMob, 0)   " +
	"	- ISNULL(UTRANCELLRELATION.pmHoPrepSuccNonMob, 0) ) AS PrepSucc,   " +
	  
	"	(EUTRANCELL.pmCellHoPrepAttLteIntraF + EUTRANCELL.pmCellHoPrepAttLteInterF   " +
	"	+ ISNULL(UTRANCELLRELATION.pmHoPrepAtt, 0)  + ISNULL(GERANCELLRELATION.pmHoPrepAtt, 0)   " +
	"	+ ISNULL(CDMA20001XRTTCELLRELATION.pmHoPrepAtt1xRttSrvcc, 0)  - ISNULL(EUTRANCELLRELATION.pmHoPrepAttNonMob, 0)   " +
	"	- ISNULL(UTRANCELLRELATION.pmHoPrepAttNonMob, 0)  - EUTRANCELL.pmHoPrepRejInUlThres) AS PrepAtt,  " + 
	  
	"	(EUTRANCELL.pmCellHoExeSuccLteIntraF + EUTRANCELL.pmCellHoExeSuccLteInterF   " +
	"	+ ISNULL(UTRANCELLRELATION.pmHoExeSucc, 0)  + ISNULL(GERANCELLRELATION.pmHoExeSucc, 0)   " +
	"	- ISNULL(EUTRANCELLRELATION.pmHoExeSuccNonMob, 0)  - ISNULL(UTRANCELLRELATION.pmHoExeSuccNonMob, 0) ) AS ExeSucc,   " +
	  
	"	(EUTRANCELL.pmCellHoExeAttLteIntraF + EUTRANCELL.pmCellHoExeAttLteInterF   " +
	"	+ ISNULL(UTRANCELLRELATION.pmHoExeAtt, 0)  + ISNULL(GERANCELLRELATION.pmHoExeAtt, 0)   " +
	"	- ISNULL(EUTRANCELLRELATION.pmHoExeAttNonMob, 0)  - ISNULL(UTRANCELLRELATION.pmHoExeAttNonMob, 0) ) AS ExeAtt,   " +
	  
		"(100 * ((PrepSucc / PrepAtt) * (ExeSucc / ExeAtt))) AS KPI_VALUE " +

	"FROM  " +

	"(SELECT  " +
	"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME,OSS_ID, " +
	"	TIMELEVEL, DC_RELEASE, DATE_ID ,  " +
	"	SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob, SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,    " +
	"	SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob,SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " +
	"FROM DC_E_ERBS_EUTRANCELLRELATION_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " +
	"GROUP BY NODE_NAME, CELL_NAME,OSS_ID,TIMELEVEL, DC_RELEASE, DATE_ID   " +
	"UNION  " +
	"SELECT  " +
	"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME,OSS_ID, " +
	"	TIMELEVEL, DC_RELEASE, DATE_ID, " +
	"	SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob, SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,    " +
	"	SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob,SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " +
	"FROM DC_E_ERBSG2_EUTRANCELLRELATION_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " +
	"GROUP BY NODE_NAME, CELL_NAME, OSS_ID, TIMELEVEL, DC_RELEASE, DATE_ID ) EUTRANCELLRELATION " +

	"LEFT JOIN " +

	"(SELECT  " +
	"	ERBS as NODE_NAME, EUTRANCELLFDD as CELL_NAME, OSS_ID, " +
	"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF,  " +
	"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +
	"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +
	"FROM DC_E_ERBS_EUTRANCELLFDD_DAY DAYREP   " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " +
	"UNION " +
	"SELECT  " +
	"	ERBS as NODE_NAME, EUTRANCELLTDD as CELL_NAME, OSS_ID, " +
	"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF,  " +
	"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +
	"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +
	"FROM DC_E_ERBS_EUTRANCELLTDD_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " + 
	"UNION  " +
	"SELECT  " +
	"	ERBS as NODE_NAME, EUTRANCELLFDD as CELL_NAME, OSS_ID, " +
	"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF,  " +
	"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +
	"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +
	"FROM DC_E_ERBSG2_EUTRANCELLFDD_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " +
	"UNION " +
	"SELECT  " +
	"	ERBS as NODE_NAME, EUTRANCELLTDD as CELL_NAME, OSS_ID, " +
	"	pmCellHoPrepSuccLteIntraF,  pmCellHoPrepSuccLteInterF, pmCellHoPrepAttLteIntraF,  " +
	"	pmCellHoPrepAttLteInterF, pmHoPrepRejInUlThres, pmCellHoExeSuccLteIntraF, " +
	"	pmCellHoExeSuccLteInterF, pmCellHoExeAttLteIntraF, pmCellHoExeAttLteInterF " +
	"FROM DC_E_ERBSG2_EUTRANCELLTDD_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' ) EUTRANCELL " +

	"	ON EUTRANCELL.NODE_NAME = EUTRANCELLRELATION.NODE_NAME   " +
	"	AND EUTRANCELL.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " +
	"	AND EUTRANCELL.OSS_ID = EUTRANCELLRELATION.OSS_ID " +
	"LEFT JOIN " +

	"(SELECT  " +
	"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, " +
	"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob,   " +
	"	SUM(pmHoPrepAtt)  AS pmHoPrepAtt,  SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,    " +
	"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob, " +
	"	SUM(pmHoExeAtt)  AS pmHoExeAtt, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " +
	"FROM DC_E_ERBS_UTRANCELLRELATION_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED'  " +
	"GROUP BY NODE_NAME, CELL_NAME , OSS_ID " +
	"UNION " +
	"SELECT  " +
	"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, " +
	"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob,   " +
	"	SUM(pmHoPrepAtt)  AS pmHoPrepAtt,  SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,    " +
	"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob, " +
	"	SUM(pmHoExeAtt)  AS pmHoExeAtt, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " +
	"FROM DC_E_ERBSG2_UTRANCELLRELATION_DAY DAYREP  " + 
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED'  " +
	"GROUP BY NODE_NAME, CELL_NAME,OSS_ID ) UTRANCELLRELATION " +

	"ON UTRANCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME " +
	"AND UTRANCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " +
	"AND UTRANCELLRELATION.OSS_ID = EUTRANCELLRELATION.OSS_ID " +

	"LEFT JOIN " +

	"(SELECT " +
	"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME,  OSS_ID, " +
	"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepAtt) AS pmHoPrepAtt,    " +
	"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeAtt)  AS pmHoExeAtt " +
	"FROM DC_E_ERBS_GERANCELLRELATION_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " +
	"GROUP BY NODE_NAME, CELL_NAME,OSS_ID " +
	"UNION " +
	"SELECT " +
	"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID," +
	"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepAtt) AS pmHoPrepAtt,    " +
	"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeAtt)  AS pmHoExeAtt " +
	"FROM DC_E_ERBSG2_GERANCELLRELATION_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " +
	"GROUP BY NODE_NAME, CELL_NAME ,OSS_ID) GERANCELLRELATION " +

	"ON GERANCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME   " +
	"AND GERANCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME  " +
	"AND GERANCELLRELATION.OSS_ID = EUTRANCELLRELATION.OSS_ID  " +

	"LEFT JOIN " +

	"(SELECT  " +
	"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, " +
	"	SUM(pmHoPrepSucc1xRttSrvcc) AS pmHoPrepSucc1xRttSrvcc,  SUM(pmHoPrepAtt1xRttSrvcc) AS pmHoPrepAtt1xRttSrvcc " +
	"FROM DC_E_ERBS_CDMA20001XRTTCELLRELATION_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
		"AND ROWSTATUS = 'AGGREGATED' " +
	"GROUP BY NODE_NAME, CELL_NAME ,OSS_ID " +
	"UNION  " +
	"SELECT  " +
		"ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, " +
		"SUM(pmHoPrepSucc1xRttSrvcc) AS pmHoPrepSucc1xRttSrvcc,  SUM(pmHoPrepAtt1xRttSrvcc) AS pmHoPrepAtt1xRttSrvcc " +
	"FROM DC_E_ERBSG2_CDMA20001XRTTCELLRELATION_DAY DAYREP  " +
	"WHERE DATE_ID = '$DATEID'  " + 
	"	AND ROWSTATUS = 'AGGREGATED' " +
	"GROUP BY NODE_NAME, CELL_NAME ,OSS_ID) CDMA20001XRTTCELLRELATION " +

	"ON CDMA20001XRTTCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME   " +
	"AND CDMA20001XRTTCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " +
	"AND CDMA20001XRTTCELLRELATION.OSS_ID = EUTRANCELLRELATION.OSS_ID " +

	") as KPI order by KPI_VALUE ASC ";	
		
public static String MobilityCellRelationsHOKPI = "SELECT	 " + 
			"EUTRANCELLRELATION.NODE_NAME as NODE_NAME, EUTRANCELLRELATION.CELL_NAME as CELL_NAME, EUTRANCELLRELATION.OSS_ID AS OSS_ID,  " + 
			"EUTRANCELLRELATION.TIMELEVEL, EUTRANCELLRELATION.DC_RELEASE, EUTRANCELLRELATION.EUtranCellRelation AS REL_NAME, " + 
			"EUTRANCELLRELATION.DATE_ID AS DATE_ID, EUTRANCELLRELATION.FREQ_REL AS FREQ_REL, " + 

			"( ISNULL(EUTRANCELLRELATION.pmHoPrepSuccLteIntraF, 0) + ISNULL(EUTRANCELLRELATION.pmHoPrepSuccLteInterF, 0) +  " + 
			"ISNULL(UTRANCELLRELATION.pmHoPrepSucc, 0) + ISNULL(GERANCELLRELATION.pmHoPrepSucc, 0) + " + 
			"ISNULL(CDMA20001XRTTCELLRELATION.pmHoPrepSucc1xRttSrvcc, 0) - ISNULL(EUTRANCELLRELATION.pmHoPrepSuccNonMob, 0) -  " + 
			"ISNULL(UTRANCELLRELATION.pmHoPrepSuccNonMob, 0) ) AS PrepSucc,  " + 
			 
			"( ISNULL(EUTRANCELLRELATION.pmHoPrepAttLteIntraF, 0)  + ISNULL(EUTRANCELLRELATION.pmHoPrepAttLteInterF, 0)  " + 
			"+ ISNULL(UTRANCELLRELATION.pmHoPrepAtt, 0)  + ISNULL(GERANCELLRELATION.pmHoPrepAtt, 0)   " + 
			"+ ISNULL(CDMA20001XRTTCELLRELATION.pmHoPrepAtt1xRttSrvcc, 0)  - ISNULL(EUTRANCELLRELATION.pmHoPrepAttNonMob, 0)   " + 
			"- ISNULL(UTRANCELLRELATION.pmHoPrepAttNonMob, 0)  - EUTRANCELL.pmHoPrepRejInUlThres) AS PrepAtt,  " + 
			 
			"( ISNULL(EUTRANCELLRELATION.pmHoExeSuccLteIntraF, 0)  + ISNULL(EUTRANCELLRELATION.pmHoExeSuccLteInterF, 0)   " + 
			"+ ISNULL(UTRANCELLRELATION.pmHoExeSucc, 0)  + ISNULL(GERANCELLRELATION.pmHoExeSucc, 0)   " + 
			"- ISNULL(EUTRANCELLRELATION.pmHoExeSuccNonMob, 0)  - ISNULL(UTRANCELLRELATION.pmHoExeSuccNonMob, 0) ) AS ExeSucc,  " + 
			 
			"( ISNULL(EUTRANCELLRELATION.pmHoExeAttLteIntraF, 0)  + ISNULL(EUTRANCELLRELATION.pmHoExeAttLteInterF, 0)   " + 
			"+ ISNULL(UTRANCELLRELATION.pmHoExeAtt, 0)  + ISNULL(GERANCELLRELATION.pmHoExeAtt, 0)   " + 
			"- ISNULL(EUTRANCELLRELATION.pmHoExeAttNonMob, 0)  - ISNULL(UTRANCELLRELATION.pmHoExeAttNonMob, 0) ) AS ExeAtt,   " + 
		  
		"	(100 * ((PrepSucc / PrepAtt) * (ExeSucc / ExeAtt))) AS KPI_VALUE " + 

		"FROM  " + 

		"(SELECT  " + 
		"	ERBS as NODE_NAME,  TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, " + 
		"	TIMELEVEL, DC_RELEASE, DATE_ID , EUtranCellRelation,EUtranFreqRelation as FREQ_REL, " + 
		"	SUM(pmHoPrepSuccLteIntraF) AS pmHoPrepSuccLteIntraF, SUM(pmHoPrepSuccLteInterF) AS pmHoPrepSuccLteInterF, " + 
		"	SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob, SUM(pmHoPrepAttLteIntraF) AS pmHoPrepAttLteIntraF, " + 
		"	SUM(pmHoPrepAttLteInterF) AS pmHoPrepAttLteInterF, SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob, " + 
		"	SUM(pmHoExeSuccLteIntraF) AS pmHoExeSuccLteIntraF, SUM(pmHoExeSuccLteInterF) AS pmHoExeSuccLteInterF, " + 
		"	SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob, SUM(pmHoExeAttLteIntraF) AS pmHoExeAttLteIntraF, " + 
		"	SUM(pmHoExeAttLteInterF) AS pmHoExeAttLteInterF, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " + 
		"FROM DC_E_ERBS_EUTRANCELLRELATION_DAY DAYREP   " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME,OSS_ID, TIMELEVEL, DC_RELEASE, DATE_ID,FREQ_REL, EUtranCellRelation " + 
		"UNION  " + 
		"SELECT  " + 
		"	ERBS as NODE_NAME,  TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME,  OSS_ID, " + 
		"	TIMELEVEL, DC_RELEASE, DATE_ID ,  EUtranCellRelation, EUtranFreqRelation as FREQ_REL," + 
		"	SUM(pmHoPrepSuccLteIntraF) AS pmHoPrepSuccLteIntraF, SUM(pmHoPrepSuccLteInterF) AS pmHoPrepSuccLteInterF, " + 
		"	SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob, SUM(pmHoPrepAttLteIntraF) AS pmHoPrepAttLteIntraF, " + 
		"	SUM(pmHoPrepAttLteInterF) AS pmHoPrepAttLteInterF, SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob, " + 
		"	SUM(pmHoExeSuccLteIntraF) AS pmHoExeSuccLteIntraF, SUM(pmHoExeSuccLteInterF) AS pmHoExeSuccLteInterF, " + 
		"	SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob, SUM(pmHoExeAttLteIntraF) AS pmHoExeAttLteIntraF, " + 
		"	SUM(pmHoExeAttLteInterF) AS pmHoExeAttLteInterF, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " + 
		"FROM DC_E_ERBSG2_EUTRANCELLRELATION_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME,OSS_ID, TIMELEVEL, DC_RELEASE, DATE_ID,FREQ_REL, EUtranCellRelation ) EUTRANCELLRELATION " + 

		"LEFT JOIN " + 

		"(SELECT  " + 
		"	ERBS as NODE_NAME, EUTRANCELLFDD as CELL_NAME,OSS_ID, pmHoPrepRejInUlThres " + 
		"FROM DC_E_ERBS_EUTRANCELLFDD_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"UNION " + 
		"SELECT  " + 
		"	ERBS as NODE_NAME, EUTRANCELLTDD as CELL_NAME,OSS_ID,pmHoPrepRejInUlThres " + 
		"FROM DC_E_ERBS_EUTRANCELLTDD_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"UNION  " + 
		"SELECT  " + 
		"	ERBS as NODE_NAME, EUTRANCELLFDD as CELL_NAME,OSS_ID,pmHoPrepRejInUlThres " + 
		"FROM DC_E_ERBSG2_EUTRANCELLFDD_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ($CELL_LIST )  " + 
		"UNION " + 
		"SELECT  " + 
		"	ERBS as NODE_NAME, EUTRANCELLTDD as CELL_NAME,OSS_ID,pmHoPrepRejInUlThres " + 
		"FROM DC_E_ERBSG2_EUTRANCELLTDD_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  ) EUTRANCELL " + 

		"	ON EUTRANCELL.NODE_NAME = EUTRANCELLRELATION.NODE_NAME   " + 
		"	AND EUTRANCELL.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " + 
		"	AND EUTRANCELL.OSS_ID = EUTRANCELLRELATION.OSS_ID " + 
		
		"LEFT JOIN " + 

		"(SELECT  " + 
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, UtranCellRelation, UtranFreqRelation as FREQ_REL," + 
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc, SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob, " + 
		"	SUM(pmHoPrepAtt) AS pmHoPrepAtt, SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob, " + 
		"	SUM(pmHoExeSucc) AS pmHoExeSucc, SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob, " + 
		"	SUM(pmHoExeAtt) AS pmHoExeAtt, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " + 
		"FROM DC_E_ERBS_UTRANCELLRELATION_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME, UtranCellRelation ,OSS_ID,FREQ_REL " + 
		"UNION " + 
		"SELECT  " + 
			"ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, UtranCellRelation,UtranFreqRelation as FREQ_REL, " + 
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc,  SUM(pmHoPrepSuccNonMob) AS pmHoPrepSuccNonMob,   " + 
		"	SUM(pmHoPrepAtt)  AS pmHoPrepAtt,  SUM(pmHoPrepAttNonMob) AS pmHoPrepAttNonMob,    " + 
		"	SUM(pmHoExeSucc)  AS pmHoExeSucc, SUM(pmHoExeSuccNonMob) AS pmHoExeSuccNonMob, " + 
		"	SUM(pmHoExeAtt)  AS pmHoExeAtt, SUM(pmHoExeAttNonMob) AS pmHoExeAttNonMob " + 
		"FROM DC_E_ERBSG2_UTRANCELLRELATION_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " +  
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME, UtranCellRelation,OSS_ID,FREQ_REL ) UTRANCELLRELATION " + 

		"ON UTRANCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME " + 
		"AND UTRANCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " + 
		"AND UTRANCELLRELATION.UtranCellRelation = EUTRANCELLRELATION.EUtranCellRelation " + 
		"AND UTRANCELLRELATION.OSS_ID = EUTRANCELLRELATION.OSS_ID " + 
		"AND UTRANCELLRELATION.FREQ_REL = EUTRANCELLRELATION.FREQ_REL "+ 
		
		"LEFT JOIN " + 

		"(SELECT  " + 
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, GeranCellRelation,  GeranFreqGroupRelation as FREQ_REL," + 
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc, SUM(pmHoPrepAtt) AS pmHoPrepAtt, " + 
		"	SUM(pmHoExeSucc) AS pmHoExeSucc, SUM(pmHoExeAtt) AS pmHoExeAtt " + 
		"FROM DC_E_ERBS_GERANCELLRELATION_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME, GeranCellRelation, OSS_ID,FREQ_REL " + 
		"UNION " + 
		"SELECT  " + 
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, GeranCellRelation, GeranFreqGroupRelation as FREQ_REL, " + 
		"	SUM(pmHoPrepSucc) AS pmHoPrepSucc, SUM(pmHoPrepAtt) AS pmHoPrepAtt, " + 
		"	SUM(pmHoExeSucc) AS pmHoExeSucc, SUM(pmHoExeAtt) AS pmHoExeAtt " + 
		"FROM DC_E_ERBSG2_GERANCELLRELATION_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME, GeranCellRelation,OSS_ID ,FREQ_REL) GERANCELLRELATION " + 

		"ON GERANCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME   " + 
		"AND GERANCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " + 
		"AND GERANCELLRELATION.GeranCellRelation = EUTRANCELLRELATION.EUtranCellRelation " + 
		"AND GERANCELLRELATION.OSS_ID = EUTRANCELLRELATION.OSS_ID " + 
		"AND GERANCELLRELATION.FREQ_REL = EUTRANCELLRELATION.FREQ_REL  " + 
		
		"LEFT JOIN " + 

		"(SELECT  " + 
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, MOID,  Cdma20001xRttFreqRelation as FREQ_REL," + 
		"	SUM(pmHoPrepSucc1xRttSrvcc) AS pmHoPrepSucc1xRttSrvcc, SUM(pmHoPrepAtt1xRttSrvcc) AS pmHoPrepAtt1xRttSrvcc " + 
		"FROM DC_E_ERBS_CDMA20001XRTTCELLRELATION_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED' " + 
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME, MOID, OSS_ID, FREQ_REL " + 
		"UNION  " + 
		"SELECT  " + 
		"	ERBS as NODE_NAME, TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, OSS_ID, MOID, Cdma20001xRttFreqRelation as FREQ_REL, " + 
		"	SUM(pmHoPrepSucc1xRttSrvcc) AS pmHoPrepSucc1xRttSrvcc, SUM(pmHoPrepAtt1xRttSrvcc) AS pmHoPrepAtt1xRttSrvcc " + 
		"FROM DC_E_ERBSG2_CDMA20001XRTTCELLRELATION_DAY DAYREP  " + 
		"WHERE DATE_ID = '$DATEID'  " + 
		"	AND ROWSTATUS = 'AGGREGATED'" +
		"	AND CELL_NAME IN ( $CELL_LIST )  " + 
		"GROUP BY NODE_NAME, CELL_NAME, MOID, OSS_ID,FREQ_REL ) CDMA20001XRTTCELLRELATION " + 

		"ON CDMA20001XRTTCELLRELATION.NODE_NAME = EUTRANCELLRELATION.NODE_NAME   " + 
		"AND CDMA20001XRTTCELLRELATION.CELL_NAME = EUTRANCELLRELATION.CELL_NAME " + 
		"AND CDMA20001XRTTCELLRELATION.MOID LIKE '%' + EUTRANCELLRELATION.EUtranCellRelation " +
		"AND CDMA20001XRTTCELLRELATION.OSS_ID = EUTRANCELLRELATION.OSS_ID ";
}

