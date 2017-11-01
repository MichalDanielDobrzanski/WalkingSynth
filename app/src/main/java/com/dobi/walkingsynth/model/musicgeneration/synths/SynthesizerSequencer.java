package com.dobi.walkingsynth.model.musicgeneration.synths;

import android.util.Log;

import com.dobi.walkingsynth.model.musicgeneration.utils.Note;
import com.dobi.walkingsynth.model.musicgeneration.utils.Scale;

import java.util.Random;

public class SynthesizerSequencer {

    private static final String TAG = SynthesizerSequencer.class.getSimpleName();

    private Note mBaseNote;
    private Scale scale;
    private int[] mRhythmScoreSequence;
    private Random mGenerator;

    public SynthesizerSequencer(Note note, Scale scale) {
        mBaseNote = note;
        this.scale = scale;
        mRhythmScoreSequence = new int[4];
        mGenerator = new Random();
    }


    public int[] getRandomScore() {
        Integer[] intervals = scale.intervals;

        int scaleLength = intervals.length;

        mRhythmScoreSequence[0] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];
        mRhythmScoreSequence[1] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];
        mRhythmScoreSequence[2] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];
        mRhythmScoreSequence[3] = mBaseNote.ordinal() + intervals[mGenerator.nextInt(scaleLength)];

        Log.d(TAG, "New score: " + mRhythmScoreSequence[0] + ",  " + mRhythmScoreSequence[1] + ",  "
                + mRhythmScoreSequence[2] + ",  " + mRhythmScoreSequence[3]);
        return mRhythmScoreSequence;
    }

    public void setNote(Note note) {
        mBaseNote = note;
    }
}
