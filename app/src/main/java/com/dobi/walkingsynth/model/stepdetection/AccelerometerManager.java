package com.dobi.walkingsynth.model.stepdetection;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.dobi.walkingsynth.model.musicgeneration.core.AudioPlayer;
import com.dobi.walkingsynth.view.GraphView;

import java.util.ArrayList;
import java.util.List;

import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_THRESHOLD_KEY;

public class AccelerometerManager implements SensorEventListener {

    private static final String TAG = AccelerometerManager.class.getSimpleName();

    private static final float THRESHOLD_INITIAL = 12.72f;

    public static final float MAX_THRESHOLD = 25f;

    private static final int OFFSET = 90;

    /**
     * Suggested periods:
     * DELAY_UI: T ~= 60ms => f = 16,6Hz
     * DELAY_GAME: T ~= 20ms => f = 50Hz
     */
    private static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_GAME;

    private final SharedPreferences sharedPreferences;

    private final AudioPlayer audioPlayer;

    private GraphView accelerometerGraph;

    private StepDetector stepDetector;

    private SensorManager sensorManager;

    private Sensor sensor;

    private List<OnStepListener> onStepListeners;

    private double threshold;

    private OnThresholdChangeListener thresholdChangeListener;

    public AccelerometerManager(SharedPreferences sharedPreferences, SensorManager sensorManager,
                                GraphView accelerometerGraph, StepDetector stepDetector,
                                AudioPlayer audioController) {
        this.sharedPreferences = sharedPreferences;

        this.audioPlayer = audioController;

        restoreThreshold();

        this.sensorManager = sensorManager;
        initalizeAccelerometer();

        this.accelerometerGraph = accelerometerGraph;

        this.stepDetector = stepDetector;
    }

    private void initalizeAccelerometer() {
        if (this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + sensor.getResolution()
                    + " Max range: " + sensor.getMaximumRange()
                    + "\n Time interval: " + sensor.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }
    }

    private void restoreThreshold() {
        this.threshold = sharedPreferences.getFloat(PREFERENCES_VALUES_THRESHOLD_KEY, THRESHOLD_INITIAL);
    }

    public void saveThreshold() {
        sharedPreferences.edit().putFloat(PREFERENCES_VALUES_THRESHOLD_KEY, (float)getThreshold()).apply();
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        Log.d(TAG, "setThreshold: to " + threshold);
        this.threshold = threshold;
        if (thresholdChangeListener != null)
            thresholdChangeListener.onThresholdChanged(threshold);
    }

    public void setOnThresholdChangeListener(OnThresholdChangeListener onThresholdChangeListener) {
        this.thresholdChangeListener = onThresholdChangeListener;
    }

    public void resumeAccelerometerAndGraph() {
        if (!sensorManager.registerListener(this, sensor, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }

        accelerometerGraph.resume();
    }

    public void pauseAccelerometerAndGraph() {
        sensorManager.unregisterListener(this, sensor);

        accelerometerGraph.pause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepDetector.invalidate(event);

        final long eventTime = stepDetector.getTimestamp();
        accelerometerGraph.invalidate(eventTime, stepDetector.getCurrentValue(), threshold);

        if (stepDetector.detect(threshold)) {
            updateListeners(eventTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void updateListeners(long eventTime) {
        for (OnStepListener listener : onStepListeners) {
            listener.onStepDetected(eventTime, stepDetector.getStepCount());
        }
    }

    public static double progressToThreshold(int progress) {
        double res = THRESHOLD_INITIAL * (progress + OFFSET) / 100F;
        Log.d(TAG, "progressToThreshold() threshold: " + res);
        return res;
    }

    public static int thresholdToProgress(double threshold) {
        int res = (int)(100 * threshold / THRESHOLD_INITIAL) - OFFSET;
        Log.d(TAG, "thresholdToProgress() progress: " + res);
        return res > 100 ? 100 : res;
    }

    public void addOnStepChangeListener(OnStepListener listener) {
        if (onStepListeners == null)
            onStepListeners = new ArrayList<>();
        onStepListeners.add(listener);
    }

    public interface OnThresholdChangeListener {
        void onThresholdChanged(double newValue);
    }

    public interface OnStepListener {
        void onStepDetected(long milliseconds, int stepsCount);
    }
}
