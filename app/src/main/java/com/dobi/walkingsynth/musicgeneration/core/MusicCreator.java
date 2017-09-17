package com.dobi.walkingsynth.musicgeneration.core;

public interface MusicCreator {

    void start();

    void destroy();

    int getCurrentTempo();

    void onStep( long milliseconds); // step count + time of current step occurence

    int getStepCount();

    void invalidateBaseNote(int pos); // base note has changed

    void invalidateScale(int position); // scale has been altered

    void invalidateStepInterval(int idx); // count for special music


}
