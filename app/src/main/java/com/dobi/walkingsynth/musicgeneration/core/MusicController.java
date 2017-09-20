package com.dobi.walkingsynth.musicgeneration.core;

public interface MusicController extends MusicAnalyzer {

    void start();

    void destroy();

    void onStep(long milliseconds);
}
