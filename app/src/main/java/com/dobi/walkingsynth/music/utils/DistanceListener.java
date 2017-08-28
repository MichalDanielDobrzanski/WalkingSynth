package com.dobi.walkingsynth.music.utils;

public interface DistanceListener {

    // distance in milliseconds between two positions
    void invalidateDistance(long distance);

}
