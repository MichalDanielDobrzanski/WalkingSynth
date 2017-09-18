package com.dobi.walkingsynth.musicgeneration.core;

import android.util.Log;

import com.dobi.walkingsynth.musicgeneration.utils.PositionAndStepListener;

import java.util.ArrayList;
import java.util.List;

public class MusicAnalyzer {

    private static final String TAG = MusicAnalyzer.class.getSimpleName();

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 120;
    private static final int MAX_TEMPO_DIFF = 40;
    private static final int MAX_STEPS_COUNT = 10000;

    /**
     * How many divisions do for a single bar.
     * 8 corresponds to quarter notes.
    */
    private static final int BAR_INTERVALS = 8;

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
    private int mPosition;
    private int mStepCount;
    private boolean isPlaying = true;
    private long mInterPositionsInterval;


    private List<PositionAndStepListener> mPositionListeners;


    void addPositionListener(PositionAndStepListener listener) {
        if (mPositionListeners == null)
            mPositionListeners = new ArrayList<>();
        mPositionListeners.add(listener);
    }


    public MusicAnalyzer() {
        mPosition = 0;
        mInterPositionsInterval = calculatePositionsInterval();

        Thread analyzerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (isPlaying) {
                        sleep(mInterPositionsInterval);

                        mPosition = (mPosition + 1) % BAR_INTERVALS;
                        invalidateListeners();
                        Log.d(TAG, "Bar position: " + mPosition + " Sleep for interval: " + mInterPositionsInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        analyzerThread.start();
    }

    /**
     * Calculate next time interval based on tempo. When (in time) should I invalidate another note.
     * 60 / tempo = seconds between beats
     * (60 / tempo) * 1000 = milliseconds between beats
     *
     *  Basically, do this every 250ms.
     *  Its nice and enough.
     *
     *
     * @return time distance to the next moment.
     */
    private long calculatePositionsInterval() {
        final long positions =  (long)((60 / (float)mTempo) * 1000 ) / BAR_INTERVALS * 2;
        Log.d(TAG, "next Moment: " + positions);
        return positions;
    }


    private void invalidateListeners() {
        if (mPositionListeners != null) {
            for (PositionAndStepListener listener : mPositionListeners) {
                listener.invalidate(mPosition, mStepCount);
            }
        }
    }

    public void onStep(long milliseconds) {
        Log.d(TAG, "onStep(): " + milliseconds);
        mStepCount = (mStepCount + 1) % MAX_STEPS_COUNT;
        if (validateAndCalculateTempo(milliseconds)) {
            mInterPositionsInterval = calculatePositionsInterval();
        }
    }

    private boolean validateAndCalculateTempo(long stepTime) {
        int tempo = calculateTempo(stepTime);
        return validateTempo(tempo);
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
     */
    private int calculateTempo(long stepTime) {
        final int newTempo =  (int)(1 / ((float)(stepTime - mLastStepTime) / (1000 * 60)));
        mLastStepTime = stepTime;
        return newTempo;
    }

    private boolean validateTempo(int tempo) {
        if (Math.abs(tempo - mTempo) < MAX_TEMPO_DIFF &&
                tempo >= MIN_TEMPO &&
                tempo <= MAX_TEMPO) {
            mTempo = tempo;
            Log.d(TAG, "Tempo is valid; value: " + mTempo + "bpm");
            return true;
        }
        return false;
    }


    public int getTempo() {
        return mTempo;
    }

    public int getStepsCount() {
        return mStepCount;
    }
}
