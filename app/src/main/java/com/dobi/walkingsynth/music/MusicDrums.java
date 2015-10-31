package com.dobi.walkingsynth.music;

import com.csounds.CsoundObj;

/**
 * Drum and rhythm creating class.
 */
public class MusicDrums {

    private static final String TAG = MusicDrums.class.getSimpleName();

    private CsoundObj mCsoundObj;

    public MusicDrums(CsoundObj csoundObj) {
        mCsoundObj = csoundObj;
    }

    public void playSequence(int positionInBar, int barCount) {
        if (positionInBar == 0 | positionInBar == 4)
            mCsoundObj.sendScore("i1 0 0.15 10");
        if (barCount % 2 == 0 & positionInBar == 0)
            mCsoundObj.sendScore("i3 0 0.50 70");
        if (barCount % 2 == 1 & positionInBar == 0)
            mCsoundObj.sendScore("i2 0 1 10000");
    }

}
