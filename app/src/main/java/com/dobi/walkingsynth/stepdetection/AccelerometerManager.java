package com.dobi.walkingsynth.stepdetection;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AccelerometerManager implements SensorEventListener {

    private static final String TAG = AccelerometerManager.class.getSimpleName();

    /**
     * Suggested periods:
     * DELAY_UI: T ~= 60ms => f = 16,6Hz
     * DELAY_GAME: T ~= 20ms => f = 50Hz
     */
    private static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_GAME;

    private AccelerometerGraph mAccelerometerGraph;
    private AccelerometerProcessor mAccelerometerProcessor;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private List<OnStepListener> mStepListeners;

    public void addOnStepChangeListener(OnStepListener listener) {
        if (mStepListeners == null)
            mStepListeners = new ArrayList<>();
        mStepListeners.add(listener);
    }

    public AccelerometerManager(SensorManager sensorManager, AccelerometerGraph graph) {
        mSensorManager = sensorManager;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + mSensor.getResolution()
                    + " Max range: " + mSensor.getMaximumRange()
                    + "\n Time interval: " + mSensor.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }

        mAccelerometerGraph = graph;
        mAccelerometerProcessor = AccelerometerProcessor.getInstance();
    }

    public void startAccelerometerAndGraph() {
        if (!mSensorManager.registerListener(this, mSensor, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
    }

    public void stopAccelerometerAndGraph() {
        mSensorManager.unregisterListener(this, mSensor);
        mAccelerometerGraph.reset();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mAccelerometerProcessor.setEvent(event);

        final long eventTime = mAccelerometerProcessor.timestampToMilliseconds();

        mAccelerometerProcessor.calcMagnitudeVector();

        mAccelerometerGraph.invalidate(eventTime);

        if (mAccelerometerProcessor.detect()) {
            updateListeners(eventTime);
        }
    }

    private void updateListeners(long eventTime) {
        for (OnStepListener listener :
                mStepListeners) {
            listener.onStepDetected(eventTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
