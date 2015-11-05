package com.dobi.walkingsynth.music;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Creating drum sequences and dynamic pattern modifications.
 */
public class DrumsSequencer {

    private static final String TAG = DrumsSequencer.class.getSimpleName();


    private ArrayList<Pair<Integer,Integer>> mSequences = new ArrayList<>(DrumsTypes.count);

    public DrumsSequencer() {
        initSequence();
    }

    private void initSequence() {
        mSequences.add(0, Pair.create(136, 136));
        mSequences.add(1, Pair.create(0, 128));
        mSequences.add(2, Pair.create(128, 0));
        Log.d(TAG,"Count is: " + mSequences.size());
    }

    public Pair<Integer,Integer> getSequence(int instr) {
        return mSequences.get(instr - 1);
    }

    public ArrayList<Pair<Integer,Integer>> getSequences() {
        return mSequences;
    }
}
