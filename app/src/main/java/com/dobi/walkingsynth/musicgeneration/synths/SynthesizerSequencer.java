package com.dobi.walkingsynth.musicgeneration.synths;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Random;

public class SynthesizerSequencer {

    private static final String TAG = SynthesizerSequencer.class.getSimpleName();

    public static Integer[] stepIntervals = new Integer[] { 20, 30, 50, 100 };

    public static LinkedHashMap<String, int[]> scales = new LinkedHashMap<>();

    public enum Notes {
        C("C"),
        CSharp("C#"),
        D("D"),
        DSharp("D#"),
        E("E"),
        F("F"),
        FSharp("F#"),
        G("G"),
        GSharp("G#"),
        A("A"),
        ASharp("A#"),
        B("B");

        private final String note;

        Notes(final String text) {
            this.note = text;
        }
    }

    public enum Scales {
        Pentatonic(new Integer[] {0, 3, 5, 7, 10, 12}),
        Flamenco(new Integer[] {0, 1, 4, 5, 7, 8, 10, 12});

        private final Integer[] intervals;

        Scales(Integer[] intervals) {
            this.intervals = intervals;
        }

    }

    private Notes mBaseNote;
    private Scales mScale;
    private int[] mRhythmScoreSequence;
    private Random generator;

    public SynthesizerSequencer() {
        mBaseNote = Notes.C;
        mScale = Scales.Pentatonic;
        mRhythmScoreSequence = new int[4];
        generator = new Random();

        loadDefaultStepIntervals();
    }

    private void loadDefaultStepIntervals() {
        stepIntervals = new Integer[] { 20, 30, 50, 100 };
    }

    public int[] invalidateScore() {
        Integer[] intervals = mScale.intervals;
        int scaleLength = intervals.length;

        mRhythmScoreSequence[0] = mBaseNote.ordinal() + intervals[generator.nextInt(scaleLength)];
        mRhythmScoreSequence[1] = mBaseNote.ordinal() + intervals[generator.nextInt(scaleLength)];
        mRhythmScoreSequence[2] = mBaseNote.ordinal() + intervals[generator.nextInt(scaleLength)];
        mRhythmScoreSequence[3] = mBaseNote.ordinal() + intervals[generator.nextInt(scaleLength)];

        Log.d(TAG, "New score: " + mRhythmScoreSequence[0] + ",  " + mRhythmScoreSequence[1]+ ",  "
                + mRhythmScoreSequence[2] + ",  " + mRhythmScoreSequence[3]);
        return mRhythmScoreSequence;
    }

    public void invdalidateBaseNote(int baseIndex) {
        Notes newNote = Notes.values()[baseIndex];
        Log.d(TAG, "New base note: " + newNote.note);
        mBaseNote = newNote;
    }
    public void invdalidateScale(int position) {
        Scales newScale = Scales.values()[position];
        Log.d(TAG, "New scale: " + newScale.toString());
        mScale = newScale;
    }
}
