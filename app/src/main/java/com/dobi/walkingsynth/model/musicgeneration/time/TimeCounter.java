package com.dobi.walkingsynth.model.musicgeneration.time;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

public class TimeCounter {

    public static final String TAG = TimeCounter.class.getSimpleName();

    private static final int ONE_SECOND = 1000;

    private AtomicLong lastTick = new AtomicLong(0L);

    private Disposable disposable;

    private TextView textView;

    private Scheduler scheduler;

    private Scheduler observeScheduler;

    public TimeCounter(Scheduler scheduler, Scheduler observeScheduler) {
        this.scheduler = scheduler;
        this.observeScheduler = observeScheduler;
    }

    public void setView(TextView textView) {
        this.textView = textView;
    }

    // this method is thread-safe thanks to get()
    public String getTime() {
        return convertMillisecondsToHumanReadable(lastTick.get());
    }

    public void resume() {
        disposable = Observable.interval(ONE_SECOND, TimeUnit.MILLISECONDS, scheduler)
                .map(tick -> lastTick.getAndIncrement())
                .observeOn(observeScheduler)
                .subscribe(tick -> {

                    String stringTime = convertMillisecondsToHumanReadable(tick);

                    Log.d(TAG, "Observable interval subscription: " + stringTime);

                    if (textView != null)
                        textView.setText(stringTime);
                });
    }

    public void stop() {
        if (disposable != null && !disposable.isDisposed()) {

            disposable.dispose();
        }
    }

    @VisibleForTesting
    protected String convertMillisecondsToHumanReadable(long tick) {
        int minutes = (int) tick / 60;
        int seconds = (int) tick % 60;
        return formatMinutesAndSeconds(minutes, seconds);
    }

    private String formatMinutesAndSeconds(int minutes, int seconds) {
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
