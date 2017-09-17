package com.dobi.walkingsynth;

import com.dobi.walkingsynth.stepdetection.AccelerometerProcessor;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class AccelerometerTests {

    @Test
    public void thresholdValues_areValid() {
        AccelerometerProcessor accelerometerProcessor = AccelerometerProcessor.getInstance();

        accelerometerProcessor.setThreshold(AccelerometerProcessor.progressToThreshold(0));

        assertTrue(accelerometerProcessor.getThreshold() > 0);

        accelerometerProcessor.setThreshold(AccelerometerProcessor.progressToThreshold(100));
        //System.out.println("threshold for progress 100: " + accelerometerProcessor.getThreshold());

        assertTrue(accelerometerProcessor.getThreshold() > 0 &&
                accelerometerProcessor.getThreshold() < AccelerometerProcessor.MAX_THRESHOLD);
    }

    @Test
    public void progressValues_areValid() {

        int progMax = AccelerometerProcessor.thresholdToProgress(AccelerometerProcessor.MAX_THRESHOLD);
        //System.out.println("progress: " + progMax);
        assertTrue(progMax == 100);

        int progDef = AccelerometerProcessor.thresholdToProgress(AccelerometerProcessor.THRESHOLD_INITIAL);
        assertTrue(progDef > 0 && progDef < 100);
    }

}
