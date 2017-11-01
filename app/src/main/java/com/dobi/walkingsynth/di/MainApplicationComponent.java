package com.dobi.walkingsynth.di;

import com.dobi.walkingsynth.view.MainActivity;

import dagger.Component;

@MainApplicationScope
@Component(modules = { MainApplicationModule.class, AccelerometerModule.class, MusicModule.class })
public interface MainApplicationComponent {

    void inject(MainActivity mainActivity);

}
