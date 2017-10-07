package com.dobi.walkingsynth.musicgeneration.core;


import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.PositionListener;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsListener;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;

/**
 * Base player functionality for csound
 */
public abstract class CsoundPlayer implements PositionListener, StepsListener {

    private Note mBaseNote;
    private Scale mScale;
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

    public void setScale(Scale mScale) {
        this.mScale = mScale;
    }

    public Scale getScale() {
        return mScale;
    }


}
