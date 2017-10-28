package com.dobi.walkingsynth.musicgeneration.utils;

public enum Scale {
    Pentatonic(new Integer[] {0, 3, 5, 7, 10, 12}),
    Flamenco(new Integer[] {0, 1, 4, 5, 7, 8, 10, 12}),
    Minor(new Integer[] {0, 2, 3, 5, 7, 8, 10, 12}),
    Major(new Integer[]{0, 2, 4, 5, 7, 9, 11, 12})
    ;

    public final Integer[] intervals;

    Scale(Integer[] intervals) {
        this.intervals = intervals;
    }

    public static String[] toStringArray() {
        String[] stringArray = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringArray[i] = values()[i].name();
        }
        return stringArray;
    }

    public static Scale getScaleByName(String name) {
        return Scale.valueOf(name);
    }
}