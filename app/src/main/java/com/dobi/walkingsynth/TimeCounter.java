package com.dobi.walkingsynth;

import android.os.Handler;
import android.widget.TextView;

import org.achartengine.model.TimeSeries;

/**
 * Timer for showing how long the music is being played.
 */
public class TimeCounter {

    private final int FPS = 60;

    /**
     * Public accessors for time measurement.
     */
    public static int seconds = 0;
    public static int minutes = 0;

    private long mStartTime = 0;

    private TextView mTextView;
    private Handler mTimerHandler;

    public TimeCounter(Handler handler, TextView textView) {
        // associates the handler from other thread with this runnable
        mTimerHandler = handler;
        mTextView = textView;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - mStartTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            mTextView.setText(String.format("%d:%02d", minutes, seconds));
            mTimerHandler.postDelayed(this, 1000 / FPS);

        }
    };

    public void start() {
        mStartTime = System.currentTimeMillis();
        mTimerHandler.postDelayed(mRunnable,0);
    }
}
