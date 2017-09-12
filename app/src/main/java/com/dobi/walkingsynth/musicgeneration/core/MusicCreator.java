package com.dobi.walkingsynth.musicgeneration.core;

public interface MusicCreator {

    void start();

    void destroy();

    int getTempo();

    void onStep(int stepCount, long milliseconds); // step count + time of current step occurence

    void invalidateBaseNote(int pos); // base note has changed

    void invalidateScale(String scale); // scale has been altered

    void invalidateStepInterval(int idx); // count for special music


}
