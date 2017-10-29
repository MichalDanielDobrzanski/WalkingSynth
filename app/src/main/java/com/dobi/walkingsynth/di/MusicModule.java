package com.dobi.walkingsynth.di;

import android.content.res.Resources;

import com.dobi.walkingsynth.musicgeneration.core.CsoundAudioController;
import com.dobi.walkingsynth.musicgeneration.core.CsoundMusicAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.CsoundStepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.AudioController;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.MusicAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsAnalyzer;

import java.io.File;

import dagger.Module;
import dagger.Provides;

@Module
public class MusicModule {

    @Provides
    @MainApplicationScope
    StepsAnalyzer providesStepsAnalyzer() {
        return new CsoundStepsAnalyzer();
    }

    @Provides
    @MainApplicationScope
    MusicAnalyzer providesMusicAnalyzer() {
        return new CsoundMusicAnalyzer();
    }

    @Provides
    @MainApplicationScope
    AudioController providesAudioController(MusicAnalyzer musicAnalyzer, StepsAnalyzer stepsAnalyzer,  Resources res, File file) {
        return new CsoundAudioController(musicAnalyzer, stepsAnalyzer, res, file);
    }
}
