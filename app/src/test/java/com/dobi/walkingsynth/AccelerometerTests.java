package com.dobi.walkingsynth;

import com.dobi.walkingsynth.stepdetection.detector.AccelerometerStepDetector;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class AccelerometerTests {

    @Test
    public void thresholdValues_areValid() {
        AccelerometerStepDetector accelerometerProcessor = AccelerometerStepDetector.getInstance();

        accelerometerProcessor.setThreshold(AccelerometerStepDetector.progressToThreshold(0));

        assertTrue(accelerometerProcessor.getThreshold() > 0);

        accelerometerProcessor.setThreshold(AccelerometerStepDetector.progressToThreshold(100));
        //System.out.println("threshold for progress 100: " + accelerometerProcessor.getThreshold());

        assertTrue(accelerometerProcessor.getThreshold() > 0 &&
                accelerometerProcessor.getThreshold() < AccelerometerStepDetector.MAX_THRESHOLD);
    }

    @Test
    public void progressValues_areValid() {

        int progMax = AccelerometerStepDetector.thresholdToProgress(AccelerometerStepDetector.MAX_THRESHOLD);
        //System.out.println("progress: " + progMax);
        assertTrue(progMax == 100);

        int progDef = AccelerometerStepDetector.thresholdToProgress(AccelerometerStepDetector.THRESHOLD_INITIAL);
        assertTrue(progDef > 0 && progDef < 100);
    }

}
