package com.dobi.walkingsynth.model.musicgeneration.core;

import android.util.Log;

import com.dobi.walkingsynth.model.stepdetection.AccelerometerManager;

import java.util.ArrayList;
import java.util.List;

public class TempoAnalyzer implements AccelerometerManager.OnStepListener {

    public static final String TAG = TempoAnalyzer.class.getSimpleName();

    private static final int MIN_TEMPO = 60;

    private static final int MAX_TEMPO = 120;

    private static final int MAX_TEMPO_DIFF = 40;

    /**
     * How many divisions do for a single bar.
     * 8 corresponds to quarter notes.
     */
    private static final int BAR_INTERVALS = 8;

    private int tempo;

    private long mLastStepTime = 0;

    /**
     * The current position in a bar ( 0-indexed )
     * 0 1 2 3 4 5 6 7
     * _ _ _ _ _ _ _ _
     */
    private int currentPosition;

    private boolean isPlaying = true;

    private long interPositionInterval;

    private List<PositionListener> listeners;

    private List<TempoListener> tempoListeners;

    public TempoAnalyzer() {

        tempo = MIN_TEMPO;

        currentPosition = 0;

        interPositionInterval = calculateInterPositionInterval();

        startAnalyzingThread();
    }

    @Override
    public void onStepDetected(long milliseconds, int stepCount) {
        Log.d(TAG, "onStepDetected(): " + milliseconds);

        if (validateAndCalculateTempo(milliseconds)) {
            interPositionInterval = calculateInterPositionInterval();
        }
    }

    private boolean validateAndCalculateTempo(long stepTime) {
        return validateTempo(calculateTempo(stepTime));
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
        if (Math.abs(tempo - this.tempo) < MAX_TEMPO_DIFF && tempo >= MIN_TEMPO && tempo <= MAX_TEMPO) {
            this.tempo = tempo;

            invalidateTempoListeners();

            Log.d(TAG, "New tempo is: " + this.tempo + "bpms");
            return true;
        }
        return false;
    }

    public int getTempo() {
        return tempo;
    }

    public void addPositionListener(PositionListener listener) {
        if (listeners == null)
            listeners = new ArrayList<>();
        listeners.add(listener);
    }

    public void addTempoListener(TempoListener listener) {
        if (tempoListeners == null) {
            tempoListeners = new ArrayList<>();
        }
        tempoListeners.add(listener);
    }

    /**
     * Calculate next time interval based on tempo. When (in time) should I invalidateTempo another note.
     * 60 / tempo = seconds between beats
     * (60 / tempo) * 1000 = milliseconds between beats
     *
     *  Basically, do this every 250ms.
     *  Its nice and enough.
     *
     *
     * @return time distance to the next moment.
     */
    private long calculateInterPositionInterval() {
        final long positions =  (long)((60 / (float) tempo) * 1000 ) / BAR_INTERVALS * 2;
        Log.d(TAG, "calculateInterPositionInterval(): " + positions);
        return positions;
    }

    private void startAnalyzingThread() {
        Thread analyzerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (isPlaying) {
                        sleep(interPositionInterval);

                        currentPosition = (currentPosition + 1) % BAR_INTERVALS;

                        invalidateListeners();

                        Log.d(TAG, "Bar position: " + currentPosition + " Sleep for interval: " + interPositionInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        analyzerThread.start();
    }

    private void invalidateListeners() {
        if (listeners != null) {
            for (PositionListener listener : listeners) {
                listener.invalidate(currentPosition);
            }
        }
    }

    private void invalidateTempoListeners() {
        if (tempoListeners != null) {
            for (TempoListener listener : tempoListeners) {
                listener.invalidateTempo(tempo);
            }
        }
    }

    public interface PositionListener {
        void invalidate(int position);
    }

    public interface TempoListener {
        void invalidateTempo(int tempo);
    }



}
