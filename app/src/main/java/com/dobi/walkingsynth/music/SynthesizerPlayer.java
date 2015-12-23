package com.dobi.walkingsynth.music;
import android.util.Log;

import com.csounds.CsoundObj;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Synthesizer player. Needs to now about the current key and scale and step and beat count.
 * Provides the SynthesizerSequencer with necessary information.
 */
public class SynthesizerPlayer extends BasePlayer {

    private static final String TAG = SynthesizerPlayer.class.getSimpleName();

    private int mStepCount = 0;
    private long mPositionTimeInterval;
    private Random generator = new Random();

    public SynthesizerPlayer(CsoundObj csoundObj) {
        super(csoundObj);
    }

    private SynthesizerSequencer mSynthesizerSequencer = new SynthesizerSequencer();

    private int[] currentRhythmSequence = mSynthesizerSequencer.getRhythmScoreSequence(); // current sequence obtained from sequencer

    protected void invalidate(int pb, int bc, long posti) {
        //Log.d(TAG,"Bar position: " + pb);
        //Log.d(TAG,"Sleep for: " + posti);
        mPositionTimeInterval = posti;
        if ((pb + 8) % 8 == 0)
            onBarCountChange(bc);
    }

    // This is called only when full bar has passed in time.
    private void onBarCountChange(int bc) {
        Log.d(TAG, "I am on beat:" + bc + ". Playing rhythm sequence.");
        playRhythmScoreSequence(bc);
    }

    // parsing the current sequence and playing it at right time.
    private void playRhythmScoreSequence(int bc) {
        if (bc % 4 == 0) {
            playCsoundRhythmNotes(currentRhythmSequence[0]);
            playCsoundLeadNote(currentRhythmSequence[0]);
        }
        if ((bc + 1) % 4 == 0) {
            playCsoundRhythmNotes(currentRhythmSequence[1]);
            playCsoundLeadNote(currentRhythmSequence[1]);
        }
        if ((bc + 2) % 4 == 0) {
            playCsoundRhythmNotes(currentRhythmSequence[2]);
            playCsoundLeadNote(currentRhythmSequence[2]);
        }
        if ((bc + 3) % 4 == 0) {
            playCsoundRhythmNotes(currentRhythmSequence[3]);
            playCsoundLeadNote(currentRhythmSequence[3]);
        }
    }

    private void playCsoundRhythmNotes(int notetoplay) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                // instrument number | amplitude | note to play
                String.format("i%d 0 1 0.%d 6.%02d", 5, 5,notetoplay));
    }

    private void playCsoundLeadNote(int notetoplay) {
        // instrument number | amplitude | note to play | mod_index | mod_factor
        mCsoundObj.sendScore(String.format("i%d 0 0.1 0.%d 8.%02d %d %d 0.6 0.8 0.6 0.1",
                4, 4,notetoplay,generator.nextInt(5),generator.nextInt(5)));
    }

    private void playCsoundArpNotes() {
        // when to start | note to play | mod_index | mod_factor
        mCsoundObj.sendScore(String.format("i4 0.%d 0.11 0.42 9.%02d %d %d 0.2 0.8 0.3 0.1",
                0,currentRhythmSequence[0],generator.nextInt(5),generator.nextInt(5)));
        mCsoundObj.sendScore(String.format("i4 0.%d 0.15 0.41 9.%02d %d %d 0.2 0.8 0.3 0.1",
                2,currentRhythmSequence[1],generator.nextInt(5),generator.nextInt(5)));
        mCsoundObj.sendScore(String.format("i4 0.%d 0.13 0.42 9.%02d %d %d 0.2 0.8 0.3 0.1",
                3,currentRhythmSequence[2],generator.nextInt(5),generator.nextInt(5)));
        mCsoundObj.sendScore(String.format("i4 0.%d 0.12 0.35 9.%02d %d %d 0.2 0.8 0.3 0.1",
                4,currentRhythmSequence[3],generator.nextInt(5),generator.nextInt(5)));
    }

    // invalidations:
    public void invaliateStep(int stepcount) {
        // change pattern when specific amount of steps has been made.
        if (stepcount % mStepInterval == 0) {
            Log.d(TAG,"Walked steps threshold. New rhythm score and playing some fancy stuff.");
            currentRhythmSequence = mSynthesizerSequencer.getRhythmScoreSequence();
            playCsoundArpNotes();
        }
    }
    public void invalidateBaseNote(int pos) {
        mSynthesizerSequencer.invdalidateBaseNote(pos);
        currentRhythmSequence = mSynthesizerSequencer.getRhythmScoreSequence();
    }
    public void invalidateScale(String scale) {
        mSynthesizerSequencer.invdalidateScale(scale);
        currentRhythmSequence = mSynthesizerSequencer.getRhythmScoreSequence();
    }
//    public void invalidateStepInterval(int idx) {
//        mStepInterval = SynthesizerSequencer.stepIntervals[idx];
//    }
}
