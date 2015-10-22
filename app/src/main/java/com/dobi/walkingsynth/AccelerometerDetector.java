package com.dobi.walkingsynth;

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

    private long mAccelCount;
    private double[] gravity = new double[3];
    private double[] linear_acceleration = new double[3];
    private double[] mAccelResult = new double[AccOptions.size];
    private double mLastOne;
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private GraphicalView mGraphView;
    private AccelerometerGraph mAccGraph;
    private ScalarKalmanFilter mFiltersCascade[] = new ScalarKalmanFilter[3];

    public AccelerometerDetector(SensorManager sensorManager,GraphicalView view, AccelerometerGraph graph) {
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
        mAccGraph = graph;
        // set filter
        mFiltersCascade[0] = new ScalarKalmanFilter(1, 1, 0.01f, 0.0025f);
        mFiltersCascade[1] = new ScalarKalmanFilter(1, 1, 0.01f, 0.0025f);
        mFiltersCascade[2] = new ScalarKalmanFilter(1, 1, 0.01f, 0.0025f);
    }

    private void calcFilterGravity(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        final float alpha = 0.9f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
    }

    private void calcMagnitudeVector(SensorEvent event, int order) {

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
        //Log.d(TAG,"Acc: x=" + linear_acceleration[0] + " y=" + linear_acceleration[1] + " z=" + linear_acceleration[2]);

        // get magnitude/length/norm of a vector
        mAccelResult[order] = Math.sqrt(
                linear_acceleration[0] * linear_acceleration[0] +
                linear_acceleration[1] * linear_acceleration[1] +
                linear_acceleration[2] * linear_acceleration[2]);
    }

    private void calcGravityDiff(SensorEvent event, int order) {
        mAccelResult[order] = (
                event.values[0] * event.values[0] +
                event.values[1] * event.values[1] +
                event.values[2] * event.values[2]) /
                (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    }

    private void calcKalmanDeriv(SensorEvent event, int order) {
        mAccelResult[order] = filter(mAccelResult[order]);
        //mAccelResult[order] = Math.abs(mAccelResult[order] - mLastOne);
        //mLastOne = mAccelResult[order];
    }

    /**
     * Smoothes the signal from accelerometer
     */
    private double filter(double measurement){
        double f1 = mFiltersCascade[0].correct(measurement);
        double f2 = mFiltersCascade[1].correct(f1);
        double f3 = mFiltersCascade[2].correct(f2);
        return f3;
    }


    public void startDetector() {
        // just starts just the accelerometer. It doesn't update the UI.
        if (!mSensorManager.registerListener(this, mAccel, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
        //mAccelCount = 0;
    }

    public void stopDetector() {
        mSensorManager.unregisterListener(this,mAccel);
    }

    public void setVisibility(int opt, boolean show) {
        mAccGraph.setVisibility(opt,show);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // handle accelerometer data
        //Log.d(TAG,"sens changed: " + mCurrentOptions.toString());
        calcMagnitudeVector(event, 0); // |V|
        //calcKalmanDeriv(event,0);
        calcGravityDiff(event, 1); // delta G
        // process timestamp
        mAccelCount += 1;
        //mAccelCount = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        // update graph
        Log.d(TAG, "Vec: x= " + mAccelResult[0] + " C=" + mAccelCount);
        mAccGraph.addNewPoint(mAccelCount, mAccelResult);
        mGraphView.repaint();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
