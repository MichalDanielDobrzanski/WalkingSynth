package com.dobi.walkingsynth.csound;

/**
 * Creating music, anaylizing tempo.
 */
public class CsoundMusician {

    private static long[] mBeatTimes = new long[4];

    public static void addNewBeatTime(long bt) {
        mBeatTimes[0] = bt;
    }

    public CsoundMusician() {

    }
}
