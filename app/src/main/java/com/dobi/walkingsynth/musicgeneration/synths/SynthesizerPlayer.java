package com.dobi.walkingsynth.musicgeneration.synths;
import android.util.Log;

import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.core.BasePlayer;
import com.dobi.walkingsynth.musicgeneration.utils.BarListener;

import java.util.Locale;
import java.util.Random;

/**
 * Synthesizer player. Needs to now about the current key and scale and step and beat count.
 * Provides the SynthesizerSequencer with necessary information.
 */
public class SynthesizerPlayer extends BasePlayer implements BarListener {

    private static final String TAG = SynthesizerPlayer.class.getSimpleName();

    private final Locale mLocale;
    private int mCurrentBar;
    private int[] currentRhythmSequence;
    private Random mGenerator = new Random(System.currentTimeMillis());
    private SynthesizerSequencer mSynthesizerSequencer;

    public SynthesizerPlayer(CsoundObj csoundObj) {
        super(csoundObj);

        mLocale = Locale.getDefault();

        mSynthesizerSequencer = new SynthesizerSequencer();
        currentRhythmSequence = mSynthesizerSequencer.invalidateScore();
    }

    @Override
    public void invalidateBar(int bar) {
        mCurrentBar = bar;
    }

    @Override
    public void invalidatePosition(int position) {
        if ((position + 8) % 8 == 0)
            onBarCountChange(mCurrentBar);
    }

    private void onBarCountChange(int barCount) {
        Log.d(TAG, "onBarCountChange(): " + barCount);
        playRhythmScoreSequence(barCount);
    }

    /**
        Parsing the current sequence and playing it at right time.
     */
    private void playRhythmScoreSequence(int bc) {
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

    public void invalidateStep(int steps) {
        // change pattern when specific amount of steps has been made
        if (steps % mStepInterval == 0) {
            Log.d(TAG,"Walked steps threshold. New rhythm score and playing some fancy stuff.");
            currentRhythmSequence = mSynthesizerSequencer.invalidateScore();
            playCsoundArpNotes();
        }
    }
    public void invalidateBaseNote(int pos) {
        mSynthesizerSequencer.invdalidateBaseNote(pos);
        currentRhythmSequence = mSynthesizerSequencer.invalidateScore();
    }
    public void invalidateScale(int position) {
        mSynthesizerSequencer.invdalidateScale(position);
        currentRhythmSequence = mSynthesizerSequencer.invalidateScore();
    }


}
