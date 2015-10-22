package com.dobi.walkingsynth;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by dobi on 22.10.15.
 */
public class SignalAlgorithms {

    private static int mInactiveCounter = 0;
    public static boolean isActiveCounter = true;
    private static double[] gravity = new double[3];
    private static double[] linear_acceleration = new double[3];
    private static double[] mAccelResult = new double[AccelOptions.size];
    private static double[] mLastAccelResult = new double[AccelOptions.size];
    private static ScalarKalmanFilter mFiltersCascade[] = new ScalarKalmanFilter[3];
    private static SensorEvent event;

    public static void initKalman() {
        // set filter
        mFiltersCascade[0] = new ScalarKalmanFilter(1, 1, 0.01f, 0.0025f);
        mFiltersCascade[1] = new ScalarKalmanFilter(1, 1, 0.01f, 0.0025f);
        mFiltersCascade[2] = new ScalarKalmanFilter(1, 1, 0.01f, 0.0025f);
    }

    /**
     * Smoothes the signal from accelerometer
     */
    private static double filter(double measurement){
        double f1 = mFiltersCascade[0].correct(measurement);
        double f2 = mFiltersCascade[1].correct(f1);
        double f3 = mFiltersCascade[2].correct(f2);
        return f3;
    }

    public static void sendEvent(SensorEvent e) {
        event = e;
    }

    public static void calcFilterGravity() {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        final float alpha = 0.9f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
    }

    public static double calcMagnitudeVector(int i) {

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
        //Log.d(TAG,"Acc: x=" + linear_acceleration[0] + " y=" + linear_acceleration[1] + " z=" + linear_acceleration[2]);

        // get magnitude/length/norm of a vector
        mAccelResult[i] = Math.sqrt(
                linear_acceleration[0] * linear_acceleration[0] +
                        linear_acceleration[1] * linear_acceleration[1] +
                        linear_acceleration[2] * linear_acceleration[2]);
        return mAccelResult[i];
    }

    public static double calcGravityDiff(int i) {
        mAccelResult[i] = (
                event.values[0] * event.values[0] +
                        event.values[1] * event.values[1] +
                        event.values[2] * event.values[2]) /
                (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        return mAccelResult[i];
    }

    public static double calcKalman(int i) {
        mAccelResult[i] = filter(mAccelResult[i]);
        //mAccelResult[i] = Math.abs(mAccelResult[i] - mLastOne);
        //mLastOne = mAccelResult[i];
        return mAccelResult[i];
    }

    public static double calcDeriv(int i) {
        mAccelResult[i] = Math.abs(mAccelResult[i] - mLastAccelResult[i]);
        mLastAccelResult[i] = mAccelResult[i];
        return mAccelResult[i];
    }

    public static double calcExpMovAvg(int i) {
        final double alpha = 0.1;
        mAccelResult[i] = alpha * mAccelResult[i] + (1 - alpha) * mLastAccelResult[i];
        mLastAccelResult[i] = mAccelResult[i];
        return mAccelResult[i];
    }

    public static boolean detectStep(int i,double thresh) {
        if (mInactiveCounter == 7) {
            mInactiveCounter = 0;
            isActiveCounter = true;
        }
        if (mAccelResult[i] > thresh) {
            if (isActiveCounter) {
                mInactiveCounter = 0;
                return true;
            }
        }
        ++mInactiveCounter;
        return false;

    }
}
