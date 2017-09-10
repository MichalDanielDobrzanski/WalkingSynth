package com.dobi.walkingsynth;

import com.dobi.walkingsynth.stepdetection.accelerometer.AccelerometerProcessing;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccelerometerTests {

    @Test
    public void thresholdRanges_areValid() {
        AccelerometerProcessing accelerometerProcessing = AccelerometerProcessing.getInstance();
        accelerometerProcessing.onProgressChange(0);
        System.out.println(accelerometerProcessing.getThreshold());
        assertTrue(accelerometerProcessing.getThreshold() > 0);
        assertTrue(accelerometerProcessing.getThreshold() < 20);
        accelerometerProcessing.onProgressChange(50);
        System.out.println(accelerometerProcessing.getThreshold());
        assertTrue(accelerometerProcessing.getThreshold() > 0);
        assertTrue(accelerometerProcessing.getThreshold() < 20);
    }

}
