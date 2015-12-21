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

    private int mStepInterval = SynthesizerSequencer.stepIntervals[0];
    private int mStepCount = 0;

    public SynthesizerPlayer(CsoundObj csoundObj) {
        super(csoundObj);
    }

    private SynthesizerSequencer mSynthesizerSequencer = new SynthesizerSequencer();
    private int[] currentSequence = mSynthesizerSequencer.getRhythmScoreSequence(); // current sequence obtained from sequencer

    protected void invalidate(int pb, int bc, int es) {
        super.invalidate(pb, bc, es);
        if ((pb + 8) % 8 == 0)
            onBarCountChange(bc);
    }

    // This is called only when full bar has passed in time.
    private void onBarCountChange(int bc) {
        Log.d(TAG,"I am on beat:" + bc);
        playRhythmScoreSequence(bc);
    }

    // parsing the current sequence and playing it at right time.
    private void playRhythmScoreSequence(int bc) {
        if (bc % 4 == 0)
            playCsoundNote(currentSequence[0]);
        if ((bc + 1) % 4 == 0)
            playCsoundNote(currentSequence[1]);
        if ((bc + 2) % 4 == 0)
            playCsoundNote(currentSequence[2]);
        if ((bc + 3) % 4 == 0)
            playCsoundNote(currentSequence[3]);
    }

    protected void playCsoundNote(int notetoplay) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                // TODO
                String.format("i%d 0 ", notetoplay));
    }

    // invalidations:
    public void invaliateStep(int stepcount) {
        // change pattern when specific amount of steps has been made.
        if (stepcount % mStepInterval == 0)
            currentSequence = mSynthesizerSequencer.getRhythmScoreSequence();
    }
    public void invalidateBaseNote(int pos) {
        mSynthesizerSequencer.invdalidateBaseNote(pos);
    }
    public void invalidateScale(String scale) {
        mSynthesizerSequencer.invdalidateScale(scale);
    }
    public void invalidateStepInterval(int idx) {
        mStepInterval = SynthesizerSequencer.stepIntervals[idx];
    }
}
