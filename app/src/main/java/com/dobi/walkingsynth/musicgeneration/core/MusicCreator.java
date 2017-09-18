package com.dobi.walkingsynth.musicgeneration.core;

import com.dobi.walkingsynth.musicgeneration.utils.Notes;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

public interface MusicCreator {

    void start();

    void destroy();

    int getCurrentTempo();

    void onStep( long milliseconds); // step count + time of current step occurence

    int getStepCount();

    void invalidateBaseNote(Notes note); // base note has changed

    void invalidateScale(Scales position); // scale has been altered

    void invalidateStep(int count); // count for special music


}
