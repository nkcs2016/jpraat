/*
 * Copyright (C) 2012-2018 Gregory Hedlund
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.hedlund.jpraat.binding.fon;

import java.util.concurrent.atomic.AtomicReference;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import ca.hedlund.jpraat.annotations.Declared;
import ca.hedlund.jpraat.binding.Praat;
import ca.hedlund.jpraat.binding.jna.Str32;
import ca.hedlund.jpraat.binding.stat.Table;
import ca.hedlund.jpraat.binding.sys.Interpreter;
import ca.hedlund.jpraat.exceptions.PraatException;

public class Formant extends Sampled {
	
	public Formant() {
		super();
	}
	
	public Formant(Pointer p) {
		super(p);
	}
	
	/**
		Function:
			return a new instance of Formant, or NULL if out of memory.
		Preconditions:
			nt >= 1;
			dt > 0.0;
			maxnFormants >= 1;
		Postconditions:
			my xmin = tmin;
			my xmax = tmax;
			my nx = nt;
			my dx = dt;
			my x1 = t1;
			my maximumNumberOfPairs == maxnFormants;
			my frames [1..nt]. intensity == 0.0;
			my frames [1..nt]. numberOfPairs == 0;
			my frames [1..nt]. formants [1..maxnFormants] = 0.0;
			my frames [1..nt]. bandwidths [1..maxnFormants] = 0.0;
	 */
	public static Formant create (double tmin, double tmax, long nt, double dt, double t1, int maxnFormants)
		throws PraatException {
		Formant retVal = null;
		try {
			Praat.wrapperLock.lock();
			retVal = Praat.INSTANCE.Formant_create_wrapped (tmin, tmax, new NativeLong(nt), dt, t1, maxnFormants);
			Praat.checkAndClearLastError();
		} catch (PraatException e) {
			throw e;
		} finally {
			Praat.wrapperLock.unlock();
		}
		return retVal;
	}
	
	public long getMinNumFormants () {
		return Praat.INSTANCE.Formant_getMinNumFormants(this).longValue();
	}
	
	public long getMaxNumFormants () {
		return Praat.INSTANCE.Formant_getMaxNumFormants(this).longValue();
	}

	public double getValueAtTime (int iformant, double time, int bark) {
		return Praat.INSTANCE.Formant_getValueAtTime(this, iformant, time, bark);
	}
	
	public double getBandwidthAtTime (int iformant, double time, int bark) {
		return Praat.INSTANCE.Formant_getBandwidthAtTime(this, iformant, time, bark);
	}

	public void getExtrema (int iformant, double tmin, double tmax, 
			AtomicReference<Double> fmin, AtomicReference<Double> fmax) {
		final Pointer minPtr = new Memory(Native.getNativeSize(Double.TYPE));
		final Pointer maxPtr = new Memory(Native.getNativeSize(Double.TYPE));
		
		Praat.INSTANCE.Formant_getExtrema(this, iformant, tmin, tmax, minPtr, maxPtr);
		
		fmin.set(minPtr.getDouble(0));
		fmax.set(maxPtr.getDouble(0));
	}
	
	public void getMinimumAndTime (int iformant, double tmin, double tmax, int bark, int interpolate,
			AtomicReference<Double> return_minimum, AtomicReference<Double> return_timeOfMinimum) {
		final Pointer minPtr = new Memory(Native.getNativeSize(Double.TYPE));
		final Pointer timePtr = new Memory(Native.getNativeSize(Double.TYPE));
		
		Praat.INSTANCE.Formant_getMinimumAndTime(this, iformant, tmin, tmax, bark, interpolate,
				minPtr, timePtr);
	
		return_minimum.set(minPtr.getDouble(0));
		return_timeOfMinimum.set(timePtr.getDouble(0));
	}
	
	public void getMaximumAndTime (int iformant, double tmin, double tmax, int bark, int interpolate,
			AtomicReference<Double> return_maximum, AtomicReference<Double> return_timeOfMaximum) {
		final Pointer maxPtr = new Memory(Native.getNativeSize(Double.TYPE));
		final Pointer timePtr = new Memory(Native.getNativeSize(Double.TYPE));
		
		Praat.INSTANCE.Formant_getMaximumAndTime(this, iformant, tmin, tmax, bark, interpolate,
				maxPtr, timePtr);
		
		return_maximum.set(maxPtr.getDouble(0));
		return_timeOfMaximum.set(timePtr.getDouble(0));
	}
	
	public double getMinimum (int iformant, double tmin, double tmax, int bark, int interpolate) {
		return Praat.INSTANCE.Formant_getMinimum(this, iformant, tmin, tmax, bark, interpolate);
	}
	
	public double getMaximum (int iformant, double tmin, double tmax, int bark, int interpolate) {
		return Praat.INSTANCE.Formant_getMaximum(this, iformant, tmin, tmax, bark, interpolate);
	}
	
	public double getTimeOfMaximum (int iformant, double tmin, double tmax, int bark, int interpolate) {
		return Praat.INSTANCE.Formant_getTimeOfMaximum(this, iformant, tmin, tmax, bark, interpolate);
	}
	
	public double getTimeOfMinimum (int iformant, double tmin, double tmax, int bark, int interpolate) {
		return Praat.INSTANCE.Formant_getTimeOfMinimum(this, iformant, tmin, tmax, bark, interpolate);
	}

	public double getQuantile (int iformant, double quantile, double tmin, double tmax, int bark) {
		return Praat.INSTANCE.Formant_getQuantile(this, iformant, quantile, tmin, tmax, bark);
	}
	
	public double getQuantileOfBandwidth (int iformant, double quantile, double tmin, double tmax, int bark) {
		return Praat.INSTANCE.Formant_getQuantileOfBandwidth(this, iformant, quantile, tmin, tmax, bark);
	}
	
	public double getMean (int iformant, double tmin, double tmax, int bark) {
		return Praat.INSTANCE.Formant_getMean(this, iformant, tmin, tmax, bark);
	}
	
	public double getStandardDeviation (int iformant, double tmin, double tmax, int bark) {
		return Praat.INSTANCE.Formant_getStandardDeviation(this, iformant, tmin, tmax, bark);
	}

	public void sort () {
		Praat.INSTANCE.Formant_sort(this);
	}

	public Matrix to_Matrix (int iformant) throws PraatException {
		Matrix retVal = null;
		try {
			Praat.wrapperLock.lock();
			retVal = Praat.INSTANCE.Formant_to_Matrix_wrapped (this, iformant);
			Praat.checkAndClearLastError();
		} catch (PraatException e) {
			throw e;
		} finally {
			Praat.wrapperLock.unlock();
		}
		return retVal;
	}
	
	public Matrix to_Matrix_bandwidths (int iformant) throws PraatException {
		Matrix retVal = null;
		try {
			Praat.wrapperLock.lock();
			retVal = Praat.INSTANCE.Formant_to_Matrix_bandwidths_wrapped(
					this, iformant);
			Praat.checkAndClearLastError();
		} catch (PraatException e) {
			throw e;
		} finally {
			Praat.wrapperLock.unlock();
		}
		return retVal;
	}

	public Formant tracker (int numberOfTracks,
		double refF1, double refF2, double refF3, double refF4, double refF5,
		double dfCost,   /* Per kHz. */
		double bfCost, double octaveJumpCost) throws PraatException {
		
		Formant retVal = null;
		try {
			Praat.wrapperLock.lock();
			retVal = Praat.INSTANCE.Formant_tracker_wrapped(this,
					numberOfTracks, refF1, refF2, refF3, refF4, refF5, dfCost,
					bfCost, octaveJumpCost);
			Praat.checkAndClearLastError();
		} catch (PraatException e) {
			throw e;
		} finally {
			Praat.wrapperLock.unlock();
		}
		
		return retVal;
	}

	public Table downto_Table (Boolean includeFrameNumbers,
			Boolean includeTimes, int timeDecimals,
			Boolean includeIntensity, int intensityDecimals,
			Boolean includeNumberOfFormants, int frequencyDecimals,
			Boolean includeBandwidths) throws PraatException {
		Table retVal = null;
		try {
			Praat.wrapperLock.lock();
			retVal = Praat.INSTANCE.Formant_downto_Table_wrapped(this,
					(includeFrameNumbers ? 1 : 0), (includeTimes ? 1 : 0),
					timeDecimals, (includeIntensity ? 1 : 0), intensityDecimals,
					(includeNumberOfFormants ? 1 : 0), frequencyDecimals,
					(includeBandwidths ? 1 : 0));
			Praat.checkAndClearLastError();
		} catch (PraatException e) {
			throw e;
		} finally {
			Praat.wrapperLock.unlock();
		}
		return retVal;
	}
	
	public double getValueAtSample(long iframe, long which, int units) {
		return Praat.INSTANCE.Formant_getValueAtSample(this, new NativeLong(iframe), new NativeLong(which), units);
	}
	
	public double getIntensityAtSample(long iframe) {
		return Praat.INSTANCE.Formant_getIntensityAtSample(this, new NativeLong(iframe));
	}

	public void formula_frequencies(String formula, Interpreter interpreter)
		throws PraatException {
		try {
			Praat.wrapperLock.lock();
			Praat.INSTANCE.Formant_formula_frequencies_wrapped(this, new Str32(formula), interpreter);
			Praat.checkAndClearLastError();
		} catch (PraatException pe) {
			throw pe;
		} finally {
			Praat.wrapperLock.unlock();
		}
	}
	
	@Declared("fon/Formant.h")
	public void formula_bandwidths(String formula, Interpreter interpreter) throws PraatException {
		try {
			Praat.wrapperLock.lock();
			Praat.INSTANCE.Formant_formula_bandwidths_wrapped(this, new Str32(formula), interpreter);
			Praat.checkAndClearLastError();
		} catch (PraatException pe) {
			throw pe;
		} finally {
			Praat.wrapperLock.unlock();
		}
	}

}
