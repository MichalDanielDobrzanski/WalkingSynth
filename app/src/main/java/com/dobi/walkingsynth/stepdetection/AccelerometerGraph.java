package com.dobi.walkingsynth.stepdetection;

import android.content.Context;
import android.view.View;

public interface AccelerometerGraph {

    void invalidate(long time);

    void reset();

    View createView(Context context);
}

