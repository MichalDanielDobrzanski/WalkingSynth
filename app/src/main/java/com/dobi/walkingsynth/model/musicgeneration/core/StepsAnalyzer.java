package com.dobi.walkingsynth.model.musicgeneration.core;

import com.dobi.walkingsynth.model.stepdetection.AccelerometerManager;

import java.util.ArrayList;
import java.util.List;

public class StepsAnalyzer implements AccelerometerManager.OnStepListener {

    public static final int INITIAL_STEPS_INTERVAL = 10;

    private int stepCount;

    private int stepsInterval = INITIAL_STEPS_INTERVAL;

    private Integer[] stepIntervals;

    private List<StepsListener> mStepsListeners;

    public StepsAnalyzer() {
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

    public int getInterval() {
        return stepsInterval;
    }

    public Integer[] getIntervals() {
        return stepIntervals;
    }

    @Override
    public void onStepEvent(long milliseconds, int stepsCount) {
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

    public interface StepsListener {
        void onStep(int step);
    }

}
