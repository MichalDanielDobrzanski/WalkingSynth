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
    private static final int MAX_TEMPO_DIFF = 40;

    private int mStepCount = 0;
    private long mLastEventTime = 0;
    private int mTempo = MIN_TEMPO;
    private boolean isPlaying = true;
    private long mNextMoment;

    public MusicAnalyzer(Resources res, File cDir) {
        super(res, cDir);
//        mNextMoment = calcMoment(1);
//        Thread hHatThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (isPlaying) {
//                        sleep(mNextMoment);
//                        csoundObj.sendScore("i1 0 0.25 10");
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        hHatThread.start();
    }

    /**
     * Calculate next time moment. When should I play another note.
     * 60 / tempo = seconds between beats
     * (60 / tempo) * 1000 = milliseconds between beats
     * @param noteType 1 = Note, 2 = HalfNote, 4 = QuarterNote
     * @return time distance to the next moment.
     */
    private long calcMoment(int noteType) {
        final long nextMoment =  (long)((60 / (float)mTempo) * 1000 ) / noteType;
        Log.d(TAG,"next Moment: " + nextMoment);
        return nextMoment;
    }

    /**
     * Called when step has been detected.
     * @param eventMsecTime current event time in milliseconds.
     */
    public void onStep(long eventMsecTime) {
        Log.d(TAG, "onStep");
        ++mStepCount;
        calculateTempo(eventMsecTime);
        mNextMoment = calcMoment(1);
        csoundObj.sendScore("i2 0 1 10000");
        //csoundObj.sendScore("i3 0 0.50 70");
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
            if (Math.abs(tempo - mTempo) < MAX_TEMPO_DIFF)
                mTempo = tempo;
        }
        Log.d(TAG, "Tempo: " + mTempo + "bpm.");
        mLastEventTime = eventTime;
    }

    public int getTempo() {
        return mTempo;
    }
}
