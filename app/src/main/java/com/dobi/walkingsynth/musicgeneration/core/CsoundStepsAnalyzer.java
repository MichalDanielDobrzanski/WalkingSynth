package com.dobi.walkingsynth.musicgeneration.core;

import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsListener;
import com.dobi.walkingsynth.stepdetection.OnStepListener;

import java.util.ArrayList;
import java.util.List;

public class CsoundStepsAnalyzer implements StepsAnalyzer, OnStepListener {

    public static final int INITIAL_STEPS_INTERVAL = 10;

    private int stepCount;

    private int stepsInterval = INITIAL_STEPS_INTERVAL;

    private Integer[] stepIntervals;

    private List<StepsListener> mStepsListeners;

    public CsoundStepsAnalyzer() {
        loadDefaultStepIntervals();
    }

    private void loadDefaultStepIntervals() {
        stepIntervals = new Integer[] {INITIAL_STEPS_INTERVAL, 20, 30, 50, 100 };
    }

    public void addStepsListener(StepsListener listener) {
        if (mStepsListeners == null)
            mStepsListeners = new ArrayList<>();
        mStepsListeners.add(listener);
    }

    @Override
    public void setStepsInterval(int newStepsInterval) {

    }

    @Override
    public int getStepsInterval() {
        return stepsInterval;
    }

    @Override
    public Integer[] getStepsIntervals() {
        return stepIntervals;
    }

    @Override
    public void onStepDetected(long milliseconds, int stepsCount) {
        stepCount = stepsCount;
        invalidateListeners();
    }

    private void invalidateListeners() {
        if (mStepsListeners != null) {
            for (StepsListener listener : mStepsListeners) {
                listener.onStep(stepCount);
            }
        }
    }
}
