package com.dobi.walkingsynth.music;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Sequencing melodies of a synth.
 * "When to play what"
 */
public class SynthesizerSequencer {

    private static final String TAG = SynthesizerSequencer.class.getSimpleName();

    public static final int[] stepIntervals = new int[]
    {
        10,
        20,
        50,
        100,
    };

    public static String[] notes =
    {
        "C",
        "C#",
        "D",
        "D#",
        "E",
        "F",
        "F#",
        "G",
        "G#",
        "A",
        "A#",
        "B"
    };

    public static int[] pentatonic = { 0, 3, 5, 7, 10, 12 };
    public static int[] flamenco = { 0, 1, 4, 5, 7, 8, 10, 12 };

    private int mBaseNote;
    private Random mRandom = new Random();
    private ArrayList<Integer> mScoreSequence = new ArrayList<>();

    public SynthesizerSequencer() {
        mBaseNote = randBaseNote();
    }


    /**
     * Draw a base note of a whole song.
     * @return base note.
     */
    public int randBaseNote() {
        // get one of 12 sounds in an octave
        final int bnote = mRandom.nextInt(12); // pseudo uniform
        Log.d(TAG, "Song will be in key of: " + notes[bnote]);
        return bnote;
    }

    public int getBaseNote() {
        return mBaseNote;
    }

}
