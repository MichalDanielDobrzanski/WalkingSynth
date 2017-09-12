package com.dobi.walkingsynth.musicgeneration.core;


import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.utils.PositionListener;

/**
 * Base player functionality for both csound_part and synths.
 */
public abstract class BasePlayer implements PositionListener {

    protected CsoundObj mCsoundObj;

    protected int mStepInterval = SynthesizerSequencer.stepIntervals[0];

    protected BasePlayer(CsoundObj csoundObj) {
        mCsoundObj = csoundObj;
    }

    public void invalidateStepInterval(int idx) {
        mStepInterval = SynthesizerSequencer.stepIntervals[idx];
    }
}
