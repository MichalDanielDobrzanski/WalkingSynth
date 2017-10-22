package com.dobi.walkingsynth.di;

import com.dobi.walkingsynth.stepdetection.AccelerometerGraph;
import com.dobi.walkingsynth.stepdetection.AccelerometerProcessor;
import com.dobi.walkingsynth.stepdetection.AchartEngineAccelerometerGraph;

import dagger.Module;
import dagger.Provides;

@Module
public class AccelerometerModule {

    @Provides
    AccelerometerProcessor providesAccelerometerProcessor() {
        return new AccelerometerProcessor();
    }

    @Provides
    AccelerometerGraph providesAchartEngineAccelerometerGraph(AccelerometerProcessor accelerometerProcessor) {
        return new AchartEngineAccelerometerGraph(accelerometerProcessor);
    }
}
