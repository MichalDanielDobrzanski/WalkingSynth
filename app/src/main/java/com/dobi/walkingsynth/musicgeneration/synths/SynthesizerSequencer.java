package com.dobi.walkingsynth.musicgeneration.synths;

import android.util.Log;

import com.dobi.walkingsynth.musicgeneration.utils.Notes;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

import java.util.Random;

public class SynthesizerSequencer {

    private static final String TAG = SynthesizerSequencer.class.getSimpleName();

    public static Integer[] stepIntervals = new Integer[] { 20, 30, 50, 100 };

    private Notes mBaseNote;
    private Scales mScale;
    private int[] mRhythmScoreSequence;
    private Random mGenerator;

    public SynthesizerSequencer(int note, String scale) {
        mBaseNote = Notes.values()[note];
        mScale = Scales.valueOf(scale);
        mRhythmScoreSequence = new int[4];
        mGenerator = new Random();

        loadDefaultStepIntervals();
    }

    private void loadDefaultStepIntervals() {
        stepIntervals = new Integer[] { 20, 30, 50, 100 };
    }

    public int[] getRandomScore() {
        Integer[] intervals = mScale.intervals;
        int scaleLength = intervals.length;

        mRhythmScoreSequence[0] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];
        mRhythmScoreSequence[1] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];
        mRhythmScoreSequence[2] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];
        mRhythmScoreSequence[3] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];

        Log.d(TAG, "New score: " + mRhythmScoreSequence[0] + ",  " + mRhythmScoreSequence[1] + ",  "
                + mRhythmScoreSequence[2] + ",  " + mRhythmScoreSequence[3]);
        return mRhythmScoreSequence;
    }

    public void setNote(Notes note) {
        mBaseNote = note;
    }

    public void invdalidateScale(int position) {
        Scales newScale = Scales.values()[position];
        Log.d(TAG, "New scale: " + newScale.toString());
        mScale = newScale;
    }

    public void setScale(Scales scale) {
        this.mScale = scale;
    }
}
