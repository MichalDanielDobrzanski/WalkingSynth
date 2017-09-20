package com.dobi.walkingsynth.musicgeneration.core.interfaces;

import com.dobi.walkingsynth.stepdetection.OnStepListener;

public interface StepsAnalyzer extends OnStepListener {

    void setStepsInterval(int newStepsInterval);

    int getStepsInterval();

    int getStepsCount();

    Integer[] getStepsIntervals();

    void addStepsListener(StepsListener listener);
}
