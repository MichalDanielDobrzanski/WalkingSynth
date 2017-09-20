package com.dobi.walkingsynth.musicgeneration.synths;

import android.util.Log;

import com.dobi.walkingsynth.musicgeneration.utils.Notes;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

import java.util.Random;

public class SynthesizerSequencer {

    private static final String TAG = SynthesizerSequencer.class.getSimpleName();

    private Notes mBaseNote;
    private Scales mScale;
    private int[] mRhythmScoreSequence;
    private Random mGenerator;

    public SynthesizerSequencer(Notes note, Scales scale) {
        mBaseNote = note;
        mScale = scale;
        mRhythmScoreSequence = new int[4];
        mGenerator = new Random();
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
