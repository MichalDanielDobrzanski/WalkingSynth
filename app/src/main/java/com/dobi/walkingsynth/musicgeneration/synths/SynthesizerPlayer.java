package com.dobi.walkingsynth.musicgeneration.synths;
import android.util.Log;

import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.core.CsoundPlayer;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;

import java.util.Locale;
import java.util.Random;

/**
 * Synthesizer player. Needs to now about the current key and scale and step and beat count.
 * Provides the SynthesizerSequencer with necessary information.
 */
public class SynthesizerPlayer extends CsoundPlayer {

    private static final String TAG = SynthesizerPlayer.class.getSimpleName();

    private final Locale mLocale;
    private int mLastStep;
    private int[] currentRhythmSequence;
    private Random mGenerator = new Random(System.currentTimeMillis());
    private SynthesizerSequencer mSynthesizerSequencer;

    public SynthesizerPlayer(CsoundObj csoundObj, int steps, SynthesizerSequencer synthesizerSequencer) {
        super(csoundObj, steps);
        mLocale = Locale.getDefault();
        mLastStep = 0;

        mSynthesizerSequencer = synthesizerSequencer;
        currentRhythmSequence = mSynthesizerSequencer.getRandomScore();
    }

    /**
        Parsing the current sequence and playing it at right time.
     */
    private void playRhythmScore(int bc) {
        if (bc % 4 == 0) {
            playCsoundRhythmNote(currentRhythmSequence[0]);
            playCsoundLeadNote(currentRhythmSequence[0]);
        }
        if ((bc + 1) % 4 == 0) {
            playCsoundRhythmNote(currentRhythmSequence[1]);
            playCsoundLeadNote(currentRhythmSequence[1]);
        }
        if ((bc + 2) % 4 == 0) {
            playCsoundRhythmNote(currentRhythmSequence[2]);
            playCsoundLeadNote(currentRhythmSequence[2]);
        }
        if ((bc + 3) % 4 == 0) {
            playCsoundRhythmNote(currentRhythmSequence[3]);
            playCsoundLeadNote(currentRhythmSequence[3]);
        }
    }

    /**
        instrument number | amplitude | note to play
     */
    private void playCsoundRhythmNote(int note) {
        mCsoundObj.sendScore(String.format(mLocale, "i%d 0 1 0.%d 6.%02d", 5, 5, note));
    }

    /**
     *  instrument number | amplitude | note to play | mod_index | mod_factor
     */
    private void playCsoundLeadNote(int note) {
        mCsoundObj.sendScore(String.format(mLocale, "i%d 0 0.1 0.%d 8.%02d %d %d 0.6 0.8 0.6 0.1",
                4, 4, note, mGenerator.nextInt(5), mGenerator.nextInt(5)));
    }

    /**
     *  when to startCSound | note to play | mod_index | mod_factor
     */
    private void playCsoundArpNotes() {
        mCsoundObj.sendScore(String.format(mLocale, "i4 0.%d 0.11 0.42 9.%02d %d %d 0.2 0.8 0.3 0.1",
                0, currentRhythmSequence[0], mGenerator.nextInt(5), mGenerator.nextInt(5)));
        mCsoundObj.sendScore(String.format(mLocale, "i4 0.%d 0.15 0.41 9.%02d %d %d 0.2 0.8 0.3 0.1",
                2, currentRhythmSequence[1], mGenerator.nextInt(5), mGenerator.nextInt(5)));
        mCsoundObj.sendScore(String.format(mLocale, "i4 0.%d 0.13 0.42 9.%02d %d %d 0.2 0.8 0.3 0.1",
                3, currentRhythmSequence[2], mGenerator.nextInt(5), mGenerator.nextInt(5)));
        mCsoundObj.sendScore(String.format(mLocale, "i4 0.%d 0.12 0.35 9.%02d %d %d 0.2 0.8 0.3 0.1",
                4, currentRhythmSequence[3], mGenerator.nextInt(5), mGenerator.nextInt(5)));
    }

    @Override
    public void setScale(Scale scale) {
        super.setScale(scale);
        mSynthesizerSequencer.setScale(scale);
        randomize();
    }

    @Override
    public void setBaseNote(Note note) {
        super.setBaseNote(note);
        mSynthesizerSequencer.setNote(note);
        randomize();
    }

    private void randomize() {
        currentRhythmSequence = mSynthesizerSequencer.getRandomScore();
    }


    @Override
    public void onStep(int step) {
        Log.d(TAG, "onStep(): step: " + step + " getStepInterval(): " + getStepInterval());
        if (step > 0 && mLastStep != step && step % getStepInterval() == 0) {
            Log.d(TAG, "Walked steps threshold. Playing random score.");
            currentRhythmSequence = mSynthesizerSequencer.getRandomScore();
            playCsoundArpNotes();
            mLastStep = step;
        }
    }

    @Override
    public void invalidate(int position) {
        Log.d(TAG, "invalidate(): " + position);
        if ((position + 8) % 8 == 0) {
            playRhythmScore(position);
        }
    }
}
