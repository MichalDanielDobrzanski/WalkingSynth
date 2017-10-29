package com.dobi.walkingsynth.musicgeneration.core.interfaces;

import com.dobi.walkingsynth.stepdetection.OnStepListener;

public interface MusicAnalyzer extends OnStepListener {

    void addPositionListener(PositionListener listener);

}
