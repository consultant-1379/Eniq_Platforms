package com.ericsson.netan.vowifi;

import com.ericsson.networkanalytics.KPIQueries;
import com.ericsson.networkanalytics.TopologyQueries;

public class VoWiFiQueries {

	@TopologyQueries
	public String getGGSNTopology(String OSS_ID){
		
		return "select NE_FDN, NE_NAME, NE_VERSION, NE_TYPE from DIM_E_CN_GGSN where STATUS = 'ACTIVE' and OSS_ID = '"+OSS_ID+"' order by NE_FDN";
		
	 }
	
	@TopologyQueries
	public String getWMGTopology(String OSS_ID){
		
		return "select NE_FDN, NE_NAME, NE_VERSION, NE_TYPE from DIM_E_CN_WMG where STATUS = 'ACTIVE' and OSS_ID = '"+OSS_ID+"' order by NE_FDN";
		
	}
	
	@TopologyQueries
	public String getCSCFTopology(String OSS_ID){
		
		return "select NE_FDN, NE_NAME, NE_VERSION, NE_TYPE from DIM_E_CN_IMS where STATUS = 'ACTIVE' and OSS_ID = '"+OSS_ID+"' and NE_TYPE like '%CSCF%' order by NE_FDN";
	}
	
	@KPIQueries
	public String KPI05(String OSS_ID, String datetime_id){
		String KPI_ID = "5";
		String KPI = "select GGSN as NODE_NAME, DATETIME_ID, TIMELEVEL, DC_RELEASE, 100*(sum(pgwApnCompletedS2bEpsBearerActivation)/sum(pgwApnAttemptedS2bEpsBearerActivation)) as KPI_VALUE_5 from DC_E_GGSN_APN_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') group by NODE_NAME, TIMELEVEL, DATETIME_ID, DC_RELEASE"; 
		return KPI_ID+"-"+KPI;
	}
	
	@KPIQueries
	public String KPI10(String OSS_ID, String datetime_id){
		String KPI_ID = "10";
		String KPI = "select GGSN as NODE_NAME, DATETIME_ID, TIMELEVEL, DC_RELEASE, 100*(sum(IratHoCompFromUwlanToEutran)/sum(IratHoAttFromUwlanToEutran)) as KPI_VALUE_10 from DC_E_GGSN_GGSN_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') group by NODE_NAME, TIMELEVEL, DATETIME_ID, DC_RELEASE"; 
		return KPI_ID+"-"+KPI;
	}
	
	@KPIQueries
	public String KPI02_03_04(String OSS_ID, String datetime_id){
		String KPI_ID = "2,3,4";
		String KPI = "select NE_NAME as NODE_NAME, DATETIME_ID, TIMELEVEL, DC_RELEASE, 100*(sum(IKE_SA_Establishment_Success)/sum(IKE_SA_Establishment_Attempts)) as KPI_VALUE_2,"
				+ "100*(sum(Authentication_and_Auth_Success)/sum(Authentication_and_Auth_Attempts)) as KPI_VALUE_3,"
				+ "	100*(sum(Ipsec_Tunnel_Establishment_Success)/sum(Ipsec_Tunnel_Establishment_Attempts)) as KPI_VALUE_4 "
				+ "from DC_E_WMG_IPSEC_SUMMARY_STATS_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') group by NODE_NAME, DATETIME_ID, TIMELEVEL, DC_RELEASE"; 
		return KPI_ID+"-"+KPI;
	}
	
	@KPIQueries
	public String KPI09(String OSS_ID, String datetime_id){
		String KPI_ID = "9";
		String KPI = "select NE_NAME as NODE_NAME, DATETIME_ID, TIMELEVEL, DC_RELEASE, 100*(sum(Successful_Handoff_Calls)/sum(Handoff_Calls_Attempted)) as KPI_VALUE_9 from DC_E_WMG_SC_HANDOFF_CALL_STATS_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') group by NODE_NAME, DATETIME_ID, TIMELEVEL, DC_RELEASE"; 
		return KPI_ID+"-"+KPI;
	}
	
	@KPIQueries
	public String KPI06(String OSS_ID, String datetime_id){
		String KPI_ID = "6";
		String KPI = "SELECT g3ManagedElement as NODE_NAME, MOID, DATETIME_ID, TIMELEVEL, DC_RELEASE, scscfSuccessfulRegistrationPerAccess, scscfFailedRegistrationPerAccess, scscfAttemptedRegistrationPerAccess FROM DC_E_IMS_CSCF2_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and MOID like '%IEEE%'"
				+ " union all "
				+ "SELECT NE_NAME as NODE_NAME, MOID, DATETIME_ID, TIMELEVEL, DC_RELEASE, scscfSuccessfulRegistrationPerAccess, scscfFailedRegistrationPerAccess, scscfAttemptedRegistrationPerAccess FROM DC_E_CSCF_REG_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and MOID like '%IEEE%'";
		return KPI_ID+"-"+KPI;
	}
	
	@KPIQueries
	public String KPI07_08(String OSS_ID, String datetime_id){
		String KPI_ID = "7,8";
		String KPI = "SELECT g3ManagedElement as NODE_NAME, MOID, DATETIME_ID, TIMELEVEL, DC_RELEASE, scscfOrigSuccessfulEstablishedInvitePerAccess, scscfOrigFailedInvitePerAccess, scscfOrigAttemptedInvitePerAccess, scscfTermSuccessfulEstablishedInvitePerAccess, scscfTermFailedInvitePerAccess, scscfTermAttemptedInvitePerAccess FROM DC_E_IMS_CSCF2_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and MOID like '%IEEE%'"
				+ " union all "
				+ "SELECT NE_NAME as NODE_NAME, MOID, DATETIME_ID, TIMELEVEL, DC_RELEASE, scscfOrigSuccessfulEstablishedInvitePerAccess, scscfOrigFailedInvitePerAccess, scscfOrigAttemptedInvitePerAccess, scscfTermSuccessfulEstablishedInvitePerAccess, scscfTermFailedInvitePerAccess, scscfTermAttemptedInvitePerAccess FROM DC_E_CSCF_SESSION_RAW RAW where UTC_DATETIME_ID = '"+datetime_id+"' and OSS_ID = '"+OSS_ID+"' and ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and MOID like '%IEEE%'"; 
		return KPI_ID+"-"+KPI;
	}
}
