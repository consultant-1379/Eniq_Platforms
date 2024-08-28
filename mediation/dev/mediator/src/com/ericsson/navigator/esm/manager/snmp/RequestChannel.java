package com.ericsson.navigator.esm.manager.snmp;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.adventnet.snmp.snmp2.SnmpPDU;

public class RequestChannel{
	private final List<AsynchronousSnmpRequest> m_Requests;
	private final DatagramChannel m_Channel;
	private int m_AllocateCount = 0;

	public RequestChannel() throws IOException {
		m_Requests = Collections.synchronizedList(new Vector<AsynchronousSnmpRequest>());
		m_Channel = DatagramChannel.open();
	}

	public DatagramChannel getChannel() {
		return m_Channel;
	}

	public void addRequest(final AsynchronousSnmpRequest request)
			throws IOException {
		m_Requests.add(request);
		request.executeRequest();
	}
	
	public AsynchronousSnmpRequest getRequest(final SnmpPDU pdu){
		synchronized(m_Requests){
			for(final AsynchronousSnmpRequest request : m_Requests){
				if(request.matchesResponse(pdu)){
					return request;
				}
			}
		}
		return null;
	}
	
	public void removeRequest(final AsynchronousSnmpRequest request){
		m_Requests.remove(request);
	}

	public List<AsynchronousSnmpRequest> getRequests() {
		return m_Requests;
	}
	
	public int getAllocateCount(){
		return m_AllocateCount;
	}

	public void allocate() {
		m_AllocateCount++;
	}
	
	public void free(){
		m_AllocateCount--;
	}
}
