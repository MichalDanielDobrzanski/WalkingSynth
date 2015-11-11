package com.dobi.walkingsynth.music;

import com.csounds.CsoundObj;

/**
 * Created by dobi on 11.11.15.
 */
public class BasePlayer {

    protected int mPositionInBar;
    protected long mSongElapsed = 0;
    protected CsoundObj mCsoundObj;

    protected BasePlayer(CsoundObj csoundObj) {
        mCsoundObj = csoundObj;
    }

    protected void invalidate(int pb, int es) {
        // update time position:
        mPositionInBar = pb;
        mSongElapsed = es;
    };

}
