/*
 * Created on Apr 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.distocraft.dc5000.etl.gui.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * Copyright &copy; Distocraft ltd. All rights reserved.<br>
 * Logout functionality is implemented in this class.
 * 
 * @author Antti Laurila
 */
public class Logout extends EtlguiServlet {
	
  private static final long serialVersionUID = 1L;
  
  private final Log log = LogFactory.getLog(this.getClass());	

	/* (non-Javadoc)
	 * @see com.distocraft.dc5000.etl.gui.common.EtlguiServlet#doHandleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.velocity.context.Context)
	 */
	public Template doHandleRequest(final HttpServletRequest request, final HttpServletResponse response, final Context ctx) {
		Template outty = null;
		try {
			request.getSession().invalidate();
			
			// remove user from the context - 
			// so that it wont be shown in the screen
			ctx.remove("theuser");
			
			outty =  getTemplate("logout.vm");
		}
		catch( ParseErrorException pee ){
			log.error("Parse error for template " + pee);
		}
		catch( ResourceNotFoundException rnfe ){
			log.error("Template not found " + rnfe);
		}
		catch( Exception e ){
			log.error("Error " + e);
		}
		return outty;
	}

}
