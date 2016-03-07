package com.dobi.walkingsynth.music;

import android.util.Log;


import com.csounds.CsoundObj;

/**
 * Base player functionality for both csound_part and synths.
 */
public class BasePlayer {

    protected CsoundObj mCsoundObj;

    protected int mStepInterval = SynthesizerSequencer.stepIntervals[0];

    protected BasePlayer(CsoundObj csoundObj) {
        mCsoundObj = csoundObj;
    }

    public void invalidateStepInterval(int idx) {
        mStepInterval = SynthesizerSequencer.stepIntervals[idx];
    }

}
