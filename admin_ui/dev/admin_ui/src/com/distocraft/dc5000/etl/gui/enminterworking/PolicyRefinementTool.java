package com.distocraft.dc5000.etl.gui.enminterworking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.sql.Connection;

//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.distocraft.dc5000.common.RmiUrlFactory;
import com.distocraft.dc5000.etl.gui.common.EtlguiServlet;
import com.ericsson.eniq.enminterworking.EnmInterCommonUtils;
import com.ericsson.eniq.enminterworking.IEnmInterworkingRMI;

import ssc.rockfactory.RockFactory;

public class PolicyRefinementTool extends EtlguiServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(this.getClass());

	private String Identifier;

	private String technology;

	private String regexString;

	Boolean validated = false;

	Boolean inserted = false;

	List<String> allRoles = new ArrayList<String>();

	List<String> allTechnologies = new ArrayList<String>();

	ArrayList<ArrayList<String>> policyTable = new ArrayList<ArrayList<String>>();

	private FileInputStream fstream;

	private BufferedReader br;

	private final String propertiesFile = System.getProperty("CONF_DIR", "/eniq/sw/conf") + "/NodeTechnologyMapping.properties";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.distocraft.dc5000.etl.gui.common.EtlguiServlet#doHandleRequest(javax.
	 * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * org.apache.velocity.context.Context)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Template doHandleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx)
			throws Exception {
		
		Template outty = null;
		Connection dwhrep = ((RockFactory) ctx.get("rockDwhRep")).getConnection();
		HttpSession session = request.getSession(false);
		
		Identifier = request.getParameter("identifier");
		technology = request.getParameter("technology");
		log.debug("Technology is " + technology);
		regexString = request.getParameter("inputstring");

		if (ctx.containsKey("prt_saved")) {
			ctx.put("message", "Policy Updated Successfully");
			Identifier = null;
			technology = null;
		}

		if (Identifier == null && technology == null) {
			
			Identifier = "-";
			technology = "-";
			
		} else {
			PolicyAndCriteria pc = new PolicyAndCriteria();
			if (!Identifier.equals("-")) {
				if(technology.equals("-")){
					technology = "*";
				}
				if (regexString.isEmpty()) {
					regexString = "*";
				}
					if(!pc.isCombinationExists(technology, regexString, Identifier, dwhrep)){
				
					//validate the given regular expression
						validated = pc.validateRegex(technology, regexString, Identifier, dwhrep);
					
						if (validated) {
						
						inserted = pc.insertPolicy(technology, regexString, Identifier, dwhrep);
						if ( inserted ){
							IEnmInterworkingRMI multiEs = (IEnmInterworkingRMI) Naming
									.lookup(RmiUrlFactory.getInstance().getMultiESRmiUrl(EnmInterCommonUtils.getEngineIP()));
							multiEs.refreshNodeAssignmentCache();
						}
						} else {
						
						log.debug("Invalid Naming Convention");
						ctx.put("invalidNaming", true);
					}
					}
					else {
						ctx.put("invalidcombination", true);
					}		
				}
			 else {
				
				ctx.put("invalidserver", true);
			}
		}

		allRoles = EnmInterUtils.getAllServers(dwhrep);
		
		policyTable = new PolicyAndCriteria().getAllPolicies(dwhrep);
		
		allTechnologies = getTechnologies();
		
		ctx.put("policyTable", policyTable);
		ctx.put("identifier", Identifier);
		ctx.put("technology", technology);
		ctx.put("allTechnologies", allTechnologies);
		ctx.put("allRoles", allRoles);
		ctx.put("inputstring", regexString);

		try {
			
			outty = getTemplate("policy_refinement_tool.vm");
			
		} catch (ResourceNotFoundException e) {
			
			log.debug("ResourceNotFoundException (getTemplate):", e);
			
		} catch (ParseErrorException e) {
			
			log.debug("ParseErrorException (getTemplate): " + e);
			
		} catch (Exception e) {
			
			log.debug("Exception (getTemplate): " + e);
			
		}
		finally{
			dwhrep.close();
		}

		return outty;
	}

	// fetches all the technologies from the property file
	protected List<String> getTechnologies() throws Exception {

		List<String> allTech = new ArrayList<String>();
		allTech.add("*");

		final File propFile = new File(propertiesFile);
		if (!propFile.exists()) {

			log.warn("Technology file doesnt exist");
			throw new Exception("Failed to load " + propertiesFile);

		} else {

			fstream = new FileInputStream(propFile);
			br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = "";
			while ((strLine = br.readLine()) != null) {

				String[] line = strLine.split("-");
				allTech.add(line[0]);
			}
		}
		return allTech;
	}
}
