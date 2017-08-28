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
        initSequence();

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

    private void initSequence() {
        mDrumsSequences.add(170);
        mDrumsSequences.add(8);
        mDrumsSequences.add(128);
        Log.d(TAG, "Count is: " + mDrumsSequences.size());
    }

    public ArrayList<Integer> getSequences() {
        return mDrumsSequences;
    }

    public float[] getParametrs(int instr) {
        return mDrumsParams[instr - 1];
    }

    public void randHiHat() {
        // when time is small draw little hi hats
        // when time is big draw frequent hi hats
        mDrumsSequences.set(0,mRandom.nextInt(256));
    }

    private int randKick() {
        return 0;
    }

    private int randSnare() {
        return 0;
    }
}
