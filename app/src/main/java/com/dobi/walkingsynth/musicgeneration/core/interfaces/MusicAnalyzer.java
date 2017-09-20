package com.dobi.walkingsynth.musicgeneration.core.interfaces;

import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;
import com.dobi.walkingsynth.stepdetection.OnStepListener;

public interface MusicAnalyzer extends OnStepListener {

    void setBaseNote(Note newNote);

    Note getBaseNote();

    void setScale(Scales newScale);

    Scales getScale();

    int getTempo();

    void addPositionListener(PositionListener listener);

}
