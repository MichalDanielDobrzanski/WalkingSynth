package com.dobi.walkingsynth.musicgeneration.utils;

public enum Notes {
    C("C"),
    CSharp("C#"),
    D("D"),
    DSharp("D#"),
    E("E"),
    F("F"),
    FSharp("F#"),
    G("G"),
    GSharp("G#"),
    A("A"),
    ASharp("A#"),
    B("B");

    public final String note;

    Notes(final String text) {
        this.note = text;
    }
}