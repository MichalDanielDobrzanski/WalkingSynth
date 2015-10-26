package com.dobi.walkingsynth.music;

import android.content.res.Resources;
import android.util.Log;

import java.io.File;

/**
 * Anaylizing tempo and other parameters
 */
public class MusicAnalyzer extends CsoundBaseSetup {

    private static final String TAG = CsoundBaseSetup.class.getSimpleName();

    private int mStepCount = 0;
    private long mLastEventTime = 0;
    private int mTempo = 0;

    public MusicAnalyzer(Resources res, File cDir) {
        super(res, cDir);
    }

    /**
     * Called when step has been detected.
     *
     * @param eventMsecTime current event time in milliseconds.
     */
    public void onStep(long eventMsecTime) {
        ++mStepCount;
        Log.d(TAG, "onStep");
        calculateTempo(eventMsecTime);
        csoundObj.sendScore(String.format(
                "i3 0 0.25 100", mStepCount));
    }

    /**
     * Tempo calculator based on two eventTimes.
     * <pre>{@code
     *
     * (t2 - t1) gives time difference between two events in milliseconds.
     * (t2 - t1) / 1000 in seconds.
     * (t2 - t1) / (1000 * 60) in minutes.
     * 1 / ((t2 - t1) / (1000 * 60)) gives bpm.
     * }</pre>
     * @param eventTime Current event to be processed.
     */
    public void calculateTempo(long eventTime) {
        final int tempo =  (int)(1 / ((float)(eventTime - mLastEventTime) / (1000 * 60)));
        Log.d(TAG, "Tempo: " + tempo + "bpm.");
        mLastEventTime = eventTime;
        mTempo = tempo;
    }
}
