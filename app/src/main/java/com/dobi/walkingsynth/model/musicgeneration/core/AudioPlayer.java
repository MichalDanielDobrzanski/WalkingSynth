package com.dobi.walkingsynth.model.musicgeneration.core;

import com.dobi.walkingsynth.model.musicgeneration.utils.Note;
import com.dobi.walkingsynth.model.musicgeneration.utils.Scale;

public interface AudioPlayer {

    void start();

    void destroy();

    void initialize(Note note, Scale scale, int interval);

    void invalidate(Note note);

    void invalidate(Scale scale);

    void invalidate(int interval);

    TempoAnalyzer getTempoAnalyzer();

    StepsAnalyzer getStepsAnalyzer();
}
