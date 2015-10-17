package com.dobi.walkingsynth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.achartengine.GraphicalView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Ma";
    private static final int ACC_MAGNITUDE = 0;
    private static final int ACC_GRAV_DIFF = 1;
    private static final int STEP_DETECTOR = 2;
    private int mCurrentOption;
    private SensorManager mSensorManager;
    // accelerometer fields
    public static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_UI;
    private Sensor mAccel;
    private AtomicBoolean isAccelRunning;
    private double[] gravity = new double[3];
    private double[] linear_acceleration = new double[3];
    private double mAccelResult;
    private long mAccelCount;
    // gyroscope
    private Sensor mGyro;
    // step detector
    private Sensor mStepDetector;
    // graph fields
    private static GraphicalView view;
    private AccelerometerGraph graph = new AccelerometerGraph();
    private Thread updaterThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            Log.d(TAG, "Success! There's a rotation vector. Resolution: " + mGyro.getResolution()
                    + " Max range: " + mGyro.getMaximumRange()
                    + "\n Time interval: " + mGyro.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No rotation vector.");
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + mAccel.getResolution()
                    + " Max range: " + mAccel.getMaximumRange()
                    + "\n Time interval: " + mAccel.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            Log.d(TAG, "Success! There's a step detector. Resolution:" + mStepDetector.getResolution()
                    + " Max range: " + mStepDetector.getMaximumRange()
                    + "\n Time interval: " + mStepDetector.getMinDelay() / 1000 + "ms.");
        }

        // threading stop
        isAccelRunning = new AtomicBoolean(false);

        // UI default setup
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.activity_main_layout);
        view = graph.getView(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mainLayout.addView(view);

        mCurrentOption = ACC_MAGNITUDE;
        final Button selectButton = (Button)findViewById(R.id.select_button);
        selectButton.setText(R.string.accel_mag);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentOption = (mCurrentOption + 1) % 3;
                switch (mCurrentOption) {
                    case ACC_MAGNITUDE:
                        selectButton.setText(R.string.accel_mag);
                        break;
                    case ACC_GRAV_DIFF:
                        selectButton.setText(R.string.accel_grav);
                        break;
                }
            }
        });
    }

    private void startNewUpdatingThread() {
        // Handles UI updating
        isAccelRunning.set(true);
        updaterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isAccelRunning.get()) {
                        Thread.sleep(50);
                        graph.addNewPoint(mAccelCount, mAccelResult);
                        view.repaint();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //At the end of run(), your thread dies.
        });
        updaterThread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // handle accelerometer data

            // start from scratch
            if (mAccelCount == 0) {
                //startNewUpdatingThread();
            }
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
            graph.addNewPoint(mAccelCount, mAccelResult);
            view.repaint();

        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // handle gyroscope data
        }

    }

    private void stopAccelerometer() {
        mSensorManager.unregisterListener(this,mAccel);
        isAccelRunning.set(false);
    }

    private void startAccelerometer() {
        // starts just the accelerometer. It doesn't update the UI.
        boolean res = mSensorManager.registerListener(this, mAccel, CONFIG_SENSOR);
        if (!res) {
            Log.d(TAG,"The sensor is not supported and unsuccessfully enabled.");
        }
        mAccelCount = 0;
    }

    private void startStepDetector() {
        mSensorManager.registerListener(this,mStepDetector,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume register Listeners");
        mSensorManager.registerListener(this,mGyro,SensorManager.SENSOR_DELAY_UI);
        startStepDetector();
        startAccelerometer();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause UNregister Listeners");
        mSensorManager.unregisterListener(this,mStepDetector);
        stopAccelerometer();

    }
}
