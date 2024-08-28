package com.distocraft.dc5000.etl.gui.enminterworking;

//import java.io.PrintWriter;
import java.rmi.Naming;
import java.sql.Connection;
//import java.text.SimpleDateFormat;
//import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.distocraft.dc5000.common.RmiUrlFactory;
import com.distocraft.dc5000.etl.gui.common.CalSelect;
//import com.distocraft.dc5000.etl.gui.common.DbCalendar;
import com.distocraft.dc5000.etl.gui.common.EtlguiServlet;
import com.ericsson.eniq.enminterworking.IEnmInterworkingRMI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ssc.rockfactory.RockFactory;

public class FlsMonitoring extends EtlguiServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(this.getClass());
	@SuppressWarnings("deprecation")
	@Override
	public Template doHandleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx)
			throws Exception {
		
		Template outty = null;
		
		String year_1 = request.getParameter("year_1");
	    String month_1 = request.getParameter("month_1");
	    String day_1 = request.getParameter("day_1");
	    String hour_1 = request.getParameter("hour_1");
	    String min_1 = request.getParameter("min_1");
	    String sec_1 = request.getParameter("sec_1");
	    String date_1=" ";
	    Connection conn=null;
	    
		//final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		 //This sends a vector of valid years from DIM_DATE Table.
	    //This is used by cal_select_1.vm
		try{
	    conn = ((RockFactory) ctx.get("rockDwh")).getConnection();
	    CalSelect calSelect = new CalSelect(conn);
	    ctx.put("validYearRange", calSelect.getYearRange());
	    ctx.put("alert_flag", true);
	    
	  //  DbCalendar calendar = new DbCalendar();
	  

	    if(year_1==null){
	    	year_1="-";
	    }
	    if(month_1==null){
	    	month_1="-";
	    }
	    if(day_1==null){
	    	day_1="-";
	    }
	    if(hour_1==null){
	    	hour_1="-";
	    }
	    if(min_1==null){
	    	min_1="-";
	    }
	    if(sec_1==null){
	    	sec_1="-";
	    }
	    
	    
	    if(!(year_1 == "-" ||month_1== "-" || day_1== "-" || hour_1== "-" ||min_1 == "-" ||sec_1 == "-" )){
	    	date_1=year_1+"-"+month_1+"-"+day_1+"T"+hour_1+":"+min_1+":"+sec_1;
	    	
	    	IEnmInterworkingRMI multiEs =  (IEnmInterworkingRMI) Naming.lookup(RmiUrlFactory.getInstance().getMultiESRmiUrl(EnmInterUtils.getEngineIP()));
			multiEs.adminuiFlsQuery(date_1);
			ctx.put("resultpage", false);
			ctx.put("alert_flag", false);
	    }
	    else{
	    	 ctx.put("resultpage", true);
	    }
	    ctx.put("year_1", year_1);
	    ctx.put("month_1", month_1);
	    ctx.put("day_1", day_1);
	    ctx.put("hour_1", hour_1);
	    ctx.put("min_1", min_1);
	    ctx.put("sec_1", sec_1);
	    ctx.put("date_1", date_1);}
		catch(Exception e){
			
		}
		finally{
			conn.close();
		}
		
		try 
		{
			outty = getTemplate("fls_monitoring.vm");
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
			//connDwh.close();
		}
		return outty;
	}

}
