package com.dobi.walkingsynth.music;

import android.content.res.Resources;
import android.util.Log;

import java.io.File;

/**
 * Anaylizing tempo and other parameters.
 */
public class MusicAnalyzer extends CsoundBaseSetup {

    private static final String TAG = MusicAnalyzer.class.getSimpleName();

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 240;
    private static final int MAX_TEMPO_DIFF = 40;
    private static final int BAR_SPACING = 8;

    private int mStepCount = 0;
    private long mLastEventTime = 0;
    private int mTempo = MIN_TEMPO;
    private boolean isPlaying = true;
    private long mNextMoment;

    /**
     * The current position in a bar ( 0-indexed )
     * 0 1 2 3 4 5 6 7
     * _ _ _ _ _ _ _ _
     */
    private int positionInBar = 0;

    /**
     * The count of bars.
     */
    private int barCount = 0;

    public MusicAnalyzer(Resources res, File cDir) {
        super(res, cDir);
        mNextMoment = calcMoment(BAR_SPACING);
        Thread hHatThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (isPlaying) {
                        sleep(mNextMoment);
                        playSequence();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        hHatThread.start();
        Log.d(TAG,"I was created.");
    }

    private void playSequence() {
        if (positionInBar == 0)
            ++barCount;
        if (positionInBar == 0 | positionInBar == 4)
            csoundObj.sendScore("i1 0 0.15 10");
        if (barCount % 2 == 0 & positionInBar == 0)
            csoundObj.sendScore("i3 0 0.50 70");
        if (barCount % 2 == 1 & positionInBar == 0)
            csoundObj.sendScore("i2 0 1 10000");
        Log.d(TAG,positionInBar + ", " + barCount + " Sleep: " + mNextMoment);
        positionInBar = (positionInBar + 1) % BAR_SPACING;
    }


    /**
     * Calculate next time moment. When should I play another note.
     * 60 / tempo = seconds between beats
     * (60 / tempo) * 1000 = milliseconds between beats
     * @param noteType 1 = Note, 2 = HalfNote, 4 = QuarterNote, 8 = EightNote
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
        final boolean speeding = calculateTempo(eventMsecTime);
        mNextMoment = calcMoment(BAR_SPACING);
        positionInBar = 0;
        //csoundObj.sendScore("i2 0 1 10000");
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
     * @return true if tempo is higher (speeding up)
     */
    public boolean calculateTempo(long eventTime) {
        final int tempo =  (int)(1 / ((float)(eventTime - mLastEventTime) / (1000 * 60)));
        final int tempoDiff = mTempo  - tempo;
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

        return tempoDiff < 0;
    }

    public int getTempo() {
        return mTempo;
    }
}
