package com.dobi.walkingsynth.music;

import android.util.Log;

import com.csounds.CsoundObj;

/**
 * Base player functionality
 */
public class BasePlayer {

    protected static int BIT_FLAG_VALUE = 128;
    /**
     * This flag id for checking whether to invalidate the note on specific time interval
     */
    protected int bitFlag;
    protected int mBeatPosition;
    protected long mSongElapsed = 0;
    protected CsoundObj mCsoundObj;

    protected BasePlayer(CsoundObj csoundObj) {
        mCsoundObj = csoundObj;
        bitFlag = BIT_FLAG_VALUE;
    }

    protected void invalidate(int pb, int es) {
        // update time position:
        mBeatPosition = pb;
        mSongElapsed = es;
    };

    /**
     * This functions moves the flag to the left.
     * The flag has 8 positions.
     *      1 0 0 0 0 0 0 0
     * then 0 1 0 0 0 0 0 0
     * then 0 0 1 0 0 0 0 0
     * then 0 0 0 1 0 0 0 0
     * etc...
     * When reached zero, it is set to
     *      1 0 0 0 0 0 0 0
     */
    protected void moveFlag() {
        bitFlag = bitFlag >> 1;
        if (bitFlag == 0)
            bitFlag = BIT_FLAG_VALUE;
    }

    protected void setBitFlag(int value) {
        BIT_FLAG_VALUE = value;
    }

    protected void playCsoundNote(int instr) {
        // to be overriden in specific derived classes
    }

    /**
     * Parsing the sequence (as number) to the playback.
     * The number values are compared with the moving flag.
     * @param seq sequence of notes to hit (passed as a number)
     * @param instr selected instrument.
     */
    protected void playAt(int seq, int instr) {
        if ((seq & bitFlag) > 0) {
            // when comparison with the flag is successful do the csound playing!
            playCsoundNote(instr + 1);
            Log.d("BasePlayer", "I" + (instr + 1) +
                    " at " + mBeatPosition + " song: " + mSongElapsed);
        }
    }

}
