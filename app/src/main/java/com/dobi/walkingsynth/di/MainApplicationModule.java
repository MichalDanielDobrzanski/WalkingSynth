package com.dobi.walkingsynth.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.SensorManager;

import com.dobi.walkingsynth.ApplicationMvp;
import com.dobi.walkingsynth.model.musicgeneration.core.AudioPlayer;
import com.dobi.walkingsynth.model.musicgeneration.time.TimeCounter;
import com.dobi.walkingsynth.model.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.presenter.MainPresenter;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class MainApplicationModule {

    public static final String PREFERENCES_NAME = "Values";

    public static final String PREFERENCES_VALUES_BASENOTE_KEY = "base-note";
    public static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";
    public static final String PREFERENCES_VALUES_SCALE_KEY = "scale";
    public static final String PREFERENCES_VALUES_STEPS_INTERVAL_KEY = "steps-interval";

    public static final float THRESHOLD_INITIAL = 12.72f;

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
        return new TimeCounter(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @Provides
    @MainApplicationScope
    SensorManager providesSensorManager() {
        return (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Provides
    @MainApplicationScope
    ApplicationMvp.Presenter providesPresenter(SharedPreferences sharedPreferences,
                                               AccelerometerManager accelerometerManager,
                                               AudioPlayer audioPlayer,
                                               TimeCounter timeCounter) {
        return new MainPresenter(sharedPreferences, accelerometerManager, audioPlayer, timeCounter);
    }

}
