package com.dobi.walkingsynth.stepdetection;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerManager implements SensorEventListener {

    private static final String TAG = AccelerometerManager.class.getSimpleName();
    /**
     * Suggested periods:
     * DELAY_UI: T ~= 60ms => f = 16,6Hz
     * DELAY_GAME: T ~= 20ms => f = 50Hz
     */
    public static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_GAME;

    private int mStepsCount = 0;

    private AccelGraph mAccelGraph;
    private AccelerometerProcessing mAccelerometerProcessing;
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private StepListener mStepListener;

    /**
     * Listener setting for Step Detected event
     * @param listener a listener.
     */
    public void setOnStepChangeListener(StepListener listener) {
        mStepListener = listener;
    }

    public AccelerometerManager(SensorManager sensorManager, AccelGraph graph, AccelerometerProcessing accelerometerProcessing) {
        mSensorManager = sensorManager;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + mAccel.getResolution()
                    + " Max range: " + mAccel.getMaximumRange()
                    + "\n Time interval: " + mAccel.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }

        mAccelGraph = graph;
        mAccelerometerProcessing = accelerometerProcessing;
    }

    public void startDetector() {
        if (!mSensorManager.registerListener(this, mAccel, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
    }

    public void stopDetector() {
        mSensorManager.unregisterListener(this, mAccel);
        mAccelGraph.reset();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mAccelerometerProcessing.setEvent(event);

        final long eventTime = mAccelerometerProcessing.timestampToMilliseconds();

        mAccelerometerProcessing.calcMagnitudeVector();

        mAccelGraph.invalidate(eventTime);

        if (mAccelerometerProcessing.detect()) {
            mStepsCount++;
            if (mStepListener != null)
                mStepListener.onStepChange(mStepsCount, eventTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
