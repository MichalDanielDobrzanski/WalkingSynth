package com.dobi.walkingsynth.model.stepdetection;

import android.hardware.SensorEvent;
import android.util.Log;

import java.util.Date;

/**
 * Computing and processing accelerometer data.
 */
public class StepDetector {

    private static final String TAG = StepDetector.class.getSimpleName();

    private static final int MAX_STEPS_COUNT = 10000;

    /**
     * Step detecting parameter. For how many samples it is sleeping.
     * If accelerometer's DELAY_GAME is T ~= 20ms, this means that f = 50Hz and MAX_TEMPO = 240bpms
     * 60bpm  - 1000ms
     * 240bpm - 250ms
     * n is samples
     * n = 250ms / T
     * n = 250 / 20 ~= 12
     */
    private static final int INACTIVE_SAMPLE = 12;

    private int currentSample = 0;

    private int stepCount = 0;

    private boolean isActiveCounter;

    private double mAccelerometerValue;

    private SensorEvent mEvent;

    public StepDetector() {
        isActiveCounter = true;
    }

    public void invalidate(SensorEvent e) {
        mEvent = e;

        calculateCurrentValue();
    }

    /**
     * Vector Magnitude |V| = sqrt(x^2 + y^2 + z^2)
     */
    private void calculateCurrentValue() {
        mAccelerometerValue = Math.sqrt(
                mEvent.values[0] * mEvent.values[0] +
                        mEvent.values[1] * mEvent.values[1] +
                        mEvent.values[2] * mEvent.values[2]);
    }

    /**
     * My step detection algorithm.
     * When the value is over the threshold, the step is found and the algorithm sleeps for
     * the specified distance which is {@link #INACTIVE_SAMPLE this }.
     */
    public boolean detect(double currentThreshold) {
        if (currentSample == INACTIVE_SAMPLE) {
            currentSample = 0;
            if (!isActiveCounter)
                isActiveCounter = true;
        }
        if (isActiveCounter && (mAccelerometerValue > currentThreshold)) {
            currentSample = 0;
            isActiveCounter = false;
            Log.d(TAG, "detect() true for threshold " + currentThreshold);
            stepCount++;
            if (stepCount == MAX_STEPS_COUNT)
                stepCount = 0;
            return true;
        }

        ++currentSample;
        return false;
    }

    public int getStepCount() {
        return stepCount;
    }

    public double getCurrentValue() {
        return mAccelerometerValue;
    }

    /**
     * Get event time. http://stackoverflow.com/questions/5500765/accelerometer-sensorevent-timestamp
     */
    public long getTimestamp() {
        return (new Date()).getTime() + (mEvent.timestamp - System.nanoTime()) / 1000000L;
    }


}
