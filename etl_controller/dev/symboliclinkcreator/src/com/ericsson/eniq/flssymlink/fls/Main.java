package com.ericsson.eniq.flssymlink.fls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.eniq.common.DatabaseConnections;
import com.ericsson.eniq.enminterworking.EnmInterworking;
import com.ericsson.eniq.enminterworking.automaticNAT.NodeAssignmentCache;
import com.ericsson.eniq.flssymlink.StaticProperties;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

public class Main {

	public static Main main = null;
	public static String eniqName = null;
	public static String timeFormat = null;
	public static int threadPoolCorePoolSizeStatic;
	public static int threadPoolMaxPoolSizeStatic;
	public static int natThreadPoolCorePoolSizeStatic;
	public static int natThreadPoolMaxPoolSizeStatic;
	public static int pmQueryIntervalStatic;
	public static int pmQueryStartDelayStatic;
	public static int topologyQueryIntervalStatic;
	public static int topologyQueryStartDelayStatic;

	public static boolean fls_admin_flag=false;
	public static boolean pmFlag = false;
	public static boolean topologyFlag = false;
	public static boolean isBulkcmInstalled = isBulkCmInstalled();

	public static Main getInstance() {

		if (main == null) {
			main = new Main();
		}
		return main;
	}

	// to store id values
	public Map<String, Long> tokenMap;

	// To store fls query responses
	public LinkedBlockingQueue<Runnable> pmQueue;
	public LinkedBlockingQueue<TopologyJson> topologyQueue;
	public LinkedBlockingQueue<Runnable> assignNodesQueue;

	// threadPoolExecutor reference
	private static ThreadPoolExecutor threadPoolExecutor;
	private static ThreadPoolExecutor threadPoolExecutorforNAT;

	public static Date lastDate = new Date();
	public static String flsStartDateTime=getCurrentTime();
	public static String flsStartDateTimeAdminUi=getCurrentTime();
	
	// to store ENM Server Details
	ENMServerDetails cache;

	private Logger log;

	// Constructor
	public Main() {
//		Initialize cache
		try {
			StaticProperties.reload();
			new NodeAssignmentCache();
		} catch (IOException | SQLException e) {
			//e.getMessage();
		}
		tokenMap = new HashMap<String, Long>();
		pmQueue = new LinkedBlockingQueue<Runnable>();
		topologyQueue = new LinkedBlockingQueue<TopologyJson>();
		assignNodesQueue = new LinkedBlockingQueue<Runnable>();
		threadPoolCorePoolSizeStatic = Integer
				.parseInt(StaticProperties.getProperty("SYMBOLICLINK_THREADPOOL_COREPOOLSIZE", "15"));
		threadPoolMaxPoolSizeStatic = Integer
				.parseInt(StaticProperties.getProperty("SYMBOLICLINK_THREAD_POOL_MAXPOOLSIZE", "30"));
		natThreadPoolCorePoolSizeStatic = Integer
				.parseInt(StaticProperties.getProperty("NAT_THREADPOOL_COREPOOLSIZE", "5"));
		natThreadPoolMaxPoolSizeStatic = Integer
				.parseInt(StaticProperties.getProperty("NAT_THREAD_POOL_MAXPOOLSIZE", "15"));
		pmQueryIntervalStatic = Integer.parseInt(StaticProperties.getProperty("SYMBOLICLINK_PMQUERY_INTERVAL", "3"));
		pmQueryStartDelayStatic = Integer
				.parseInt(StaticProperties.getProperty("SYMBOLICLINK_PMQUERY_START_DELAY", "1"));
		topologyQueryIntervalStatic = Integer
				.parseInt(StaticProperties.getProperty("SYMBOLICLINK_TOPOLOGYQUERY_INTERVAL", "15"));
		topologyQueryStartDelayStatic = Integer
				.parseInt(StaticProperties.getProperty("SYMBOLICLINK_TOPOLOGYQUERY_START_DELAY", "15"));

		threadPoolExecutorforNAT = new ThreadPoolExecutor(natThreadPoolCorePoolSizeStatic, natThreadPoolMaxPoolSizeStatic, 3, TimeUnit.MILLISECONDS, assignNodesQueue);
		threadPoolExecutorforNAT.prestartAllCoreThreads();
	}

