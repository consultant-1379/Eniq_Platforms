package com.ericsson.navigator.esm.agent.pm;

import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.util.component.Component;

public interface PmAgent extends Component {
	void process(CounterSet counterSet);
}
