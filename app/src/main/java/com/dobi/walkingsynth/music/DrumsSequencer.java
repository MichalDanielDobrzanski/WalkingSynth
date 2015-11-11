package com.dobi.walkingsynth.music;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Creating drum sequences and dynamic pattern modifications.
 */
public class DrumsSequencer {

    private static final String TAG = DrumsSequencer.class.getSimpleName();


    private ArrayList<Integer> mSequences = new ArrayList<>(DrumsTypes.count);

    public DrumsSequencer() {
        initSequence();
    }

    private void initSequence() {
        mSequences.add(0, 170);
        mSequences.add(1, 8);
        mSequences.add(2, 128);
        Log.d(TAG,"Count is: " + mSequences.size());
    }

    public Integer getSequence(int instr) {
        return mSequences.get(instr - 1);
    }

    public ArrayList<Integer> getSequences() {
        return mSequences;
    }
}
