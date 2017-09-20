package com.dobi.walkingsynth.musicgeneration.core;


import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.PositionListener;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsListener;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

/**
 * Base player functionality for csound
 */
public abstract class CsoundPlayer implements PositionListener, StepsListener {

    private Note mBaseNote;
    private Scales mScale;
    private int mStepInterval;
    protected CsoundObj mCsoundObj;

    protected CsoundPlayer(CsoundObj csoundObj, int steps) {
        mCsoundObj = csoundObj;
        mStepInterval = steps;
    }

    public void setStepInterval(int step) {
        mStepInterval = step;
    }

    public int getStepInterval() {
        return mStepInterval;
    }

    public void setBaseNote(Note mNote) {
        this.mBaseNote = mNote;
    }

    public Note getBaseNote() {
        return mBaseNote;
    }

    public void setScale(Scales mScale) {
        this.mScale = mScale;
    }

    public Scales getScale() {
        return mScale;
    }


}
