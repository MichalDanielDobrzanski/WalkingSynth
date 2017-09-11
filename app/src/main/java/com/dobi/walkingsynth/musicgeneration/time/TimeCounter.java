package com.dobi.walkingsynth.musicgeneration.time;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
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
    private Timer timer;

    public TimeCounter(final TextView textView) {
        final WeakReference<TextView> textViewWeakReference = new WeakReference<>(textView);

        mInitialTime = SystemClock.elapsedRealtime();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long newValue = (SystemClock.elapsedRealtime() - mInitialTime);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textViewWeakReference.get().setText(convertMillisecondsToHumanReadable(newValue));
                    }
                });
            }
        }, ONE_SECOND, ONE_SECOND);
    }


    private String convertMillisecondsToHumanReadable(long milliseconds) {
        int seconds = (int) (milliseconds / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return outputTimerValue(minutes, seconds);
    }

    private String outputTimerValue(int minutes, int seconds) {
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public void cancel() {
        timer.cancel();
    }
}
