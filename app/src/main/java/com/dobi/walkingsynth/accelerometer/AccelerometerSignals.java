package com.dobi.walkingsynth.accelerometer;

/**
 * Accelerometer signal names that are used in analysing, processing and plotting.
 */
public enum AccelerometerSignals {

    MAGNITUDE,
    MOV_AVERAGE
    ;

    public static final int count = AccelerometerSignals.values().length;
    public static final String[] OPTIONS = {"|V|","\\u0394g"};

}
