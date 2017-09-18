package com.dobi.walkingsynth.musicgeneration.utils;

public enum Scales {
    Pentatonic(new Integer[] {0, 3, 5, 7, 10, 12}),
    Flamenco(new Integer[] {0, 1, 4, 5, 7, 8, 10, 12});

    public final Integer[] intervals;

    Scales(Integer[] intervals) {
        this.intervals = intervals;
    }

}