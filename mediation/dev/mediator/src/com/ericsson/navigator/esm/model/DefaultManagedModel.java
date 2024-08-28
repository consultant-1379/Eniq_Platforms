package com.ericsson.navigator.esm.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ericsson.navigator.esm.util.reference.WeakListenerList;

public class DefaultManagedModel<M extends ManagedDataList<?, ?>> implements ManagedModel<M> { 

	private static final long serialVersionUID = 1L;
	private final Map<String, List<M>> managedFDNList = new Hashtable<String, List<M>>();
	private final List<M> managedLists = new ArrayList<M>();
	private final Map<ManagedDataType, List<M>> managedTypeList = new Hashtable<ManagedDataType, List<M>>();
	private final WeakListenerList<ManagedModelListener<M>> managedListeners = new WeakListenerList<ManagedModelListener<M>>();

	public DefaultManagedModel(){
		for(ManagedDataType type : ManagedDataType.values()){
			managedTypeList.put(type, new Vector<M>());//NOPMD
		}
	}
	
	private void fireSystemAdded(final ManagedModelEvent<M> event) {
		synchronized(managedListeners){
			for (final ManagedModelListener<M> l : managedListeners) {
				if(l.getTypes().contains(event.getManaged().getType())){
					l.systemAdded(event);
				}
			}
		}
	}

	private void fireSystemRemoved(final ManagedModelEvent<M> event) {
		synchronized(managedListeners){
			for (final ManagedModelListener<M> l : managedListeners) {
				if(l.getTypes().contains(event.getManaged().getType())){
					l.systemRemoved(event);
				}
			}
		}
	}

	public void addManagedModelListener(final ManagedModelListener<M> l) {
		synchronized(managedListeners){
			managedListeners.addListener(l);
		}
	}

	public void removeManagedModelListener(final ManagedModelListener<M> l) {
		synchronized(managedListeners){
			managedListeners.removeListener(l);
		}
	}

	public void addManaged(final M managed) {
		synchronized(managedFDNList){
			List<M> lists = managedFDNList.get(managed.getFDN());
			if(lists == null){
				lists = new ArrayList<M>();
				managedFDNList.put(managed.getFDN(), lists);
			}
			lists.add(managed);
			managedTypeList.get(managed.getType()).add(managed);
			managedLists.add(managed);
		}
		fireSystemAdded(new ManagedModelEvent<M>(this, managed));
	}

	public void removeManaged(final M managed) {
		synchronized(managedFDNList){
			managedFDNList.remove(managed.getFDN());
			managedTypeList.get(managed.getType()).remove(managed);
			managedLists.remove(managed);
		}
		fireSystemRemoved(new ManagedModelEvent<M>(this, managed));
	}

	public List<M> getManagedDataLists(final ManagedDataType type) {
		return new Vector<M>(managedTypeList.get(type));
	}
	
	public List<M> getAllManagedDataLists() {
		return new Vector<M>(managedLists);
	}

	public M getManaged(final Long uniqueId, final ManagedDataType type) {
		synchronized (managedFDNList) {
			for(final M managed : getManagedDataLists(type)){
				if(managed.hasData(uniqueId)){
					return managed;
				}
			}
		}
		return null;
	}

	public List<M> getManaged(final String fdn) {
		return managedFDNList.get(fdn);
	}
}