	// main method
	public static void main(String[] args) throws Exception {
		try {
			main = getInstance();
			
			// log file creation for Fls querying process

			main.log = Logger.getLogger("symboliclinkcreator.fls");
			main.log.info("Initializing FLS service!!!");
			
			if (main.enableFlsService()) {
				System.setSecurityManager(new com.distocraft.dc5000.etl.engine.ETLCSecurityManager());

				// Initialising and binding RMI methods
				try {
					final EnmInterworking enmInter = new EnmInterworking();
					if (!enmInter.initRMI()) {
						main.log.severe("RMI Initialisation failed... Exiting");
						System.exit(0);
					} else {
						main.log.info("RMI initialisation done");
					}
				} catch (Exception e) {
					main.log.warning("RMI Initialization failed exceptionally: " + e.getMessage());
				}
				getEniqName();

				if (args.length == 1) {
					Main.timeFormat = args[0];
				}

				// cache initialization(enm server details)
				HashMap<String, ENMServerDetails> enmServerDetails = CacheENMServerDetails.getInstance(main.log);

				Process pFLS = Runtime.getRuntime().exec(
						"cat " + StaticProperties.getProperty("FLS_CONF_PATH", "/eniq/installation/config/fls_conf"));
				BufferedReader inputFLS = new BufferedReader(new InputStreamReader(pFLS.getInputStream()));

				String line;
				String[] fls_enabled_enm = null;
				while ((line = inputFLS.readLine()) != null) {
					fls_enabled_enm = line.split("\\s+");
				}

				if (fls_enabled_enm.length > 0) {

					main.cache = enmServerDetails.get(fls_enabled_enm[0]);
					if(main.cache == null){
						main.log.warning("cache is empty");
						System.exit(0);
					}

				} else {

					main.log.warning("fls_enabled_enm server oss id not found");
					System.exit(0);
				}

				// Persisted Token restore
				try {
					File f = new File(System.getProperty("CONF_DIR") + File.separator + "Persisted.ser");
					PersistedToken persistedToken = new PersistedToken();
					if (f.exists()) {
						try {
							final FileInputStream fin = new FileInputStream(
									System.getProperty("CONF_DIR") + File.separator + "Persisted.ser");
							if (fin.getChannel().size() > 0) {
								final ObjectInputStream in = new ObjectInputStream(fin);

								persistedToken = (PersistedToken) in.readObject();
								main.tokenMap = persistedToken.tokenMap;
								if(!main.tokenMap.isEmpty()){

								main.log.info("Persisted token map value after restart is:"
										+ persistedToken.tokenMap.toString());
									} else {


								// code to query fls by user specified date and time
								fls_admin_flag=true;
								main.log.info("fls_admin_flag is :"+fls_admin_flag);
								
								pmFlag = true;
								topologyFlag = true;
																				
							}	
								in.close();
							}
							fin.close();
						} catch (Exception e) {
							main.log.warning("Exception during retrieving the Persisted token value." + e.getMessage());
							fls_admin_flag=true;
							main.log.info("fls_admin_flag is :"+fls_admin_flag);
							
							pmFlag = true;
							topologyFlag = true;
												
						}
					}else{
						 
							File date_fls=new File(System.getProperty("CONF_DIR") + File.separator +"date_fls.txt");
							//String dateTime=null;
							if(date_fls.exists()){
								final FileInputStream fin2=new FileInputStream(System.getProperty("CONF_DIR") + File.separator +"date_fls.txt");
								final ObjectInputStream in2=new ObjectInputStream(fin2);
								flsStartDateTime=in2.toString();
								main.log.info("date_fls.txt file  exists");
								main.log.info("Fls is starting with Date and Time:"+flsStartDateTime );
								in2.close();
							}else{
								flsStartDateTime=getCurrentTime();
								//main.log.info("date_fls.txt file doesn't exist");
								main.log.info("Fls is staring with current date and time:"+flsStartDateTime);
							}
					 }
				} catch (Exception e) {
					main.log.warning(e.getMessage());
					fls_admin_flag=true;
					main.log.info("fls_admin_flag is :"+fls_admin_flag);
					
					pmFlag = true;
					topologyFlag = true;
				}


				// time for querying topology
				Timer topologyTimer = new Timer();
				final int topologyQueryInterval = topologyQueryIntervalStatic * 60 * 1000;
				// final int topologyStartDelay =
				// topologyQueryStartDelayStatic*60*1000;
				final int topologyStartDelay = (topologyQueryIntervalStatic * 60 * 1000)
						- (int) (System.currentTimeMillis() % (topologyQueryIntervalStatic * 60 * 1000));

				topologyTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							if (main.enableFlsService()) {
								FlsQueryTopology flsQueryTopology = FlsQueryTopology.getInstance(main.cache, main.log,
										main.topologyQueue);
								main.topologyQueue = flsQueryTopology.topologyQueue;
								main.topologyTimerRun();
//								RestClientInstance restClientInstance = RestClientInstance.getInstance();
//								restClientInstance.closeSession();
							} else {
								main.log.finest("Fls_conf file is empty");
							}
						} catch (Exception e) {
							main.log.warning("Exception occured while querying for Topology!!  ");
						}
					}
				}, topologyStartDelay, topologyQueryInterval);

				Timer pmTimer = new Timer();
				final int pmQueryInterval = pmQueryIntervalStatic * 60 * 1000;
				final int pmStartDelay = (pmQueryIntervalStatic * 60 * 1000)
						- (int) (System.currentTimeMillis() % (pmQueryIntervalStatic * 60 * 1000));
				
				main.log.info("Starting Pm Timer...");

				pmTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							if (main.enableFlsService()) {
								FlsQueryPm flsQueryPM = FlsQueryPm.getInstance(main.cache, main.log, main.pmQueue);
								main.pmQueue = flsQueryPM.pmQueue;
								main.pmTimerRun();
								RestClientInstance restClientInstance = RestClientInstance.getInstance();
								restClientInstance.closeSession();
							} else {
								main.log.finest("Fls_conf file is empty");
							}
						} catch (Exception e) {
							main.log.warning("Exception occured while querying for PM!!" + e.getMessage());
						}
					}

				}, pmStartDelay, pmQueryInterval);

				// to run blocking queue for creating symbolic link for pm files
				try{
				threadPoolExecutor = new ThreadPoolExecutor(threadPoolCorePoolSizeStatic, threadPoolMaxPoolSizeStatic,
						100001, TimeUnit.MILLISECONDS, main.pmQueue);
				threadPoolExecutor.prestartAllCoreThreads();
				}
				catch(Exception e){
					main.log.warning("Exception at threadPoolExecutor:"+e.getMessage());
				}

			} else {
				main.log.info("This server doesn't have fls enabled");
			}
		} catch (Exception e) {
			main.log.warning("Failed to initialize FLS : " + e.getMessage());
		}
	}

	// method to check fls enabled
	boolean enableFlsService() {
		boolean enableFLService = false;
		try {
			Process pFLS = Runtime.getRuntime().exec("cat /eniq/installation/config/fls_conf");
			BufferedReader inputFLS = new BufferedReader(new InputStreamReader(pFLS.getInputStream()));

			String line;
			String[] fls_elabled_enm = null;
			while ((line = inputFLS.readLine()) != null) {
				fls_elabled_enm = line.split("\\s+");
			}

			if (fls_elabled_enm.length > 0) {
				enableFLService = true;

			} else {
				log.info("FLS mode is not configured in the server");
			}
		} catch (Exception e) {
			log.warning(
					"Exception occured while reading the file :: /eniq/installation/config/fls_conf." + e.getMessage());
		}
		return enableFLService;
	}

	// to stop main process from FlsStopMain
	public void endProcess() {
		main.log.info("Shutting Down FLS  process");
		System.exit(0);
	}

	// To get ENIQ name of server
	public static void getEniqName() throws Exception {

		if (eniqName == null) {
			Process pFLS = Runtime.getRuntime().exec(
					"cat " + StaticProperties.getProperty("SERVICE_NAMES", "/eniq/installation/config/service_names"));
			BufferedReader inputFLS = new BufferedReader(new InputStreamReader(pFLS.getInputStream()));

			String line = null;
			while ((line = inputFLS.readLine()) != null) {

				if (line.matches(".*::.*::engine")) {
					Pattern pattern = Pattern.compile(".*::(.*)::engine");
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						main.log.fine("Server Engine name : " + matcher.group(1));
						eniqName = matcher.group(1);
					} else {
						main.log.warning("Not matching the Engine matcher patter .*::(.*)::engine");
					}
				}
			}
		}
	}

	// To collect Nodes assigned to each Eniq-S
	public ArrayList<String> getNodeList() throws SQLException {
		RockFactory dwhrep = null;
		String selectString = null;
		final RockResultSet rockResultSet;
		try {
			dwhrep = DatabaseConnections.getDwhRepConnection();
			selectString = "select distinct NETYPE from ENIQS_Node_Assignment where ENIQ_IDENTIFIER='" + Main.eniqName
					+ "' and NETYPE != ''";
			rockResultSet = dwhrep.setSelectSQL(selectString);
			ResultSet rs = rockResultSet.getResultSet();
			ArrayList<String> returnNodeList = new ArrayList<String>();
			log.finest("Querying for NodeType ");
			if (rockResultSet.getResultSet().isBeforeFirst()) {
				while (rs.next()) {
					returnNodeList.add(rs.getString(1));
					log.info("Netype value : " + rs.getString(1));
				}
			} else {
				log.info(
						"ENIQS_Node_Assignment table is empty or NETYPE column is empty. Will not be querying for PM Files!!");
			}
			return returnNodeList;

		} catch (Exception e) {
			log.warning("Exception in getNodeList method" + e.getMessage());
			return null;
		}
		finally{
			dwhrep.getConnection().close();
		}
	}

	// To check whether Bulk-CM Techpack installed or not
	public static boolean isBulkCmInstalled() {
		RockFactory dwhrep = null;
		String selectString = null;
		final RockResultSet rockResultSet;
		try {
			dwhrep = DatabaseConnections.getDwhRepConnection();
			selectString = "select TECHPACK_NAME,STATUS from TPActivation";
			rockResultSet = dwhrep.setSelectSQL(selectString);
			ResultSet rs = rockResultSet.getResultSet();

			if (rs.next()) {
				String TECHPACK_NAME = rs.getString(1);
				if (Pattern.matches(TECHPACK_NAME, "DC_E_BULK_CM")) {
					String activeString = rs.getString(2);
					if (activeString.equals("ACTIVE")) {
						return true;
					}
				}

			}

		} catch (Exception e) {
			main.log.warning("Exception while checking for BulkCM : " + e.getMessage());
		}

		return false;
	}

	// method to called by TopologyTimer for every interval for pm subscription
	public void topologyTimerRun() {
		try{
		FlsQueryTopology flsQueryTopology = FlsQueryTopology.getInstance(main.cache, main.log, main.topologyQueue);

		Long preId, postId;
		if (!topologyFlag) {
			if (!main.tokenMap.isEmpty() && main.tokenMap.containsKey("TOPOLOGY")) {
				// topology fls request when id is available in map

				postId=preId = main.tokenMap.get("TOPOLOGY");
				main.log.info("Id value of Topology before querying: " + preId);
				try{
				postId = flsQueryTopology.queryTopology("TOPOLOGY", preId, null, 1);
				}
				catch(Exception e){
				main.log.warning("Exception at while calling FlsQueryTopology "+e.getMessage());	
				}

				// storing last id in map
				main.log.info("Id value of topology after querying: " + postId);
				if (postId != preId) {
					main.tokenMap.put("TOPOLOGY", postId);
				} else {
					main.log.info("Topology request has not received any new file entries!!");
				}
			} else {
				
				// topology fls request when id is available in map
				postId=preId = (long) -1;
				
				main.log.info("Topology request for first time");
				try{
				postId = flsQueryTopology.queryTopology("TOPOLOGY", preId, flsStartDateTime, 2);
				}
				catch(Exception e){
					main.log.warning("Exception at while calling FlsQueryTopology "+e.getMessage());	
				}
				// storing last id in map
				main.log.info("id value of topology after querying: " + postId);
				if (postId != preId) {
					main.tokenMap.put("TOPOLOGY", postId);
				} else {
					main.log.info("Topology request has not received any new file entries!!");
				}
			}

		} else {
			topologyFlag=false;
			main.log.info("topology request by User inputed Date and Time");
			postId=preId=(long) -1;
			try{
			postId=flsQueryTopology.queryTopology("TOPOLOGY",preId,flsStartDateTimeAdminUi,2);
			}
			catch(Exception e){
				main.log.warning("Exception at while calling FlsQueryTopology: "+e.getMessage());	
			}
			//storing last id in map
			main.log.info("id value of topology after querying with current Date and Time: "+postId);
			if(postId != preId ){
				main.tokenMap.put("TOPOLOGY",postId);
			}
			else{
				main.log.info("Topology request has not received any new file entries!!");
			}
		}
		// to create symbolic link for topology files
		Thread t = new Thread(new TopologyQueueHandler(main.topologyQueue, main.log), "topologyhandler");
		t.start();
		}
		catch(Exception e){
			main.log.warning("Exception at topology Quering: "+e);
		}
	}

	// method to called by pmTimer for every interval for pm subscription
	public void pmTimerRun() {

		FlsQueryPm flsQueryPM = FlsQueryPm.getInstance(main.cache, main.log, main.pmQueue);
		long preId, postId;
		PersistedToken persistedToken = new PersistedToken(main.tokenMap);
		// to storelist node types for subscription
		ArrayList<String> nodeTypeList = new ArrayList<String>();
		main.pmQueue = flsQueryPM.pmQueue;
		try {
			nodeTypeList = main.getNodeList();

		} catch (SQLException e) {
			main.log.warning("Exception in retrieving nodetype list " + e.getMessage());
		}

		// to do subscription for every nodetype
		try {
			if (nodeTypeList != null) {
				for (String nodeType : nodeTypeList) {
					try {
						if (!pmFlag) {
							if (!main.tokenMap.isEmpty() && main.tokenMap.containsKey(nodeType)) {
								// pm fls request when id is available in map
								postId =preId = main.tokenMap.get(nodeType).intValue();
								main.log.info("PM request for node: " + nodeType + " and Id value : " + preId
										+ " before sending get request");
								try{
								postId = flsQueryPM.queryPM(nodeType, preId, null, 1);
								}
								catch(Exception e)
								{
									main.log.warning("Exception while calling FlsQueryPm for NodeType:  "+nodeType+" Exception is "+e.getMessage());
								}
								main.pmQueue = flsQueryPM.pmQueue;
								// storing last id in map
								if (postId != preId) {
									main.log.info("PM request for node: " + nodeType + " and Id value : " + postId
											+ " id  after successive get request");
									main.tokenMap.put(nodeType, postId);
								} else {
									main.log.info("PM request has not received any new file entries for nodeType: "
											+ nodeType);
								}
							}

							else{
								//pm fls request when id is not available in map
									main.log.info("first time pm request for node: "+nodeType);
									postId=preId=-1;
									try{
										postId=flsQueryPM.queryPM(nodeType,preId,flsStartDateTime,2);	
										}
									catch(Exception e)
									{
										main.log.warning("Exception while calling FlsQueryPm for NodeType:  "+nodeType+" Exception is "+e.getMessage());
									}
									main.pmQueue=flsQueryPM.pmQueue;
									//storing last id in map
									
									main.log.info("pm request for node: "+nodeType+" id  after first time pm get request:"+postId);
									if(postId != preId ){
										main.tokenMap.put(nodeType,postId);
									}
								}
						} else{
							
							main.log.info("pm request for node: "+nodeType+" id  after user inputed date & time");
							
							postId=preId=(long)-1;
							try{
								postId=flsQueryPM.queryPM(nodeType,preId,flsStartDateTimeAdminUi,2);	
							}
							catch(Exception e)
							{
								main.log.warning("Exception while calling FlsQueryPm for NodeType:  "+nodeType+" Exception is "+e.getMessage());
							}
							main.pmQueue=flsQueryPM.pmQueue;
							//storing last id in map
							
							main.log.info("pm request for node: "+nodeType+" id  after user inputted date & time :"+postId);
							if(postId != preId ){
								main.tokenMap.put(nodeType,postId);
							}
						}
					} catch (Exception e) {
						main.log.warning("Exception occuerd while querying for PM for  " + nodeType + "!" + e.getMessage());
					}
				}
			} else {
				main.log.info("Either Node assignment table is empty or REPDB is offline");
			}
			pmFlag=false;
		} catch (Exception e) {
			main.log.warning("Exception occuerd in PM" + e.getMessage());
		}
		try {
			final FileOutputStream fout = new FileOutputStream(
					System.getProperty("CONF_DIR") + File.separator + "Persisted.ser");
			final ObjectOutputStream out = new ObjectOutputStream(fout);

			persistedToken.setTokenMap(main.tokenMap);
			main.log.info("Persisting token map value " + persistedToken.getTokenMap());

			out.writeObject(persistedToken);
			out.flush();
			out.close();
			fout.close();
		} catch (Exception e) {
			main.log.warning("Exception at persisting tokenMap " + e.getMessage());
		}

	}
	public void pmCallbyAdminUi(String flsUserStartDateTime){
		try{
			fls_admin_flag=false;
			main.log.info("fls_admin_flag is :"+fls_admin_flag);
		FlsQueryPm flsQueryPM=FlsQueryPm.getInstance(main.cache,main.log,main.pmQueue);
		ArrayList<String> nodeTypeList= new ArrayList<String>();
		//main.pmQueue=flsQueryPM.pmQueue;
		try {
			nodeTypeList=main.getNodeList();
			
		} catch (SQLException e) {
			main.log.warning("exception in node type list "+e);
		}
		Long id;
		Map<String,Long> map=new HashMap<String,Long>();
		if(nodeTypeList !=null){
			for(String nodeType:nodeTypeList){
				id=flsQueryPM.queryPM(nodeType, -1, flsUserStartDateTime, 3);
				main.pmQueue=flsQueryPM.pmQueue;
				if(id != -1){
					map.put(nodeType, id);
				}
			}
		}
		if(map.size()>0){
			pmCallbyAdminUiRun(map);
		}
		}
		catch(Exception e){
			main.log.warning("Exception at pmCallByAdminUi "+e.getMessage());
		}
	}

	private void pmCallbyAdminUiRun(final Map<String, Long> map) {
		try{
		
		final Timer t=new Timer();
		t.schedule(new TimerTask(){
			@Override
			public void run() {
				LinkedList<String> list=new LinkedList<String>();
				if(map.size()>0){
					
					for(Map.Entry<String, Long> entry : map.entrySet()){
						FlsQueryPm flsQueryPM=FlsQueryPm.getInstance(main.cache,main.log,main.pmQueue);
						Long postId=flsQueryPM.queryPM(entry.getKey(), entry.getValue(), null, 4);
						if(entry.getValue()!= postId){
							entry.setValue(postId);
						}
						else{
							list.add(entry.getKey());
						}
					}
					
				}
				else{
					t.cancel();
				}
			for(ListIterator<String> i=list.listIterator();i.hasNext();)	{
				map.remove(i.next());
			}
			}
			}, 0, 3*60*1000);
		}
		catch(Exception e){
			main.log.warning("Exception at pmCallbyAdminui\t"+e.getMessage());
		}
		
	}
private static String getCurrentTime() {
	try{
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat ft;

		if(Main.timeFormat !=null){
			ft =new SimpleDateFormat(Main.timeFormat);
		}
		else{
			ft =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}
     
		String dateTime=ft.format(date).toString();
		
		return dateTime;
	}
	catch(Exception e){
		main.log.warning("Exception at getCurrentTime method"+e.getMessage());
	}
	return null;
	}

public Logger getLog() {
	return log;
}

public void setLog(Logger log) {
	this.log = log;
	}
}
