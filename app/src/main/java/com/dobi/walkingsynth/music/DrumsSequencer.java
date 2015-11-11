package com.dobi.walkingsynth.music;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Creating drum sequences and dynamic pattern modifications.
 */
public class DrumsSequencer {

    private static final String TAG = DrumsSequencer.class.getSimpleName();

    private int mTime = 0;
    private int mTimeDistance;
    private Random mRandom = new Random();
    private ArrayList<Integer> mSequences = new ArrayList<>(DrumsTypes.count);

    public DrumsSequencer() {
        initSequence();
        mTimeDistance = mRandom.nextInt(20) + 7;
    }

    private void initSequence() {
        mSequences.add(0, 170);
        mSequences.add(1, 8);
        mSequences.add(2, 128);
        Log.d(TAG, "Count is: " + mSequences.size());
    }

    public Integer getSequence(int instr) {
        return mSequences.get(instr - 1);
    }

    /**
     * On time interval listener handler.
     * @param t time.
     */
    public void setTime (int t) {
        mTime = t;

        if (t % mTimeDistance == 0) {
            // a need to change the beat
            mTimeDistance = mRandom.nextInt(mTimeDistance) + 7;
            // do the change:
            mSequences.set(0,randHiHat());
        }

    }

    public ArrayList<Integer> getSequences() {
        return mSequences;
    }

    private int randHiHat() {
        // when time is small draw little hi hats
        // when time is big draw frequent hi hats
        return mRandom.nextInt(256 - mTime) + mTime;
    }

    private int randKick() {
        return 0;
    }

    private int randSnare() {
        return 0;
    }
}
