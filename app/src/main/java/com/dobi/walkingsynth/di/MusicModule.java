package com.dobi.walkingsynth.di;

import android.content.res.Resources;

import com.dobi.walkingsynth.model.musicgeneration.core.CsoundAudioPlayer;
import com.dobi.walkingsynth.model.musicgeneration.core.StepsAnalyzer;
import com.dobi.walkingsynth.model.musicgeneration.core.TempoAnalyzer;
import com.dobi.walkingsynth.model.musicgeneration.core.AudioPlayer;

import java.io.File;

import dagger.Module;
import dagger.Provides;

@Module
public class MusicModule {

    @Provides
    @MainApplicationScope
    StepsAnalyzer providesStepsAnalyzer() {
        return new StepsAnalyzer();
    }

    @Provides
    @MainApplicationScope
    TempoAnalyzer providesTempoAnalyzer() {
        return new TempoAnalyzer();
    }

    @Provides
    @MainApplicationScope
    AudioPlayer providesAudioController(TempoAnalyzer tempoAnalyzer, StepsAnalyzer stepsAnalyzer, Resources res, File file) {
        return new CsoundAudioPlayer(tempoAnalyzer, stepsAnalyzer, res, file);
    }
}
