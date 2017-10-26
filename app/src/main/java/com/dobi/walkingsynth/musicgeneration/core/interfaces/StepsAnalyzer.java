package com.dobi.walkingsynth.musicgeneration.core.interfaces;

import com.dobi.walkingsynth.stepdetection.OnStepListener;

public interface StepsAnalyzer extends OnStepListener {

    void setStepsInterval(int newStepsInterval);

    int getStepsInterval();

    Integer[] getStepsIntervals();

    void addStepsListener(StepsListener listener);
}
