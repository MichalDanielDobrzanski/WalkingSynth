package com.dobi.walkingsynth.musicgeneration.core.interfaces;

public interface AudioController {

    void start();

    void destroy();

    MusicAnalyzer getMusicAnalyzer();

    StepsAnalyzer getStepsAnalyzer();
}
