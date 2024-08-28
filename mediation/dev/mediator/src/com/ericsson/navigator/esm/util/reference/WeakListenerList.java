package com.ericsson.navigator.esm.util.reference;

import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class WeakListenerList<EL extends EventListener> implements Iterable<EL>{

	private final List<WeakReference<EL>> m_Listeners;

	private class WeakIterator implements Iterator<EL> {

		private final Iterator<WeakReference<EL>> m_Iterator;

		private EL m_Next = null;

		public WeakIterator() {
			m_Iterator = m_Listeners.iterator();
		}

		public boolean hasNext() {
			while (m_Iterator.hasNext()) {
				m_Next = m_Iterator.next().get();
				if (m_Next == null) {
					m_Iterator.remove();
				} else {
					return true;
				}
			}
			return false;
		}

		public EL next() {
			return m_Next;
		}

		public void remove() {
			m_Iterator.remove();
		}

	}

	private static final long serialVersionUID = 1L;

	public WeakListenerList() {
		m_Listeners = new Vector<WeakReference<EL>>();
	}

	public void addListener(final EL l) {
		m_Listeners.add(new WeakReference<EL>(l));
	}

	public Iterator<EL> iterator() {
		return new WeakIterator();
	}

	public void removeListener(final EventListener l) {
		final Iterator<EL> i = iterator();
		while (i.hasNext()) {
			if (i.next().equals(l)) {
				i.remove();
			}
		}
	}

	public boolean contains(final EL l) {
		for(final WeakReference<EL> w : m_Listeners){
			if(w.get() == l){
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return m_Listeners.isEmpty();
	}
}
 