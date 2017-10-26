package com.dobi.walkingsynth.stepdetection.graph;

import android.content.Context;
import android.view.View;

import com.dobi.walkingsynth.stepdetection.OnThresholdChangeListener;

public interface AccelerometerGraph extends OnThresholdChangeListener {

    void invalidate(long time, double accelerometerValue, double threshold);

    void resume();

    void pause();

    View createView(Context context);
}

