package com.ericsson.wifi.ewmnbi;

import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;
import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.MemoryRestrictedParser;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.StatsBatch;
import com.ericsson.wifi.ewmnbi.WIFITimer.PollerTask;
import com.ericsson.wifi.ewmnbi.WIFITimer.PollerTask.FileInformation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.common.ProcessedFiles;
import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionMemoryConsumption;
import com.distocraft.dc5000.etl.engine.main.EngineThread;
import com.distocraft.dc5000.etl.engine.priorityqueue.PriorityQueue;
import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.ParseSession;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.ParserFileFilter;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.StatsBatch;

public class WIFITimer {
	Timer timer;
	public static final String SESSIONTYPE = "ADAPTER";
	private final Properties conf;
	private final String techPack;
	private final String set_type;
	private final String set_name;
	private final String parserType;
	private final RockFactory rf;
	private final RockFactory reprock;
	private Main mainParserObject = null;
	private ParserDebugger debugger = null;
	private final List<File> localDirLockList = new ArrayList<File>();
	private ParseSession psession = null;
	private int fileCount = 0;
	private int checkType = 0;
	protected int memoryUsageFactor = 0;
	protected String regexpForWorkerLimit = "";
	private static final int MAXFILECOUNT = Integer.MAX_VALUE;
	private DataFormatCache dfCache = null;
	private String fileNameFilter = null;
	private final List<SourceFile> sourceFileList;
    public int NoOfFileSystem;
	public static int NoOfDir;
	public String preAppend = "0";
	private Logger log;
	public WIFITimer(int seconds, final Properties conf, final String techPack,final String set_type, final String set_name, final RockFactory rf,final RockFactory reprock) {

		this.conf = conf;
		this.techPack = techPack;
		this.set_type = set_type;
		this.set_name = set_name;
		this.rf = rf;
		this.reprock = reprock;
		this.parserType = "wifi";
		log = Logger.getLogger("etl." + techPack + "." + set_type + "." + set_name + ".parser");
		sourceFileList = new ArrayList<SourceFile>();
		timer = new Timer();
		timer.schedule(new PollerTask(), 1000, 25 * 1000);
		try {
			Thread.sleep(5*60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			timer.cancel();
			System.exit(0);
		}
	}

	class PollerTask extends TimerTask {
		SourceFile sf = null;
		private List<FileInformation> fileList = new ArrayList<FileInformation>();
		private SourceFile sourceFile;
		private int memoryConsumptionMB = 0;
		private String techPack;
		private String setType;
		private String setName;
		private int status = 0;
		private String workerName = "";
		final private List errorList = new ArrayList();
		private String filename;
		private PollerTask mainParserObject;
		private final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());

		@Override
		public void run() {
			try {
				SourceFile sf = null;
				this.parse();

				while ((sf = mainParserObject.nextSourceFile()) != null) {
					int i = parsetimestamp(sf);
				}
			} catch (Exception e) {
			}
		}

		public String resolveDirVariable(String directory) {

			if (directory == null) {
				return null;
			}

			if (directory.indexOf("${") >= 0) {
				final int sti = directory.indexOf("${");
				final int eni = directory.indexOf("}", sti);

				if (eni >= 0) {
					final String variable = directory.substring(sti + 2, eni);
					final String val = System.getProperty(variable);
					final String result = directory.substring(0, sti) + val+ directory.substring(eni + 1);
					directory = result;
				}
			}
			return directory;
		}

		protected File[] fileList(final File inDir) {
			if (inDir.isFile()) {
				return new File[0];
			}
			File[] list;
			int retry = 0;
			do {
				list = inDir.listFiles();
				retry++;
			} while (list == null && retry < 3);
			if (list == null) {
				return new File[0];
			} else {
				return list;
			}
		}

		private boolean isDirLocked(final File dir) {
			if (localDirLockList.contains(dir)) {

				final Share share = Share.instance();
				final List list = (List) share.get("lockedDirectoryList");
				
				if (list == null) {
					return false;
				} else {
					return list.contains(dir);
				}
			} else {
				return false;
			}

		}

		public FileInformation createFileInformation(final File file,
				final long fileSize, final int memoryConsumptionB) {
			return new FileInformation(file, fileSize, memoryConsumptionB);
		}

		public class FileInformation {
			public File file;
			public long fileSizeB;
			public int memoryConsumptionMB;
			
			public FileInformation(final File file, final long fileSize,final int memoryConsumptionB) {
				this.file = file;
				this.fileSizeB = fileSize;
				this.memoryConsumptionMB = (memoryConsumptionB == 0 ? 0: (int) Math.ceil(memoryConsumptionB / 1024 / 1024));
			}
		}

		private class FileInformationComparator implements Comparator {
			@Override
			public int compare(final Object o1, final Object o2) {
				if (o1 == o2) {
					return 0;
				}
				final FileInformation fi1 = (FileInformation) o1;
				final FileInformation fi2 = (FileInformation) o2;
				return ((Long) fi2.fileSizeB).compareTo((fi1.fileSizeB));
			}
		}

		private List<FileInformation> createFileList() {
			final List<FileInformation> resultList = new ArrayList<FileInformation>();
			File inDir = null;						
			File wificonfig = new File("/eniq/connectd/etc/wifi.env");			
			if (!wificonfig.exists()) {
				wificonfig = new File("/eniq/sw/conf/wifi.env");			
				}            
			String directory = null;
			try { 
				FileInputStream fr = new FileInputStream(wificonfig);
				BufferedReader br = new BufferedReader(new InputStreamReader(fr));
				String strLine;
				while ((strLine = br.readLine()) != null) {
					System.out.println(strLine);
					directory = resolveDirVariable(strLine);
                    inDir = new File(directory);
					int fileCountLimit = MAXFILECOUNT;
					try {
						fileCountLimit = Integer.parseInt("32765");
					} catch (final Exception e) {
						e.printStackTrace();
					}

					if (fileCountLimit <= 0 || fileCountLimit > MAXFILECOUNT) {
						fileCountLimit = MAXFILECOUNT;
					}
					final File[] f = fileList(inDir);
					fileNameFilter = "(.*.ewmstats)$";
					final ParserFileFilter pff = new ParserFileFilter(fileCountLimit, checkType, fileNameFilter);
					for (int i = 0; i < f.length; i++) {
						if (f[i].isDirectory()) {
							if (isDirLocked(f[i])) {
								this.unlockDirs();
							} else {
								final File[] ret = f[i].listFiles(pff);
								for (int fi = 0; fi < ret.length; fi++) {
									FileInformation fileInformation = null;
									fileInformation = new FileInformation(ret[fi], 0, 0);
									resultList.add(fileInformation);
								}
							}
						} else {
							if (pff.accept(f[i])) {
								FileInformation fileInformation = null;
								fileInformation = new FileInformation(f[i], 0,0);
								resultList.add(fileInformation);
							}
						}
					}
				}
				return resultList;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		private void unlockDirs() {

			try {
				final Iterator<File> iter = localDirLockList.iterator();
				while (iter.hasNext()) {
					final File dirLock = iter.next();
					final Share sh = Share.instance();
					final List list = (List) sh.get("lockedDirectoryList");
					if (list != null) {
						list.remove(dirLock);
					}
				}
				localDirLockList.clear();
			} catch (final Exception e) {
				new Exception("Error while unlocking directories ");
			}
		}

		public int getNextSFMemConsumptionMB() {
			if (fileList.size() > 0) {
				final FileInformation fi = fileList.get(0);
				return fi.memoryConsumptionMB;
			}
			return 0;
		}

		public void set(final PollerTask main, final String techPack,
				final String setType, final String setName,
				final String workerName) {

			this.mainParserObject = main;
			this.techPack = techPack;
			this.setType = setType;
			this.setName = setName;
			this.status = 1;
			this.workerName = workerName;
			this.memoryConsumptionMB = mainParserObject.getNextSFMemConsumptionMB();
		}

		private int fileListSize() {
			long size = 0;
			final Iterator<FileInformation> iterator = fileList.iterator();
			while (iterator.hasNext()) {
				final FileInformation f = iterator.next();
				size += f.file.length();
			}
			final int mbsize = (int) (size / 1024 / 1024);
			return mbsize;
		}

		private Map<String, TimerTask> createWorkersfordeletewififile()
				throws Exception {

			int workercount = 3;
			final Map<String, TimerTask> workerList = new HashMap<String, TimerTask>();
			for (int i = 0; i < workercount; i++) {
				final String workerName = "w" + i + System.currentTimeMillis();
				this.set(this, techPack, set_type, set_name, workerName);
				workerList.put(workerName, this);
			}
			return workerList;
		}

		public Map<String, Set> parse() throws Exception {

			checkType = Integer.parseInt(conf.getProperty("brokenLinkCheck","0"));
			dfCache = DataFormatCache.getCache();
			Map<String, TimerTask> workers = null;
			try {
				fileList = createFileList();
				if (fileList.size() <= 0) {
					System.out.println("No valid files found from IN directories ");
					this.unlockDirs();
					return new HashMap();
				}
				System.out.println("File list created. " + fileList.size()+ " files to be polled.");
				workers = createWorkersfordeletewififile();
				this.unlockDirs();
				return null;
			} catch (final Exception ex) {
				ex.printStackTrace();
				throw ex;
			} finally {
				this.unlockDirs();
			}
		}

		public boolean isBrokenLink(final File file, final int cType) {

			if (cType == 0) {
				if (file.exists()) {
					return false;
				} else {
					return true;
				}
			} else if (cType == 1) {
				if (file.exists()) {
					return false;
				} else {
					return true;
				}
			} else if (cType == 2) {
				if (file.isFile()) {
					return false;
				} else {
					return true;
				}
			} else if (cType == 3) {
				if (file.canRead()) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		public SourceFile nextSourceFile() throws Exception {

			while (true) {
				FileInformation fi = null;
				synchronized (this) {
					if (fileList.size() > 0) {
						fi = fileList.remove(0);
						fileCount++;
					} else {
						continue;
					}
				}
				SourceFile sf = null;
				if (!isBrokenLink(fi.file, checkType)) {
					sf = new SourceFile(fi.file, fi.memoryConsumptionMB, conf,rf, reprock, psession, debugger, conf.getProperty("useZip", "false"), log);
				} else {
					try {
						fi.file.delete();
					} catch (final Exception e) {
					}
					continue;
				}
				if (sf.fileSize() == 0) {
					continue;
				}
                return sf;
			}
		}

		public int parsetimestamp(SourceFile sf) {
			this.sourceFile = sf;

			this.filename = sf.getName();
			try {
				InputStream input = null;
				try {
                    input = sf.getFileInputStream();
					int i = 0, j = 0, k = 0;
					StatsBatch.Builder builder = StatsBatch.newBuilder();
					
					Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				    long currenttime = c.getTimeInMillis();
				    long offset = TimeZone.getDefault().getOffset(currenttime);
				    Date d1 = new Date(currenttime-offset);

					while (builder.mergeDelimitedFrom(input) == true) {
						StatsBatch statsBatch = builder.build();
						i++;
						Long time = statsBatch.getTimestamp();
						Date d2 = new Date(time);
						long diff = (d1.getTime() - d2.getTime()) / 1000;
						if (diff > 86400) {
							j++;
						} else {
							k++;
						}
					}
					if (i == j) {
						System.out.println("deleting old file" +"  " +this.filename);
						input.close();
						sf.delete();
						return 1;
					} else {
						return 0;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return 0;
          }
	}

	public static void main(String args[]) {
		try {
			if (args.length <= 0) {
				System.err.println("\"source\" must be defined as argument");
				return;
			}
			final Properties props = new com.distocraft.dc5000.common.Properties(args[0], new Hashtable());
			String confDir = System.getProperty("dc5000.config.directory");
			if (!confDir.endsWith(File.separator)) {
				confDir += File.separator;
			}
			StaticProperties.reload();
            System.out.println("WIFI Poller is about to start");
            WIFITimer wifitmr = new WIFITimer(5, props, "x", "x", "x",null, null);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}