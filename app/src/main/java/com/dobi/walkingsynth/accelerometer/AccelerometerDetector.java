package com.dobi.walkingsynth.accelerometer;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.achartengine.GraphicalView;

/**
 * Configuring accelerometer and handling its results.
 */
public class AccelerometerDetector implements SensorEventListener {

    private static final String TAG = AccelerometerDetector.class.getSimpleName();
    /**
     * Suggested periods:
     * DELAY_UI: T ~= 60ms => f = 16,6Hz
     * DELAY_GAME: T ~= 20ms => f = 50Hz
     */
    public static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_GAME;

    private double[] mAccelResult = new double[AccelerometerSignals.count];
    private AccelerometerGraph mAccelGraph;
    private AccelerometerProcessing mAccelProcessing = AccelerometerProcessing.getInstance();
    private GraphicalView mGraphView;

    private SensorManager mSensorManager;
    private Sensor mAccel;

    private OnStepCountChangeListener mStepListener;

    /**
     * Listener setting for Step Detected event
     * @param listener a listener.
     */
    public void setStepCountChangeListener(OnStepCountChangeListener listener) {
        mStepListener = listener;
    }

    public AccelerometerDetector(SensorManager sensorManager,GraphicalView view, AccelerometerGraph graph, SharedPreferences prefs) {
        mStepListener = null;
        mSensorManager = sensorManager;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + mAccel.getResolution()
                    + " Max range: " + mAccel.getMaximumRange()
                    + "\n Time interval: " + mAccel.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }
        // get graph handles
        mGraphView = view;
        mAccelGraph = graph;
    }

    public void startDetector() {
        // just starts just the accelerometer. It doesn't update the UI.
        if (!mSensorManager.registerListener(this, mAccel, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
    }

    public void stopDetector() {
        mSensorManager.unregisterListener(this, mAccel);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // handle accelerometer data
        mAccelProcessing.setEvent(event);
        final long eventMsecTime = mAccelProcessing.timestampToMilliseconds();

        mAccelResult[0] = mAccelProcessing.calcMagnitudeVector(0);
        mAccelResult[0] = mAccelProcessing.calcExpMovAvg(0);
        mAccelResult[1] = mAccelProcessing.calcMagnitudeVector(1);
        //Log.d(TAG, "Vec: x= " + mAccelResult[0] + " C=" + eventMsecTime);

        // update graph with value and timestamp
        mAccelGraph.addNewPoints(eventMsecTime, mAccelResult);

        // step detection
        if (mAccelProcessing.stepDetected(1)) {
            // step is found!

            // notify potential listeners
            if (mStepListener != null)
                mStepListener.onStepCountChange(eventMsecTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
