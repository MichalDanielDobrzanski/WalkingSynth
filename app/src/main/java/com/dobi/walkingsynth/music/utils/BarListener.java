package com.dobi.walkingsynth.music.utils;

public interface BarListener {

    // bar count (how many have passed)
    void invalidateBar(int bar);
}
