package com.distocraft.dc5000.etl.gui.commandline;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.commons.lang.StringEscapeUtils;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.common.CommandRunner;
import com.distocraft.dc5000.etl.gui.common.DwhMonitoring;
import com.distocraft.dc5000.etl.gui.common.EtlguiServlet;
import com.distocraft.dc5000.etl.gui.util.Helper;

/**
 * Copyright &copy; Distocraft ltd. All rights reserved.<br>
 * This servlet supports limited set of commandline commands.
 * 
 * @author Antti Laurila
 */
public class CommandLineServlet extends EtlguiServlet {

  private static final long serialVersionUID = 1L;

  private static final Map<String, String> commands = new TreeMap<String, String>();

  private static boolean initDone = false;

  private final Log log = LogFactory.getLog(this.getClass());

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.velocity.servlet.VelocityServlet#handleRequest(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse,
   *      org.apache.velocity.context.Context)
   */
  public Template doHandleRequest(final HttpServletRequest request, final HttpServletResponse response, final Context ctx) {
    Template outty = null;
    

    // get users input
    String runCommand = request.getParameter("command");
    String result = null;
    
    String pattern =  "^[a-zA-Z0-9_ ]*$";
    
    if(runCommand == null){
    	runCommand = "-";
    }
    if(runCommand.matches(pattern)){
    	runCommand = StringEscapeUtils.escapeHtml(runCommand);
    }else{
    	runCommand = null;
    }

    // see if we recon the command what user requested
    // if so run the command otherwise don't
    if (runCommand == null) {
      runCommand = "-";
    } else {

      final String cmdLine = (String) commands.get(runCommand);

      if (cmdLine == null) {
        runCommand = "-";
      } else if (cmdLine.equalsIgnoreCase("getInstalledTechPacks")) {

        final RockFactory rockDwhRep = (RockFactory) ctx.get("rockDwhRep");

        final List<List<String>> activeTps = DwhMonitoring.getActiveInstalledTechpacks(rockDwhRep.getConnection());
        final List<List<String>> inActiveTps = DwhMonitoring.getInActiveInstalledTechpacks(rockDwhRep.getConnection());          

        ctx.put("activeTps",activeTps);
        ctx.put("inactiveTps",inActiveTps);
        ctx.put("instTps", "getInstalledTechPacks");

      } else {
        log.debug("Running cmd: '" + cmdLine + "'");
        try {
          result = CommandRunner.runCmd(cmdLine, log);
        } catch (IOException e) {
          log.error("SQLException",e);
          result = "";
        }
      }
    }


    // put result into the context, which is read by the velocity engine
    // and rendered to page with template called
    ctx.put("commands", new Vector<String>(commands.keySet()));
    ctx.put("theCommand", runCommand);
    ctx.put("theResult", result);

    try {
      outty = getTemplate("cmdline.vm");
    } catch (Exception e) {
      throw new VelocityException(e);
    }
    
    /*
     * } catch (ParseErrorException pee) { log.error("Parse error for template " +
     * pee); } catch (ResourceNotFoundException rnfe) { log.error("Template not
     * found " + rnfe); } catch (Exception e) { log.error("Error " +
     * e.getMessage()); }
     */
    return outty;
  }

  /**
   * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
   */
  public synchronized void init(final ServletConfig config) throws ServletException {
    super.init(config);

    if (initDone) {
      return;
    }

    final String rtDir = System.getProperty("RT_DIR");

    // read the commandline txt file ... hidden to this JAR
    try {

      final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(config.getServletContext()
          .getRealPath(Helper.PATHSEPERATOR)
          + Helper.getEnvEntryString("conffiles") + Helper.PATHSEPERATOR + "commands.txt")));

      String line = null;

      while ((line = in.readLine()) != null) {
        final String[] cmdDef = line.split("=");

        if (cmdDef.length != 2) {
          continue;
        }

        // log.debug("Found command: '"+cmdDef[0]+"' = '"+cmdDef[1]+"'");
        final String commandValue = cmdDef[1].replaceAll("\\$\\{RT_DIR\\}",rtDir);
        commands.put(cmdDef[0], commandValue);
      }

      in.close();
    } catch (IOException e) {
      // what we can do except printout the problem
      log.debug("Error when reading commands from JAR file.");
      log.debug("Error was: " + e.getMessage());
      log.debug("IOException", e);
      
    }

    initDone = true;
  }

}