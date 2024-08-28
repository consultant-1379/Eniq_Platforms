//**********************************************************************
//
//COPYRIGHT Ericsson AB, Sweden, 2003.
//All rights reserved.
//
//The Copyright to the computer program(s) herein is the property of
//Ericsson AB, Sweden.
//The program(s) may be used and/or copied only with the written
//permission from Ericsson AB or in accordance with the terms
//and conditions stipulated in the agreement/contract under
//which the program(s) have been supplied.
//
//**********************************************************************

// Created on 2003-dec-03
package com.ericsson.navigator.esm.util.rate;

import java.util.Arrays;

/**
 * Handles sample values for one type of measure. Each measures keeps an array
 * with samples for the sample period.
 * 
 * @author epkhlim
 */
public final class Measures {
	// Most items are final declared to get static binding, which is better for
	// performance. Default Java uses dynamic binding.

	/**
	 * Counter for all values ever added to the measure.
	 */
	private int counter = 0;

	/**
	 * The time when the last sample was recorded.
	 */
	private long lastSampleTime = 0;
	/**
	 * Peak value, updated every time a new sample is recorded.
	 */
	private int peak = 0;

	/**
	 * Array with samples for the sample period.
	 */
	private int[] samples = null;

	/**
	 * Initialize data holders.
	 * 
	 * @param length
	 *            how many seconds history to keep
	 */
	public Measures(final int length) {
		samples = new int[length]; // sample length
		Arrays.fill(samples, 0); // set all seconds to zero
	}

	/**
	 * Add a value to measures.
	 * 
	 * @param value
	 */
	public synchronized final void addValue(final long time, final int value) {

		clearOldSamples(time);

		samples[getSecond(time)] += value;

		// update counter
		counter += value;

		// calculate any peak value here
		// This could be time consuming, if so remove!
		final int currentSum = getSumOfAll();
		if (peak < currentSum) {
			peak = currentSum;
		}
	}

	/**
	 * Clear samples older then sample length.
	 * 
	 * <p>
	 * If a new second is entered then earlier data for that second is cleared.
	 * 
	 * <p>
	 * Also cleares entries after last sample was recorded up till current
	 * sample second. This is because nothing was recorded during this time but
	 * the entries may contain data from earlier recordings, that is recordings
	 * older then last minute.
	 * 
	 * @param time
	 *            current time in milliseconds
	 */
	private synchronized final void clearOldSamples(final long time) {
		if (time - lastSampleTime > samples.length * 1000) {
			// clear all old
			Arrays.fill(samples, 0);
			lastSampleTime = time;
		} else {
			final int lastSampleSecond = getSecond(lastSampleTime);
			final int currentSampleSecond = getSecond(time);
			if (lastSampleSecond != currentSampleSecond) {
				// Clear all entries after last sample up till current sample
				// second because no samples has been received for those
				// seconds.
				int secondAfterLastSample = lastSampleSecond + 1;

				if (secondAfterLastSample == samples.length) { // bound check
					secondAfterLastSample = 0;
				}

				// clear entries
				while (secondAfterLastSample <= currentSampleSecond) {
					samples[secondAfterLastSample++] = 0;
				}
				lastSampleTime = time;
			} else {
				// Do nothing.
				// New sample belongs to same second as last sample
				// then current seconds entry should not be cleared.
			}
		}
	}

	/**
	 * Returns counter. The counter is the sum of all values ever added to the
	 * measures.
	 */
	public synchronized final int getCounter() {
		return counter;
	}

	/**
	 * Get copy of the internal sample array. Could be used if other
	 * calculations are needed then provided by the class.
	 * 
	 * @return
	 */
	public synchronized final int[] getData() {
		final int[] copy = new int[samples.length];
		System.arraycopy(samples, 0, copy, 0, copy.length);
		return copy;
	}

	/**
	 * Get samples for last second.
	 */
	public synchronized final int getLastSecond(final long time) {
		// clear old data
		clearOldSamples(time);

		// get samples for previous second
		int lastSecond = getSecond(time) - 1;
		if (lastSecond < 0) { // bound check
			lastSecond = samples.length - 1;
		}

		// get previous seconds data
		return samples[lastSecond];
	}

	/**
	 * The peak sum since the beginning for all samples.
	 */
	public synchronized final int getPeakSumOfAll() {
		return peak;
	}

	/**
	 * Get current second in sample array
	 * 
	 * @param time
	 * @return
	 */
	private synchronized final int getSecond(final long time) {
		return (int) (time / 1000) % samples.length;
	}
	
	

	/**
	 * Get the sum of all samples.
	 */
	public synchronized final int getSumOfAll() {
		int sum = 0;
		for (int i = 0; i < samples.length; i++) {
			sum += samples[i];
		}
		return sum;
	}

	/**
	 * Get a string representation of all samples.
	 */
	@Override
	public synchronized final String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("last second: ");
		buf.append(this.getLastSecond(System.currentTimeMillis()));
		buf.append(", last ");
		buf.append(samples.length);
		buf.append(" seconds: ");
		buf.append(this.getSumOfAll());
		buf.append(", [");
		buf.append(samples.length);
		buf.append(" seconds peak since start: ");
		buf.append(this.getPeakSumOfAll());
		buf.append("]\tsamples:{");

		// print all data
		for (int i = 0; i < samples.length; i++) {
			buf.append(samples[i]);

			if (i != samples.length - 1) {
				buf.append(',');
			}
		}
		buf.append('}');
		return buf.toString();
	}
}
