package com.dobi.walkingsynth.music;

import android.content.res.Resources;
import android.util.Log;

import java.io.File;

/**
 * Anaylizing tempo and other parameters.
 */
public class MusicAnalyzer extends CsoundBaseSetup {

    private static final String TAG = CsoundBaseSetup.class.getSimpleName();

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 240;
    private int mStepCount = 0;
    private long mLastEventTime = 0;
    private int mTempo = MIN_TEMPO;

    public MusicAnalyzer(Resources res, File cDir) {
        super(res, cDir);
    }

    /**
     * Called when step has been detected.
     * @param eventMsecTime current event time in milliseconds.
     */
    public void onStep(long eventMsecTime) {
        Log.d(TAG, "onStep");
        ++mStepCount;
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
        // process tempo
        if (tempo < MIN_TEMPO) {
            mTempo = MIN_TEMPO;
        } else if (tempo > MAX_TEMPO) {
            mTempo = MAX_TEMPO;
        } else {
            mTempo = tempo;
        }
        Log.d(TAG, "Tempo: " + mTempo + "bpm.");
        mLastEventTime = eventTime;
    }

    public int getTempo() {
        return mTempo;
    }
}
