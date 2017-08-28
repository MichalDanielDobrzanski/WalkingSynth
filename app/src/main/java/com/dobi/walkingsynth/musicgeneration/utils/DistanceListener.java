package com.dobi.walkingsynth.musicgeneration.utils;

public interface DistanceListener {

    // distance in milliseconds between two positions
    void invalidateDistance(long distance);

}
