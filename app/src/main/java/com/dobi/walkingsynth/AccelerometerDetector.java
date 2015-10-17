package com.dobi.walkingsynth;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.achartengine.GraphicalView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dobi on 17.10.15.
 */
public class AccelerometerDetector implements SensorEventListener {

    private static final String TAG = "AccelDetector";
    private static final int ACC_MAGNITUDE = 0;
    private static final int ACC_GRAV_DIFF = 1;

    private SensorManager mSensorManager;
    private Sensor mAccel;

    // accelerometer fields
    private int mCurrentOption;
    public static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_UI;
    private double[] gravity = new double[3];
    private double[] linear_acceleration = new double[3];
    private double mAccelResult;
    private long mAccelCount;

    // graph handles
    private GraphicalView mView;
    private AccelerometerGraph mGraph;

    public AccelerometerDetector() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + mAccel.getResolution()
                    + " Max range: " + mAccel.getMaximumRange()
                    + "\n Time interval: " + mAccel.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }
        mCurrentOption = ACC_MAGNITUDE;
    }

    public void startDetector() {
        // just starts just the accelerometer. It doesn't update the UI.
        if (!mSensorManager.registerListener(this, mAccel, CONFIG_SENSOR)) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
        mAccelCount = 0;
    }

    public void stopDetector() {
        mSensorManager.unregisterListener(this,mAccel);
    }

    public void setOption(int o) {
        mCurrentOption = o;
    }

    public void getGraphHandles(GraphicalView gv, AccelerometerGraph ag) {
        mView = gv;
        mGraph = ag;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // handle accelerometer data
        if (mCurrentOption == ACC_MAGNITUDE) {
            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.
            final float alpha = 0.9f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            //Log.d(TAG,"Acc: x=" + linear_acceleration[0] + " y=" + linear_acceleration[1] + " z=" + linear_acceleration[2]);

            // get magnitude/length/norm of a vector
            mAccelResult = Math.sqrt(
                    linear_acceleration[0] * linear_acceleration[0] +
                            linear_acceleration[1] * linear_acceleration[1] +
                            linear_acceleration[2] * linear_acceleration[2]);
        } else if (mCurrentOption == ACC_GRAV_DIFF) {
            mAccelResult = (
                    event.values[0] * event.values[0] +
                            event.values[1] * event.values[1] +
                            event.values[2] * event.values[2]) /
                    (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        }
        mAccelCount += 1;
        Log.d(TAG, "Vec: x= " + mAccelResult + " C=" + mAccelCount);

        // update graph
        mGraph.addNewPoint(mAccelCount, mAccelResult);
        mView.repaint();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
