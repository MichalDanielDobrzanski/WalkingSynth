package com.dobi.walkingsynth.music;

import android.util.Log;

/**
 * Anaylizing tempo and other parameters.
 */
public class MusicAnalyzer {

    private static final String TAG = MusicAnalyzer.class.getSimpleName();

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 240;
    private static final int MAX_TEMPO_DIFF = 40;
    private static final int BAR_SPACING = 8;

    /**
     * Tempo variable initialized to MIN_TEMPO
     */
    private int mTempo = MIN_TEMPO;

    /**
     * To calculate the current tempo value.
     */
    private long mLastStepTime = 0;

    /**
     * The current position in a bar ( 0-indexed )
     * 0 1 2 3 4 5 6 7
     * _ _ _ _ _ _ _ _
     */
    private int positionInBar = 0;

    private boolean isPlaying = true;
    /**
     * time interval between two positions in a bar.
     */
    private long mPositionInterval;

    /**
     * The counter of bars.
     */
    private int barCount = 0;

    private OnBasicIntervalListener mIntervalListener;

    public void setBasicIntervalListener(OnBasicIntervalListener listener) {
        mIntervalListener = listener;
    }

    public MusicAnalyzer() {
        mIntervalListener = null;
        mPositionInterval = calcMoment(BAR_SPACING);
        Thread hHatThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (isPlaying) {
                        sleep(mPositionInterval);
                        if (positionInBar == 0)
                            ++barCount;
                        // notify potential listeners
                        if (mIntervalListener != null)
                            mIntervalListener.onBasicInterval(positionInBar,barCount);
                        //playSequence();
                        Log.d(TAG, positionInBar + ", " + barCount + " Sleep: " + mPositionInterval);
                        positionInBar = (positionInBar + 1) % BAR_SPACING;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        hHatThread.start();
        Log.d(TAG,"I was created.");
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
        Log.d(TAG, "next Moment: " + nextMoment);
        return nextMoment;
    }

    /**
     * Called when step has been detected.
     * @param stepTime current step time in milliseconds.
     */
    public void onStep(long stepTime) {
        Log.d(TAG, "onStep");
        final boolean speeding = calculateTempo(stepTime);
        mPositionInterval = calcMoment(BAR_SPACING);
        positionInBar = 0;
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
     * @param stepTime Current time of event to be processed.
     * @return true if tempo is higher (speeding up)
     */
    public boolean calculateTempo(long stepTime) {
        final int tempo =  (int)(1 / ((float)(stepTime - mLastStepTime) / (1000 * 60)));
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
        mLastStepTime = stepTime;
        return tempoDiff < 0;
    }

    public int getTempo() {
        return mTempo;
    }

}
