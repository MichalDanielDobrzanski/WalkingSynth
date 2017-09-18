package com.dobi.walkingsynth.musicgeneration.core;


import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.utils.Notes;
import com.dobi.walkingsynth.musicgeneration.utils.PositionAndStepListener;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

/**
 * Base player functionality for csound
 */
public abstract class BasePlayer implements PositionAndStepListener {

    private Notes mNote;
    private Scales mScale;
    private int mStepInterval;
    protected CsoundObj mCsoundObj;

    protected BasePlayer(CsoundObj csoundObj, int steps) {
        mCsoundObj = csoundObj;
        mStepInterval = steps;
    }

    public void setStepInterval(int step) {
        mStepInterval = step;
    }

    public int getStepInterval() {
        return mStepInterval;
    }

    public void setNote(Notes mNote) {
        this.mNote = mNote;
    }

    public Notes getNote() {
        return mNote;
    }

    public void setScale(Scales mScale) {
        this.mScale = mScale;
    }

    public Scales getScale() {
        return mScale;
    }


}
