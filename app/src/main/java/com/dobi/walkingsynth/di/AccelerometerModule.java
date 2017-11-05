package com.dobi.walkingsynth.di;

import android.hardware.SensorManager;

import com.dobi.walkingsynth.model.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.model.stepdetection.StepDetector;
import com.dobi.walkingsynth.view.GraphView;
import com.dobi.walkingsynth.view.impl.AchartEngineGraphView;

import dagger.Module;
import dagger.Provides;

@Module
public class AccelerometerModule {

    @Provides
    @MainApplicationScope
    StepDetector providesStepDetector() {
        return new StepDetector();
    }

    @Provides
    @MainApplicationScope
    GraphView providesAchartEngineAccelerometerGraph() {
        return new AchartEngineGraphView();
    }

    @Provides
    @MainApplicationScope
    AccelerometerManager providesAccelerometerManager(SensorManager sensorManager,
                                                      GraphView accelerometerGraph,
                                                      StepDetector stepDetector) {

        AccelerometerManager accelerometerManager = new AccelerometerManager(
                sensorManager,
                accelerometerGraph,
                stepDetector);

        accelerometerManager.setOnThresholdChangeListener(accelerometerGraph);

        return accelerometerManager;
    }
}
