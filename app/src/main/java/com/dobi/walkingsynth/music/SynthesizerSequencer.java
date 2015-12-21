package com.dobi.walkingsynth.music;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Sequencing melodies of a synth.
 * "When to play what"
 */
public class SynthesizerSequencer {

    private static final String TAG = SynthesizerSequencer.class.getSimpleName();

    public static final int[] stepIntervals = new int[]
    {
        10, 20, 50,100
    };

    // hash map static initialization
    public static LinkedHashMap<String,Integer> notes = new LinkedHashMap<>();
    public static LinkedHashMap<String,int[]> scales = new LinkedHashMap<>();
    static {
        scales.put("Pentatonic",new int[]{ 0, 3, 5, 7, 10, 12 });
        scales.put("Flamenco",new int[]{ 0, 1, 4, 5, 7, 8, 10, 12 });

        notes.put("C",0);
        notes.put("C#",1);
        notes.put("D",2);
        notes.put("D#",3);
        notes.put("E",4);
        notes.put("F",5);
        notes.put("F#",6);
        notes.put("G",7);
        notes.put("G#",8);
        notes.put("A",9);
        notes.put("A#",10);
        notes.put("B",11);

    }

    //public static int[] pentatonic = { 0, 3, 5, 7, 10, 12 };
    //public static int[] flamenco = { 0, 1, 4, 5, 7, 8, 10, 12 };

    // dynamic variables for synthesizer playback:
    private int mBaseNote = 0;    // what is the current base note.
    private String mScale = null; // what scale is in current use.

    // randomized sequences:
    private int[] mRhythmScoreSequence = new int[4];
    private int[] mArpScoreSequence = new int[4];

    private Random generator = new Random();

    public int[] getRhythmScoreSequence() {
        int[] resultScale = scales.get(mScale);
        int scaleLength = resultScale.length;

        mRhythmScoreSequence[0] = mBaseNote;
        mRhythmScoreSequence[1] = resultScale[generator.nextInt(scaleLength)];
        mRhythmScoreSequence[2] = resultScale[generator.nextInt(scaleLength)];
        mRhythmScoreSequence[3] = resultScale[generator.nextInt(scaleLength)];
        Log.d(TAG,"Output sound sequence: " + mRhythmScoreSequence[0] + ",  " + mRhythmScoreSequence[1]+ ",  "
                + mRhythmScoreSequence[2] + ",  " + mRhythmScoreSequence[3]);
        return mRhythmScoreSequence;
    }

//    public int[] getArpScoreSequence() {
//
//    }

    // outer invalidations:
    public void invdalidateBaseNote(int val) {
        Log.d(TAG, "Current base note: " + val);
        this.mBaseNote = val;
    }
    public void invdalidateScale(String scale) {
        Log.d(TAG, "Current scale: " + scale);
        this.mScale = scale;
    }
}
