package com.dobi.walkingsynth.musicgeneration.core;

import android.util.Log;

import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsListener;
import com.dobi.walkingsynth.stepdetection.OnStepListener;

import java.util.ArrayList;
import java.util.List;

import static com.dobi.walkingsynth.stepdetection.AchartEngineAccelerometerGraph.TAG;

public class CsoundStepsAnalyzer implements StepsAnalyzer, OnStepListener {

    private static final int MAX_STEPS_COUNT = 10000;
    public static final int INITIAL_STEPS_INTERVAL = 10;

    private int mStepCount;
    private int mStepsInterval;
    private Integer[] stepIntervals;

    private List<StepsListener> mStepsListeners;

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
        return mStepsInterval;
    }

    @Override
    public int getStepsCount() {
        return mStepCount;
    }

    @Override
    public Integer[] getStepsIntervals() {
        return stepIntervals;
    }


    private void invalidateListeners() {
        if (mStepsListeners != null) {
            for (StepsListener listener : mStepsListeners) {
                listener.onStep(mStepCount);
            }
        }
    }

    public CsoundStepsAnalyzer(int steps) {
        mStepsInterval = steps;
        loadDefaultStepIntervals();
    }

    private void loadDefaultStepIntervals() {
        stepIntervals = new Integer[] {INITIAL_STEPS_INTERVAL, 20, 30, 50, 100 };
    }

    @Override
    public void onStepDetected(long milliseconds) {
        Log.d(TAG, "onStepDetected(): " + milliseconds);
        mStepCount = (mStepCount + 1) % MAX_STEPS_COUNT;
        invalidateListeners();
    }
}
