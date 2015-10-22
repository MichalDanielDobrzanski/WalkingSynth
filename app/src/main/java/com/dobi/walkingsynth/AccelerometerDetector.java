package com.dobi.walkingsynth;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.achartengine.GraphicalView;

import java.util.Date;

/**
 * Created by dobi on 17.10.15.
 */
public class AccelerometerDetector implements SensorEventListener {

    private static final String TAG = "AccelDetector";
    public static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_UI;

    private int mStepCount = 0;
    private double mThresh = AccelerometerGraph.THRESH_INIT;
    private double[] mAccelResult = new double[AccelOptions.size];
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private AccelerometerGraph mAccelGraph;
    private GraphicalView mGraphView;
    private SharedPreferences mPreferences;

    private OnStepCountChangeListener mStepListener;
    public void setStepCountChangeListener(OnStepCountChangeListener listener) {
        mStepListener = listener;
    }


    public AccelerometerDetector(SensorManager sensorManager,GraphicalView view, AccelerometerGraph graph, SharedPreferences prefs) {
        mStepListener = null;
        mPreferences = prefs;
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

    private void detectStep(int i,long event) {
        if (SignalAlgorithms.detectStep(i, mThresh)) {
            SignalAlgorithms.isActiveCounter = false;
            ++mStepCount;
            if (mStepListener != null)
                mStepListener.onStepCountChange(mStepCount);
        }
    }

    private void saveThreshold() {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putFloat(AccelerometerGraph.THRESH,(float)mThresh);
        preferencesEditor.apply();
    }


    public void startDetector() {
        // just starts just the accelerometer. It doesn't update the UI.
        if (!mSensorManager.registerListener(this, mAccel, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
        mAccelGraph.initialize();
    }

    public void stopDetector() {
        mSensorManager.unregisterListener(this, mAccel);
    }

    public void setVisibility(int opt, boolean show) {
        mAccelGraph.setVisibility(opt, show);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // handle accelerometer data
        //Log.d(TAG,"sens changed: " + mCurrentOptions.toString());
        SignalAlgorithms.sendEvent(event);
        mAccelResult[0] = SignalAlgorithms.calcMagnitudeVector(0); // |V|
        mAccelResult[0] = SignalAlgorithms.calcExpMovAvg(0);
        mAccelResult[1] = SignalAlgorithms.calcMagnitudeVector(1);
        // process timestamp
        final long eventTime = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        // update graph with value and timestamp
        //Log.d(TAG, "Vec: x= " + mAccelResult[0] + " C=" + eventTime);
        mAccelGraph.addNewPoints(eventTime, mAccelResult);
        //step detection
        detectStep(1, eventTime);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
