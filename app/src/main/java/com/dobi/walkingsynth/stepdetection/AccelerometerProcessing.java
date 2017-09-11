package com.dobi.walkingsynth.stepdetection;

import android.hardware.SensorEvent;
import android.util.Log;

import java.util.Date;

/**
 * Computing and processing accelerometer data.
 */
public class AccelerometerProcessing  {

    public static final float THRESHOLD_INITIAL = 12.72f;

    public static AccelerometerProcessing getInstance() {
        if (mInstance == null)
            mInstance = new AccelerometerProcessing();
        return mInstance;
    }

    private static AccelerometerProcessing mInstance;

    private static final String TAG = AccelerometerProcessing.class.getSimpleName();

    /**
     * Step detecting parameter. How many periods it is sleeping.
     * If DELAY_GAME: T ~= 20ms => f = 50Hz
     * and MAX_TEMPO = 240bpms
     * then:
     * 60bpm - 1000milliseconds
     * 240bpm - 250milliseconds
     *
     * n - periods
     * n = 250msec / T
     * n = 250 / 20 ~= 12
     */
    private static final int INACTIVE_PERIODS = 12;

    private float mThreshold;
    private boolean isActiveCounter;

    private double mAccelerometerCurrentValue;

    private double[] mGravity = new double[3];
    private double[] mLinearAcceleration = new double[3];

    private SensorEvent mEvent;

    private AccelerometerProcessing() {
        mThreshold = THRESHOLD_INITIAL;
        isActiveCounter = true;
        mGravity = new double[3];
        mLinearAcceleration = new double[3];
    }

    public void setEvent(SensorEvent e) {
        mEvent = e;
    }

    public double getAccelerometerCurrentValue() {
        return mAccelerometerCurrentValue;
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
    public void calcMagnitudeVector() {
        // Remove the mGravity contribution with the high-pass filter.
        mLinearAcceleration[0] = mEvent.values[0] - mGravity[0];
        mLinearAcceleration[1] = mEvent.values[1] - mGravity[1];
        mLinearAcceleration[2] = mEvent.values[2] - mGravity[2];

        mAccelerometerCurrentValue = Math.sqrt(
                mLinearAcceleration[0] * mLinearAcceleration[0] +
                mLinearAcceleration[1] * mLinearAcceleration[1] +
                mLinearAcceleration[2] * mLinearAcceleration[2]);
    }

    private int mInactiveCounter = 0;

    /**
     * My step detection algorithm.
     * When the value is over the threshold, the step is found and the algorithm sleeps for
     * the specified distance which is {@link #INACTIVE_PERIODS this }.
     */
    public boolean detect() {
        if (mInactiveCounter == INACTIVE_PERIODS) {
            mInactiveCounter = 0;
            if (!isActiveCounter)
                isActiveCounter = true;
        }
        if (mAccelerometerCurrentValue > mThreshold) {
            if (isActiveCounter) {
                mInactiveCounter = 0;
                isActiveCounter = false;
                return true;
            }
        }
        ++mInactiveCounter;
        return false;
    }

    private static final int OFFSET = 90;

    public void onProgressChange(int progress) {
        float diff = (progress + OFFSET) / 100F;
        mThreshold = THRESHOLD_INITIAL * diff;
        Log.d(TAG, "onProgressChange() threshold: " + mThreshold);
    }
}
