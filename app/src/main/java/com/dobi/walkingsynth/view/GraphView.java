package com.dobi.walkingsynth.view;

import android.content.Context;
import android.view.View;

import com.dobi.walkingsynth.model.stepdetection.AccelerometerManager;

public interface GraphView extends AccelerometerManager.OnThresholdChangeListener {

    void invalidate(long time, double accelerometerValue, double threshold);

    void resume();

    void pause();

    View createView(Context context);
}

