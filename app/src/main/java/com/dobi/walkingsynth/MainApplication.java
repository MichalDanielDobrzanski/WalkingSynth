package com.dobi.walkingsynth;

import android.app.Application;

import com.dobi.walkingsynth.di.DaggerMainApplicationComponent;
import com.dobi.walkingsynth.di.MainApplicationComponent;
import com.dobi.walkingsynth.di.MainApplicationModule;

public class MainApplication extends Application {

    private MainApplicationComponent mainApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainApplicationComponent = DaggerMainApplicationComponent.builder()
                .mainApplicationModule(new MainApplicationModule(this))
                .build();
    }

    MainApplicationComponent getApplicationComponent() {
        return mainApplicationComponent;
    }
}
