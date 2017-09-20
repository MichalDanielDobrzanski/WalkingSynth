package com.dobi.walkingsynth.musicgeneration.core;

import com.dobi.walkingsynth.musicgeneration.utils.Notes;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

public interface MusicAnalyzer {

    void setBaseNote(Notes newNote);

    Notes getCurrentBaseNote();

    void setScale(Scales newScale);

    Scales getCurrentScale();

    void setStepsInterval(int newStepsInterval);

    int getCurrentStepsInterval();

    int getCurrentTempo();

    int getCurrentStepsCount();

    Integer[] getStepsIntervals();

}
