package com.dobi.walkingsynth.music;

import android.util.Log;

import java.util.Random;

/**
 * Sequencing melodies of a synth
 */
public class SynthesizerSequencer {

    private static final String TAG = SynthesizerSequencer.class.getSimpleName();

    /**
     * Based on http://www.phy.mtu.edu/~suits/notefreqs.html
     */
    public static double f0 = 261.63; // corresponds to C4
    private static String[] notes =
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

    private Random mRandom = new Random();

    public int randBaseNote() {
        // get one of 12 sounds in an octave
        final int bnote = mRandom.nextInt(12); // pseudo uniform
        Log.d(TAG, "Song will be in key of: " + notes[bnote]);
        return  bnote;
    }

    private double freqToPitch(double freq) {
        return 12 * Math.log(freq/f0) / Math.log(2);
    }


}
