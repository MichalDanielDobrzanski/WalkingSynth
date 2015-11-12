package com.dobi.walkingsynth.music;

import android.util.Log;

import java.util.Random;

/**
 * Anaylizing tempo and other parameters.
 */
public class MusicAnalyzer {

    private static final String TAG = MusicAnalyzer.class.getSimpleName();

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 130;
    private static final int MAX_TEMPO_DIFF = 40;
    public static final int BAR_INTERVALS = 4; // as for now

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
     * 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
     * _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
     */
    private int mBeatPosition = 0;

    private int mBeatCount = 0;

    private boolean isPlaying = true;
    /**
     * time interval between two positions in a bar.
     */
    private long mPositionInterval;

    private OnTimeIntervalListener mIntervalListener;

    public void setIntervalListener(OnTimeIntervalListener listener) {
        mIntervalListener = listener;
    }

    /**
     * Song counting variables:
     */
    private int mSongNumber = 1;
    private long mSongLength;
    private long mSongElapsed = 0;

    /**
     * Entry analysis parameters
     */
    public MusicAnalyzer() {
        mIntervalListener = null;
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
                        mBeatPosition = (mBeatPosition + 1) % BAR_INTERVALS;
                        if (mBeatPosition == 0) {
                            mBeatCount = 0;
                        }
                        // update elapsed time and check whether to start new song
                        mSongElapsed += mPositionInterval;
                        if (mSongElapsed > mSongLength) {
                            // start a new song
                            calcNewSongLength();
                            mSongElapsed = 0;
                            mBeatCount = 0;
                            ++mSongNumber;
                        }
                        // notify potential listeners
                        if (mIntervalListener != null)
                            mIntervalListener.onInterval(mBeatPosition, mBeatCount, calcElapsedSong());
                        Log.d(TAG, mBeatPosition + " Sleep: " + mPositionInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        hHatThread.start();
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
     * Calculate next time interval based on tempo. When should I invalidate another note.
     * 60 / tempo = seconds between beats
     * (60 / tempo) * 1000 = milliseconds between beats
     * @param intervalType 1 = Note, 2 = HalfNote, 4 = QuarterNote, 8 = EightNote
     * @return time distance to the next moment.
     */
    private long calcPositionInterval() {
        final long pi =  (long)((60 / (float)mTempo) * 1000 ) / BAR_INTERVALS;
        Log.d(TAG, "next Moment: " + pi);
        return pi;
    }

    /**
     * Called when step has been detected.
     * @param stepTime current step time in milliseconds.
     */
    public void onStep(long stepTime) {
        Log.d(TAG, "onStep");
        if (isTempoOk(calculateTempo(stepTime))) {
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

    /**
     * Checks whether the calculated tempo is ok.
     * If it is then this method sets the variable.
     * @param tempo value to be checked.
     * @return tempo is ok / not ok.
     */
    private boolean isTempoOk(int tempo) {
        if (Math.abs(tempo - mTempo) < MAX_TEMPO_DIFF & tempo >= MIN_TEMPO & tempo <= MAX_TEMPO) {
            mTempo = tempo;
            Log.d(TAG, "Tempo is ok. Value: " + mTempo + "bpm.");
            return true;
        }
        return false;
    }


    public int getTempo() {
        return mTempo;
    }

}
