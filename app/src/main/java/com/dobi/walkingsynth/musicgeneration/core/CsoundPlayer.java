package com.dobi.walkingsynth.musicgeneration.core;


import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;

/**
 * Base player functionality for csound
 */
public abstract class CsoundPlayer implements TempoAnalyzer.PositionListener, StepsAnalyzer.StepsListener {

    protected Note note;

    protected Scale scale;

    protected int interval;

    protected CsoundObj csoundObj;

    protected CsoundPlayer(CsoundObj csoundObj, int interval) {
        this.csoundObj = csoundObj;
        this.interval = interval;
    }

    public abstract void invalidateNote(Note note);

    public abstract void invalidateScale(Scale scale);

    public abstract void invalidateInterval(int interval);

}
