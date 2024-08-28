package com.distocraft.dc5000.etl.gui.enminterworking;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.distocraft.dc5000.etl.gui.common.EtlguiServlet;

import ssc.rockfactory.RockFactory;

/**
 * Copyright &copy; ERICSSON. All rights reserved.<br>
 * Servlet with Unassigned nodes implementation. <br>
 *
 **/

public class UnAssignedNodes extends EtlguiServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(this.getClass());
	private Template outty = null;
	
	@SuppressWarnings("deprecation")
	@Override
	public Template doHandleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx)
			throws Exception {
		Connection connDwh = ((RockFactory) ctx.get("rockDwhRep")).getConnection();
		ArrayList<ArrayList<String>> un_nodes = new ArrayList<ArrayList<String>>();
	   
	    
	    // put result into the context, which is read by the velocity engine
	    // and rendered to page with template called
	    
	    un_nodes = getUnassignedNodes(connDwh);
	    ctx.put("nodeSnap",un_nodes);

	    try 
		{
			outty = getTemplate("unassigned_nodes.vm");
		}
		catch (ResourceNotFoundException e) 
		{
			log.debug("ResourceNotFoundException (getTemplate):", e);
		}
		catch (ParseErrorException e)
		{
			log.debug("ParseErrorException (getTemplate): " + e);
		}
		catch (Exception e) 
		{
			log.debug("Exception (getTemplate): " + e);
		}

	    finally{
	    	connDwh.close();
		}
		return outty;
		
	}
	
		private ArrayList<ArrayList<String>> getUnassignedNodes(Connection connDwh) {
		ArrayList<ArrayList<String>> nTable = new ArrayList<ArrayList<String>>();
		Statement stmt = null;
		ResultSet rSet = null;
		try
		{
			stmt = connDwh.createStatement();
			rSet = stmt.executeQuery("Select ENIQ_IDENTIFIER, FDN, NETYPE from ENIQS_Node_Assignment where ENIQ_IDENTIFIER=''");
			while(rSet.next())
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add(rSet.getString("ENIQ_IDENTIFIER"));
				list.add(rSet.getString("FDN"));
				list.add(rSet.getString("NETYPE"));
				nTable.add(list);
			}
		}
		catch (Exception e)
		{
			log.error("Exception: ", e);
		}
		finally {
		      // finally clean up
		      try {
		        if (rSet != null) {
		        	rSet.close();
		        }
		        if (stmt != null) {
		          stmt.close();
		        }
		      } catch (SQLException e) {
		        log.error("SQLException: ", e);
		      }
		    }
		return nTable;
	}

	}


