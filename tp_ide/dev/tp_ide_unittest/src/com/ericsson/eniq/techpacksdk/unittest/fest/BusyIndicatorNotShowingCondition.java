package com.ericsson.eniq.techpacksdk.unittest.fest;

import java.awt.Component;

import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.timing.Condition;
import org.fest.swing.exception.ComponentLookupException;

import com.ericsson.eniq.techpacksdk.BusyIndicator;

/**
 * A custom condition class for checking if there are no visible Busy
 * Indicators. This is used when pausing until the busy indicator
 * disappears.
 * 
 * @author eheitur
 * 
 */
public class BusyIndicatorNotShowingCondition extends Condition {

	// Matcher for the Busy Indicator
	ComponentMatcher busyIndicatorMatcher = new ComponentMatcher() {
		public boolean matches(Component c) {
			if (!(c instanceof BusyIndicator)) {
				return false;
			} else {
				return c.isVisible();
			}
		}
	};

	public BusyIndicatorNotShowingCondition(String description) {
		super(description);
	}

	/**
	 * Returns true if there are no visible Busy Indicator(s).
	 */
	@Override
	public boolean test() {
		ComponentFinder finder = (ComponentFinder) BasicComponentFinder
				.finderWithCurrentAwtHierarchy();

		try {
			finder.find(busyIndicatorMatcher);
		} catch (ComponentLookupException lue) {
			// Visible BusyIndicator was not found.
			return true;
		}

		// Visible BusyIndicator was found.
		return false;
	}

}

