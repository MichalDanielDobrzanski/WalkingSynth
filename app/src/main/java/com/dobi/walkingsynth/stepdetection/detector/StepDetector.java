package com.dobi.walkingsynth.stepdetection.detector;

import android.hardware.SensorEvent;

public interface StepDetector {

    void invalidate(SensorEvent sensorEvent);

    boolean detect(double currentThreshold);

    int getStepCount();

    double getCurrentValue();

    long getTimestamp();

}
