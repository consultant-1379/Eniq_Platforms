package com.ericsson.navigator.esm.util.log;

import com.ericsson.navigator.esm.model.ManagedData;

public interface Log<D extends ManagedData<?, D>> {

	void log(D data) throws LogException;
}
