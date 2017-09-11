package com.dobi.walkingsynth.stepdetection;

import android.hardware.SensorEvent;
import android.util.Log;

import java.util.Date;

/**
 * Computing and processing accelerometer data.
 */
public class AccelerometerProcessor {

    public static final float THRESHOLD_INITIAL = 12.72f;

    public static AccelerometerProcessor getInstance() {
        if (mInstance == null)
            mInstance = new AccelerometerProcessor();
        return mInstance;
    }

    private static AccelerometerProcessor mInstance;

    private static final String TAG = AccelerometerProcessor.class.getSimpleName();

    private float mThreshold;
    private boolean isActiveCounter;
    private double mAccelerometerValue;
    private double[] mGravity;
    private double[] mLinearAcceleration;

    private SensorEvent mEvent;

    private AccelerometerProcessor() {
        mThreshold = THRESHOLD_INITIAL;
        isActiveCounter = true;
        mGravity = new double[3];
        mLinearAcceleration = new double[3];
    }

    public void setEvent(SensorEvent e) {
        mEvent = e;
    }

    public double getAccelerometerValue() {
        return mAccelerometerValue;
    }

    public double getThreshold() {
        return mThreshold;
    }

    /**
     * Get event time. http://stackoverflow.com/questions/5500765/accelerometer-sensorevent-timestamp
     */
    public long timestampToMilliseconds() {
        return (new Date()).getTime() + (mEvent.timestamp - System.nanoTime()) / 1000000L;
    }


    /**
     * Vector Magnitude |V| = sqrt(x^2 + y^2 + z^2)
     */
    void calcMagnitudeVector() {
        // Remove the mGravity contribution with the high-pass filter.
        mLinearAcceleration[0] = mEvent.values[0] - mGravity[0];
        mLinearAcceleration[1] = mEvent.values[1] - mGravity[1];
        mLinearAcceleration[2] = mEvent.values[2] - mGravity[2];

        mAccelerometerValue = Math.sqrt(
                mLinearAcceleration[0] * mLinearAcceleration[0] +
                mLinearAcceleration[1] * mLinearAcceleration[1] +
                mLinearAcceleration[2] * mLinearAcceleration[2]);
    }

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

    private int mCurrentSample = 0;

    /**
     * My step detection algorithm.
     * When the value is over the threshold, the step is found and the algorithm sleeps for
     * the specified distance which is {@link #INACTIVE_SAMPLE this }.
     */
    boolean detect() {
        if (mCurrentSample == INACTIVE_SAMPLE) {
            mCurrentSample = 0;
            if (!isActiveCounter)
                isActiveCounter = true;
        }
        if (isActiveCounter && mAccelerometerValue > mThreshold) {
            mCurrentSample = 0;
            isActiveCounter = false;
            return true;
        }

        ++mCurrentSample;
        return false;
    }

    private static final int OFFSET = 90;

    public void onProgressChange(int progress) {
        float diff = (progress + OFFSET) / 100F;
        mThreshold = THRESHOLD_INITIAL * diff;
        Log.d(TAG, "onProgressChange() threshold: " + mThreshold);
    }
}
