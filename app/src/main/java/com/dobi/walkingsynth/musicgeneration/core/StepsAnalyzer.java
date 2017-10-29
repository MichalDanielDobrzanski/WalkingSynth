package com.dobi.walkingsynth.musicgeneration.core;

import com.dobi.walkingsynth.stepdetection.OnStepListener;

import java.util.ArrayList;
import java.util.List;

public class StepsAnalyzer implements OnStepListener {

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

    public int getStepsInterval() {
        return stepsInterval;
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

    public interface StepsListener {
        void onStep(int step);
    }

}
