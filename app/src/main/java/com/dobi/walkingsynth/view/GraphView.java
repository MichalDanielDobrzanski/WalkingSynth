package com.dobi.walkingsynth.view;

import android.content.Context;
import android.view.View;

public interface GraphView {

    void invalidate(long time, double accelerometerValue, double threshold);

    void resume();

    void pause();

    View createView(Context context);

    void onThreshold(double newValue);
}

