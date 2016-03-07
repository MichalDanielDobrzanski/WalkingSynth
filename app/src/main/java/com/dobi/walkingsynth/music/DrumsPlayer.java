package com.dobi.walkingsynth.music;

import android.util.Log;

import com.csounds.CsoundObj;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Drum and rhythm creating class.
 */
public class DrumsPlayer extends BasePlayer {

    private static final String TAG = DrumsPlayer.class.getSimpleName();

    protected static int BIT_FLAG_VALUE = 128;
    /**
     * This flag id for checking whether to invalidate the note on specific time interval
     */
    protected int bitFlag;

    private int mBarPosition;

    private DrumsSequencer mDrumsSequencer = new DrumsSequencer();

    public DrumsPlayer(CsoundObj csoundObj) {
        super(csoundObj);
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
    protected void moveFlag() {
        bitFlag = bitFlag >> 1;
        if (bitFlag == 0)
            bitFlag = BIT_FLAG_VALUE;
    }


    /**
     * Main looping function for reading the playSequence values.
     */
    public void invalidate(int pb, int bc, int es) {
        mBarPosition = pb;
        // read drum pattern from sequencer and play it:
        //mDrumsSequencer.setTime(es);
        playSequence(mDrumsSequencer.getSequences());
    }

    /**
     * Csound playback.
     * @param instr selected instrument.
     */
    protected void playCsoundNote(int instr) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                String.format("i%d 0 ", instr)
                        + df.format(mDrumsSequencer.getParametrs(instr)[0]) + " "
                        + df.format(mDrumsSequencer.getParametrs(instr)[1])
        );
    }

    /**
     * Reads the current pattern sequences and passes it to the csound playback.
     * seqences = hi-hat, snare, drum patterns
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
     * Parsing the sequence (as number) to the playback.
     * The number values are compared with the moving flag.
     * @param seq sequence of notes to hit (passed as a number)
     * @param instr selected instrument.
     */
    private void playAt(int seq, int instr) {
        if ((seq & bitFlag) > 0) {
            // when comparison with the flag is successful do the csound playing!
            playCsoundNote(instr + 1);
            Log.d("BasePlayer", "I" + (instr + 1) + " at " + mBarPosition);
        }
    }

    public void invaliateStep(int stepcount) {
        // change pattern when specific amount of steps has been made.
        if (stepcount % mStepInterval == 0) {
            Log.d(TAG,"Walked steps threshold. New csound_part rhythm score!");
            mDrumsSequencer.randHiHat();
        }
    }

}
