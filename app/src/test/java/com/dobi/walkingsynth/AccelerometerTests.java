package com.dobi.walkingsynth;

import com.dobi.walkingsynth.stepdetection.AccelerometerProcessor;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AccelerometerTests {

    private static int MAX_THRESHOLD = 25;

    @Test
    public void thresholdRanges_areValid() {
        AccelerometerProcessor accelerometerProcessing = AccelerometerProcessor.getInstance();

        accelerometerProcessing.onProgressChange(0);
        assertTrue(accelerometerProcessing.getThreshold() > 0);
        assertTrue(accelerometerProcessing.getThreshold() < MAX_THRESHOLD);

        accelerometerProcessing.onProgressChange(50);
        assertTrue(accelerometerProcessing.getThreshold() > 0);
        assertTrue(accelerometerProcessing.getThreshold() < MAX_THRESHOLD);

        accelerometerProcessing.onProgressChange(100);
        assertTrue(accelerometerProcessing.getThreshold() > 0);
        assertTrue(accelerometerProcessing.getThreshold() < MAX_THRESHOLD);

    }

}
