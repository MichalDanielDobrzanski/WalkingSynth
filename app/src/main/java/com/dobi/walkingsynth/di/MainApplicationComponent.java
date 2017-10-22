package com.dobi.walkingsynth.di;

import com.dobi.walkingsynth.MainActivity;

import dagger.Component;

@MainApplicationScope
@Component(modules = { MainApplicationModule.class, AccelerometerModule.class })
public interface MainApplicationComponent {

    void inject(MainActivity mainActivity);

}
