package com.dobi.walkingsynth.music.drums;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creating drum sequences and dynamic pattern modifications.
 * "When to play what"
 */
public class DrumsSequencer {

    private static final String TAG = DrumsSequencer.class.getSimpleName();

    private enum DrumsTypes {
        BD,
        SN,
        HH;

        public static int count = DrumsTypes.values().length;
    }

    private ArrayList<Integer> mDrumsSequences = new ArrayList<>(DrumsTypes.count);
    private float[][] mDrumsParams = new float[DrumsTypes.count][2];

    private Random mRandom = new Random();

    DrumsSequencer() {
        generateFirstSequence();

        // hi hat:
        mDrumsParams[0][0] = 0.11f;
        mDrumsParams[0][1] = 0.2f;

        // snare:
        mDrumsParams[1][0] = 1f;
        mDrumsParams[1][1] = 0.5f;

        // kick:
        mDrumsParams[2][0] = 60f;
        mDrumsParams[2][1] = 0.5f;
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
        return mDrumsParams[instrument - 1];
    }

    void randomizeHiHat() {
        mDrumsSequences.set(0,mRandom.nextInt(256));
    }
}
