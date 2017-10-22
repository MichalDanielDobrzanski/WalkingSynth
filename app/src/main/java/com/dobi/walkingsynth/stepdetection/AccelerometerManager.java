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

    private AccelerometerGraph accelerometerGraph;

    private AccelerometerProcessor accelerometerProcessor;

    private SensorManager sensorManager;

    private Sensor sensor;

    private List<OnStepListener> onStepListeners;

    public void addOnStepChangeListener(OnStepListener listener) {
        if (onStepListeners == null)
            onStepListeners = new ArrayList<>();
        onStepListeners.add(listener);
    }

    public AccelerometerManager(SensorManager sensorManager, AccelerometerGraph accelerometerGraph, AccelerometerProcessor accelerometerProcessor) {
        this.sensorManager = sensorManager;
        if (this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + sensor.getResolution()
                    + " Max range: " + sensor.getMaximumRange()
                    + "\n Time interval: " + sensor.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }

        this.accelerometerGraph = accelerometerGraph;
        this.accelerometerProcessor = accelerometerProcessor;
    }

    public void startAccelerometerAndGraph() {
        if (!sensorManager.registerListener(this, sensor, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
    }

    public void stopAccelerometerAndGraph() {
        sensorManager.unregisterListener(this, sensor);
        accelerometerGraph.reset();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accelerometerProcessor.setEvent(event);

        final long eventTime = accelerometerProcessor.timestampToMilliseconds();

        accelerometerProcessor.calcMagnitudeVector();

        accelerometerGraph.invalidate(eventTime);

        if (accelerometerProcessor.detect()) {
            updateListeners(eventTime);
        }
    }

    private void updateListeners(long eventTime) {
        for (OnStepListener listener :
                onStepListeners) {
            listener.onStepDetected(eventTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
