package com.dobi.walkingsynth.musicgeneration.core;

import android.util.Log;

import com.dobi.walkingsynth.musicgeneration.utils.BarListener;
import com.dobi.walkingsynth.musicgeneration.utils.DistanceListener;
import com.dobi.walkingsynth.musicgeneration.utils.PositionListener;

import java.util.ArrayList;
import java.util.List;

class MusicAnalyzer {

    private static final String TAG = MusicAnalyzer.class.getSimpleName();

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 120;
    private static final int MAX_TEMPO_DIFF = 40;

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
    private int mBarPosition;
    private int mBarCount;

    private boolean isPlaying = true;
    /**
     * time interval between two positions in a bar.
     */
    private long mInterPositionsInterval;

    private List<PositionListener> mPositionListeners;

    void addPositionListener(PositionListener listener) {
        if (mPositionListeners == null)
            mPositionListeners = new ArrayList<>();
        mPositionListeners.add(listener);
    }

    private List<BarListener> mBarListeners;

    public void addBarListener(BarListener listener) {
        if (mBarListeners == null)
            mBarListeners = new ArrayList<>();
        mBarListeners.add(listener);
    }

    private List<DistanceListener> mDistanceListeners;

    public void addDistanceListener(DistanceListener listener) {
        if (mDistanceListeners == null)
            mDistanceListeners = new ArrayList<>();
        mDistanceListeners.add(listener);
    }

    MusicAnalyzer() {
        mBarCount = 0;
        mBarPosition = 0;
        mInterPositionsInterval = calculatePositionsInterval();

        Thread analyzerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (isPlaying) {
                        sleep(mInterPositionsInterval);

                        mBarPosition = (mBarPosition + 1) % BAR_INTERVALS;
                        if (mBarPosition == 0) {
                            ++mBarCount;
                        }

                        invalidateListeners();
                        Log.d(TAG, "Bar position: " + mBarPosition + " Sleep for interval: " + mInterPositionsInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        analyzerThread.start();
    }

    /**
     * Calculate next time interval based on tempo. When (in time) should I invalidatePosition another note.
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
            for (PositionListener listener : mPositionListeners) {
                listener.invalidatePosition(mBarPosition);
            }
        }
        if (mBarListeners != null) {
            for (BarListener listener : mBarListeners) {
                listener.invalidateBar(mBarPosition);
            }
        }
        if (mDistanceListeners != null) {
            for (DistanceListener listener : mDistanceListeners) {
                listener.invalidateDistance(mInterPositionsInterval);
            }
        }
    }

    public void onStep(long milliseconds) {
        Log.d(TAG, "onStep(): " + milliseconds);

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


    int getTempo() {
        return mTempo;
    }

}
