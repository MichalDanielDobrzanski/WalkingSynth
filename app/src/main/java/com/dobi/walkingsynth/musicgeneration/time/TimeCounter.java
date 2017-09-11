package com.dobi.walkingsynth.musicgeneration.time;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer for showing how long the music is being played.
 */
public class TimeCounter {

    public static final String TAG = TimeCounter.class.getSimpleName();

    private static final int ONE_SECOND = 1000;

    private long mInitialTime;
    private String mLastTime;
    private Timer mTimer;

    private final WeakReference<TextView> mTextViewWeakReference;

    public TimeCounter(final TextView textView) {
        mTextViewWeakReference = new WeakReference<>(textView);
        mInitialTime = SystemClock.elapsedRealtime();
        mTimer = new Timer();
    }

    public void startTimer() {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                final long newValue = (SystemClock.elapsedRealtime() - mInitialTime);
                mLastTime = convertMillisecondsToHumanReadable(newValue);
                Log.d(TAG, "run: time: " + mLastTime);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = mTextViewWeakReference.get();
                        if (textView != null)
                            textView.setText(mLastTime);
                    }
                });
            }
        }, ONE_SECOND, ONE_SECOND);
    }

    private String convertMillisecondsToHumanReadable(long milliseconds) {
        int seconds = (int) (milliseconds / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return formatMinutesAndSeconds(minutes, seconds);
    }

    private String formatMinutesAndSeconds(int minutes, int seconds) {
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
