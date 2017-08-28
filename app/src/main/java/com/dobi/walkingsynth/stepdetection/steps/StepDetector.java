package com.dobi.walkingsynth.stepdetection.steps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.dobi.walkingsynth.stepdetection.accelerometer.AccelerometerProcessing;
import com.dobi.walkingsynth.stepdetection.accelerometer.AccelerometerSignals;
import com.dobi.walkingsynth.stepdetection.plotting.AccelGraph;

public class StepDetector implements SensorEventListener {

    private static final String TAG = StepDetector.class.getSimpleName();
    /**
     * Suggested periods:
     * DELAY_UI: T ~= 60ms => f = 16,6Hz
     * DELAY_GAME: T ~= 20ms => f = 50Hz
     */
    public static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_GAME;

    private int mStepsCount = 0;

    private double[] mAccelResult = new double[AccelerometerSignals.count];

    private AccelGraph mAccelGraph;

    private AccelerometerProcessing mAccelerometerProcessing = AccelerometerProcessing.getInstance();

    private SensorManager mSensorManager;

    private Sensor mAccel;

    private StepListener mStepListener;

    /**
     * Listener setting for Step Detected event
     * @param listener a listener.
     */
    public void setStepCountListener(StepListener listener) {
        mStepListener = listener;
    }

    public StepDetector(SensorManager sensorManager, AccelGraph graph) {
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
        mAccelGraph.reset();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // handle accelerometer data
        mAccelerometerProcessing.setEvent(event);
        final long eventMilisecTime = mAccelerometerProcessing.timestampToMilliseconds();

        mAccelResult[0] = mAccelerometerProcessing.calcMagnitudeVector(0);
        mAccelResult[0] = mAccelerometerProcessing.calcExpMovAvg(0);
        mAccelResult[1] = mAccelerometerProcessing.calcMagnitudeVector(1);
        //Log.d(TAG, "Vec: x= " + mAccelResult[0] + " C=" + eventMilisecTime);

        // update graph with value and timestamp
        mAccelGraph.invalidate(eventMilisecTime, mAccelResult);

        // step detection
        if (mAccelerometerProcessing.stepDetected(1)) {
            // step is found!
            mStepsCount++;
            // notify potential listeners
            if (mStepListener != null)
                mStepListener.onStepCount(mStepsCount, eventMilisecTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
