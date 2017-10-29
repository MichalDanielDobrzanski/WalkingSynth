package com.dobi.walkingsynth.model.musicgeneration.drums;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creating drum sequences and dynamic pattern modifications.
 */
public class DrumsSequencer {

    private static final String TAG = DrumsSequencer.class.getSimpleName();

    private enum DrumsTypes {
        HIHAT,
        SNARE,
        BASS_DRUM;

        public static int count = DrumsTypes.values().length;
    }

    private ArrayList<Integer> mDrumsSequences;
    private float[][] mDrumsParameters;

    private Random mRandom = new Random();

    public DrumsSequencer() {
        mDrumsSequences = new ArrayList<>(DrumsTypes.count);
        mDrumsParameters = new float[DrumsTypes.count][2];

        generateFirstSequence();

        // hi hat:
        mDrumsParameters[0][0] = 0.11f;
        mDrumsParameters[0][1] = 0.2f;

        // snare:
        mDrumsParameters[1][0] = 1f;
        mDrumsParameters[1][1] = 0.5f;

        // kick:
        mDrumsParameters[2][0] = 60f;
        mDrumsParameters[2][1] = 0.5f;
    }

    private void generateFirstSequence() {
        mDrumsSequences.add(170);
        mDrumsSequences.add(8);
        mDrumsSequences.add(128);
        Log.d(TAG, "Count is: " + mDrumsSequences.size());
    }

    ArrayList<Integer> getSequences() {
        return mDrumsSequences;
    }

    float[] getParameters(int instrument) {
        return mDrumsParameters[instrument - 1];
    }

    void randomizeHiHat() {
        mDrumsSequences.set(0,mRandom.nextInt(256));
    }
}
