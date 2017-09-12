package com.dobi.walkingsynth.musicgeneration.core;

import android.util.Log;

import com.dobi.walkingsynth.musicgeneration.utils.BarListener;
import com.dobi.walkingsynth.musicgeneration.utils.DistanceListener;
import com.dobi.walkingsynth.musicgeneration.utils.PositionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private int mBarPosition = 0;

    private int mBarCount = 0;

    private boolean isPlaying = true;
    /**
     * time interval between two positions in a bar.
     */
    private long mPositionInterval;

    private List<PositionListener> mPositionListeners;

    void addPositionListener(PositionListener listener) {
        if (mPositionListeners == null)
            mPositionListeners = new ArrayList<>();
        mPositionListeners.add(listener);
    }

    private List<BarListener> mBarListeners;

    void addBarListener(BarListener listener) {
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

    /**
     * Song counting variables:
     */
    private int mSongNumber = 1;
    private long mSongLength;
    private long mSongElapsed = 0;


    MusicAnalyzer() {
        mPositionInterval = calcPositionInterval();
        // calculate the length of a song
        calcNewSongLength();
        // time looper
        Thread hHatThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (isPlaying) {
                        sleep(mPositionInterval);
                        mBarPosition = (mBarPosition + 1) % BAR_INTERVALS;
                        if (mBarPosition == 0) {
                            ++mBarCount;
                        }
                        // update elapsed time and check whether to startCSound new song
                        mSongElapsed += mPositionInterval;
                        if (mSongElapsed > mSongLength) {
                            // startCSound a new song
                            calcNewSongLength();
                            mSongElapsed = 0;
                            mBarCount = 0;
                            ++mSongNumber;
                        }
                        // notify potential listeners
                        invalidateListeners();

                        Log.d(TAG, mBarPosition + " Sleep: " + mPositionInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        hHatThread.start();
    }

    private void invalidateListeners() {
        if (mPositionListeners != null) {
            for (PositionListener listener : mPositionListeners) {
                listener.invalidatePosition(mBarPosition);
                //mBarCount, mPositionInterval, calcElapsedSong()
            }
        }
        if (mBarListeners != null) {
            for (BarListener listener : mBarListeners) {
                listener.invalidateBar(mBarPosition);
            }
        }
        if (mDistanceListeners != null) {
            for (DistanceListener listener : mDistanceListeners) {
                listener.invalidateDistance(mPositionInterval);
            }
        }
    }


    private void calcNewSongLength() {
        Random random = new Random();
        mSongLength = (random.nextInt(600 - 180) + 180) * 1000; // to milliseconds
        Log.d(TAG, "Song length: " + ((double) mSongLength / 1000 / 60));
    }

    private int calcElapsedSong() {
        return (int)(((double)mSongElapsed / (double)mSongLength) * 100);
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
    private long calcPositionInterval() {
        final long pi =  (long)((60 / (float)mTempo) * 1000 ) / BAR_INTERVALS * 2;
        Log.d(TAG, "next Moment: " + pi);
        return pi;
    }

    public void onStep(long milliseconds) {
        Log.d(TAG, "onStep(): " + milliseconds);
        if (validateTempo(calculateTempo(milliseconds))) {
            mPositionInterval = calcPositionInterval();
        }
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
        // calc new tempo
        final int tempo =  (int)(1 / ((float)(stepTime - mLastStepTime) / (1000 * 60)));
        mLastStepTime = stepTime;
        return tempo;
    }

    private boolean validateTempo(int tempo) {
        if (Math.abs(tempo - mTempo) < MAX_TEMPO_DIFF & tempo >= MIN_TEMPO & tempo <= MAX_TEMPO) {
            mTempo = tempo;
            Log.d(TAG, "Tempo validated. Value: " + mTempo + "bpm.");
            return true;
        }
        return false;
    }


    int getTempo() {
        return mTempo;
    }

}
