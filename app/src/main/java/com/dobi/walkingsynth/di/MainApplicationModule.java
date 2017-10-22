package com.dobi.walkingsynth.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;

import com.dobi.walkingsynth.musicgeneration.time.TimeCounter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainApplicationModule {

    public static final String PREFERENCES_NAME = "Values";

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
