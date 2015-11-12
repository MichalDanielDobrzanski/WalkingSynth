package com.dobi.walkingsynth.music;
import android.util.Log;

import com.csounds.CsoundObj;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Synthesizer playing
 */
public class SynthesizerPlayer extends BasePlayer {

    private static final String TAG = SynthesizerPlayer.class.getSimpleName();

    /**
     * This flag id for checking whether to invalidate the note on specific time interval
     */
    private int bitFlag = 128;
    private int mBeatCount = 0;
    private SynthesizerSequencer mSynthesizerSequencer = new SynthesizerSequencer();

    public SynthesizerPlayer(CsoundObj csoundObj) {
        super(csoundObj);
    }

    protected void invalidate(int pb, int bc, int es) {
        super.invalidate(pb, es);
        mBeatCount = bc;
    }

    protected void playCsoundNote(int instr) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                // TODO
                String.format("i%d 0 ", instr));
    }

    private void playSequence(ArrayList<Integer> sequences) {
        for (int i = 0; i < sequences.size(); ++i) {
            // reading sequences for every instrument
            playAt(sequences.get(i),i);
        }
        // move flag:
        moveFlag();
    }

    protected void playAt(int seq, int instr) {
        if ((seq & bitFlag) > 0) {
            // when comparison with the flag is successful do the csound playing!
            playCsoundNote(instr + 1);
            Log.d(TAG, "I" + (instr + 1) +
                    " at " + mBeatPosition + " song: " + mSongElapsed);
        }
    }
}
