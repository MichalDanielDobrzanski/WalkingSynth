package com.dobi.walkingsynth.music;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creating drum sequences and dynamic pattern modifications.
 * "When to play what"
 */
public class DrumsSequencer {

    private static final String TAG = DrumsSequencer.class.getSimpleName();

    private Random mRandom = new Random();

    // a sequence to be played:
    private ArrayList<Integer> mSequences = new ArrayList<>(DrumsTypes.count);

    /**
     * Container for storing drum instruments parameters.
     */
    private float[][] mParams = new float[DrumsTypes.count][2];

    public DrumsSequencer() {
        initSequence();
        // initial parametrs
        // hi hat:
        mParams[0][0] = 0.11f;
        mParams[0][1] = 0.2f;
        // snare:
        mParams[1][0] = 1f;
        mParams[1][1] = 0.5f;
        // kick:
        mParams[2][0] = 60f;
        mParams[2][1] = 0.5f;
    }

    private void initSequence() {
        mSequences.add(0, 170);
        mSequences.add(1, 8);
        mSequences.add(2, 128);
        Log.d(TAG, "Count is: " + mSequences.size());
    }

    public ArrayList<Integer> getSequences() {
        return mSequences;
    }

    public float[] getParametrs(int instr) {
        return mParams[instr - 1];
    }

    public void randHiHat() {
        // when time is small draw little hi hats
        // when time is big draw frequent hi hats
        mSequences.set(0,mRandom.nextInt(256));
    }

    private int randKick() {
        return 0;
    }

    private int randSnare() {
        return 0;
    }
}
