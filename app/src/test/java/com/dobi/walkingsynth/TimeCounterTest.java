package com.dobi.walkingsynth;


import com.dobi.walkingsynth.model.musicgeneration.time.TimeCounter;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.TestScheduler;

import static junit.framework.Assert.assertTrue;

public class TimeCounterTest {

    private TestScheduler testScheduler;

    @Before
    public void setup() {
        testScheduler = new TestScheduler();
    }



    @Test
    public void timeCounter_60seconds() {
        TimeCounter timeCounter = new TimeCounter(testScheduler, testScheduler);
        timeCounter.resume();

        assertTrue(Objects.equals(timeCounter.getTime(), "00:00"));

        testScheduler.advanceTimeBy(60, TimeUnit.SECONDS);

        assertTrue(Objects.equals(timeCounter.getTime(), "01:00"));
    }

    @Test
    public void timeCounter_stopped() {
        TimeCounter timeCounter = new TimeCounter(testScheduler, testScheduler);
        timeCounter.resume();
        timeCounter.stop();

        assertTrue(Objects.equals(timeCounter.getTime(), "00:00"));

        testScheduler.advanceTimeBy(10000, TimeUnit.SECONDS);

        assertTrue(Objects.equals(timeCounter.getTime(), "00:00"));

    }
}
