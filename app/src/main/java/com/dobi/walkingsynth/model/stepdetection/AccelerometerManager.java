package com.dobi.walkingsynth.model.stepdetection;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.dobi.walkingsynth.model.musicgeneration.core.AudioPlayer;
import com.dobi.walkingsynth.view.GraphView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

public class AccelerometerManager {

    private static final String TAG = AccelerometerManager.class.getSimpleName();

    /**
     * Suggested periods:
     * DELAY_UI: T ~= 60ms => f = 16,6Hz
     * DELAY_GAME: T ~= 20ms => f = 50Hz
     */
    private static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_GAME;

    private GraphView accelerometerGraph;

    private StepDetector stepDetector;

    private SensorManager sensorManager;

    private AudioPlayer audioPlayer;

    private Sensor sensor;

    private List<OnStepListener> onStepListeners;

    private ConnectableObservable<SensorEvent> accelerometerObservable;

    private Disposable disposable;

    private double threshold;

    public AccelerometerManager(SensorManager sensorManager,
                                GraphView accelerometerGraph,
                                StepDetector stepDetector,
                                AudioPlayer audioPlayer) {

        this.sensorManager = sensorManager;

        this.accelerometerGraph = accelerometerGraph;

        this.stepDetector = stepDetector;

        this.audioPlayer = audioPlayer;

        this.accelerometerObservable = Observable.<SensorEvent>create(emitter -> {
            if (this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Log.d(TAG, "Success! There's a accelerometer. Resolution:" + sensor.getResolution()
                        + " Max range: " + sensor.getMaximumRange()
                        + "\n Time interval: " + sensor.getMinDelay() / 1000 + "ms.");
            } else {
                Log.e(TAG, "Failure! No accelerometer.");
                emitter.onError(new Throwable());
            }

            SensorEventListener sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    emitter.onNext(event);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            sensorManager.registerListener(sensorEventListener, sensor, CONFIG_SENSOR);

            accelerometerGraph.resume();

            emitter.setCancellable(() -> {
                Log.d(TAG, "AccelerometerManager: cancelling.");

                sensorManager.unregisterListener(sensorEventListener);

                accelerometerGraph.pause();
            });
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .publish(); // make it hot observable

        accelerometerObservable.connect();
    }

    private Double getSensorValue(SensorEvent event) {
        return Math.sqrt(event.values[0] * event.values[0] +
                        event.values[1] * event.values[1] +
                        event.values[2] * event.values[2]);
    }

    /**
     * Gets event time. http://stackoverflow.com/questions/5500765/accelerometer-sensorevent-timestamp
     */
    private long getTimestamp(SensorEvent event) {
        return (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;

        accelerometerGraph.onThreshold(threshold);
    }

    public void resume() {
        Log.d(TAG, "resume: resuming...");

        disposable = accelerometerObservable.subscribe(event -> {

            Double sensorValue = getSensorValue(event);

            long timeStamp = getTimestamp(event);

            accelerometerGraph.invalidate(timeStamp, sensorValue, threshold);

            if (stepDetector.detect(sensorValue, threshold)) {
                Log.d(TAG, "AccelerometerManager: Detected a step");

                audioPlayer.getStepsAnalyzer().onStepEvent(timeStamp, stepDetector.getStepCount());
                audioPlayer.getTempoAnalyzer().onStepEvent(timeStamp, stepDetector.getStepCount());

                updateListeners(timeStamp);
            }
        });

        accelerometerGraph.resume();
    }

    public void stop() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();

        accelerometerGraph.pause();
    }

    private void updateListeners(long eventTime) {
        for (OnStepListener listener : onStepListeners) {
            listener.onStepEvent(eventTime, stepDetector.getStepCount());
        }
    }

    public void addOnStepChangeListener(OnStepListener listener) {
        if (onStepListeners == null)
            onStepListeners = new ArrayList<>();
        onStepListeners.add(listener);
    }

    public interface OnStepListener {
        void onStepEvent(long milliseconds, int stepsCount);
    }
}
