package com.dobi.walkingsynth.di;

import android.content.SharedPreferences;
import android.hardware.SensorManager;

import com.dobi.walkingsynth.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.stepdetection.detector.AccelerometerStepDetector;
import com.dobi.walkingsynth.stepdetection.detector.StepDetector;
import com.dobi.walkingsynth.stepdetection.graph.AccelerometerGraph;
import com.dobi.walkingsynth.stepdetection.graph.AchartEngineAccelerometerGraph;

import dagger.Module;
import dagger.Provides;

@Module
public class AccelerometerModule {

    @Provides
    @MainApplicationScope
    StepDetector providesStepDetector() {
        return new AccelerometerStepDetector();
    }

    @Provides
    @MainApplicationScope
    AccelerometerGraph providesAchartEngineAccelerometerGraph() {
        return new AchartEngineAccelerometerGraph();
    }

    @Provides
    @MainApplicationScope
    AccelerometerManager providesAccelerometerManager(SharedPreferences sharedPreferences, SensorManager sensorManager, AccelerometerGraph accelerometerGraph, StepDetector stepDetector) {
        AccelerometerManager accelerometerManager = new AccelerometerManager(sharedPreferences, sensorManager, accelerometerGraph, stepDetector);
        accelerometerManager.setOnThresholdChangeListener(accelerometerGraph);
        return accelerometerManager;
    }
}
