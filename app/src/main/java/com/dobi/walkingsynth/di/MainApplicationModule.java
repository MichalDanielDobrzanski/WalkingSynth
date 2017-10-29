package com.dobi.walkingsynth.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.SensorManager;

import com.dobi.walkingsynth.musicgeneration.time.TimeCounter;

import java.io.File;

import dagger.Module;
import dagger.Provides;

@Module
public class MainApplicationModule {

    public static final String PREFERENCES_NAME = "Values";

    public static final String PREFERENCES_VALUES_BASENOTE_KEY = "base-note";
    public static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";
    public static final String PREFERENCES_VALUES_SCALE_KEY = "scale";
    public static final String PREFERENCES_VALUES_STEPS_INTERVAL_KEY = "steps-interval";

    private Context context;

    public MainApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    @MainApplicationScope
    Context providesContext() {
        return context;
    }

    @Provides
    @MainApplicationScope
    File providesFile(Context context) {
        return context.getCacheDir();
    }

    @Provides
    @MainApplicationScope
    Resources providesResources(Context context) {
        return context.getResources();
    }

    @Provides
    @MainApplicationScope
    SharedPreferences providesSharedPreferences() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @MainApplicationScope
    TimeCounter providesTimeCounter() {
        TimeCounter timeCounter = new TimeCounter();
        timeCounter.startTimer();
        return timeCounter;
    }

    @Provides
    @MainApplicationScope
    SensorManager providesSensorManager() {
        return (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
    }

}
