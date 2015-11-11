package com.dobi.walkingsynth.music;

import android.util.Log;
import android.util.Pair;

import com.csounds.CsoundObj;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Drum and rhythm creating class.
 */
public class DrumsPlayer {

    private static final String TAG = DrumsPlayer.class.getSimpleName();

    private CsoundObj mCsoundObj;
    private int mPositionInBar;
    private DrumsSequencer mDrumsSequencer = new DrumsSequencer();

    /**
     * Container for storing instruments parameters.
     */
    private float[][] mParams = new float[DrumsTypes.count][2];

    /**
     * This flag id for checking whether to invalidate the note on specific time interval
     */
    private int bitFlag = 128;

    public DrumsPlayer(CsoundObj csoundObj) {
        mCsoundObj = csoundObj;
        // hi hat:
        mParams[0][0] = 0.15f;
        mParams[0][1] = 10;
        // snare:
        mParams[1][0] = 1;
        mParams[1][1] = 10000;
        // kick:
        mParams[2][0] = 0.50f;
        mParams[2][1] = 70;

    }

    /**
     * Song variables
     */
    private long mSongElapsed = 0;

    /**
     * Main looping function for reading the playSequence values.
     * @param positionInBar time position.
     */
    public void invalidate(int pb, long es) {
        // update time position:
        mPositionInBar = pb;
        mSongElapsed = es;
        // read drum pattern from sequencer and play it:
        playSequence(mDrumsSequencer.getSequences());

    }

    /**
     * Csound playback.
     * @param instr selected instrument.
     */
    private void playCsoundNote(int instr) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                String.format("i%d 0 ", instr)
                        + df.format(mParams[instr - 1][0]) + " "
                        + df.format(mParams[instr - 1][1])
        );
    }

    /**
     * Reads the current pattern sequences and passes it to csound playback.
     * @param sequences the pattern sequences.
     */
    private void playSequence(ArrayList<Integer> sequences) {
        for (int i = 0; i < sequences.size(); ++i) {
            // reading sequences for every instrument
            playAt(sequences.get(i),i);
        }
        // move flag:
        moveFlag();
    }

    /**
     * Parsing the number to the playSequence. The number values is compared with the flag.
     * @param seq playSequence (passed as a number)
     * @param instr selected instrument.
     */
    private void playAt(int seq, int instr) {
        if ((seq & bitFlag) > 0) {
            // when comparison with the flag is successful do the csound playing!
            playCsoundNote(instr + 1);
            Log.d(TAG, "I" + (instr + 1) +
                    " at " + mPositionInBar + " song: " + mSongElapsed);
        }
    }

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
    private void moveFlag() {
        bitFlag = bitFlag >> 1;
        if (bitFlag == 0)
            bitFlag = 128;
    }

}
