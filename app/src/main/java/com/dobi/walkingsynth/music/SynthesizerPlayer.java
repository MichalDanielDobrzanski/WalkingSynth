package com.dobi.walkingsynth.music;
import android.util.Log;

import com.csounds.CsoundObj;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Synthesizer player. Needs to now about the current key and scale and step and beat count.
 * Provides the SynthesizerSequencer with necessary information.
 */
public class SynthesizerPlayer extends BasePlayer {

    private static final String TAG = SynthesizerPlayer.class.getSimpleName();

    /**
     * This flag id for checking whether to invalidate the note on specific time interval
     */
    private int bitFlag = 128;

    public SynthesizerPlayer(CsoundObj csoundObj) {
        super(csoundObj);
    }

    private SynthesizerSequencer mSynthesizerSequencer = new SynthesizerSequencer();

    protected void invalidate(int pb, int bc, int es) {
        super.invalidate(pb, bc, es);
        mSynthesizerSequencer.invdalidateBeatCount(bc);
        Log.d(TAG,"I am on beat:" + bc);
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

    public void invaliateStep(int nval)
    {
        mSynthesizerSequencer.invdalidateStepCount(nval);
        Log.d(TAG, "My step count: " + nval);
    }

    public void invalidateBaseNote(int pos)
    {
        mSynthesizerSequencer.invdalidateBaseNote(pos);
        Log.d(TAG, "Current base note:" + pos);
    }

    public void invalidateScale(int idx) {
        mSynthesizerSequencer.invdalidateScale(idx);
    }
}
