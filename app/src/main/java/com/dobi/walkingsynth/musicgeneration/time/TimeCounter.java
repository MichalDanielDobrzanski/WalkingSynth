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

public class TimeCounter {

    public static final String TAG = TimeCounter.class.getSimpleName();

    private static final int ONE_SECOND = 1000;

    private boolean mRunning;
    private long mInitialTime;
    private String mLastTime;
    private Timer mTimer;

    private WeakReference<TextView> mTextViewWeakReference;

    public TimeCounter() {
        mRunning = false;
        mInitialTime = SystemClock.elapsedRealtime();
        mLastTime = convertMillisecondsToHumanReadable(0);
        mTimer = new Timer();
    }

    public void setView(TextView textView) {
        textView.setText(mLastTime);
        mTextViewWeakReference = new WeakReference<>(textView);
    }

    public void startTimer() {
        if (!mRunning) {
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
            mRunning = true;
        }
    }

    public void stopTimer() {
        mTimer.cancel();
        mRunning = false;
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
