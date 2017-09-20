package com.dobi.walkingsynth.musicgeneration.utils;

public enum Note {
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

    public static Note getByName(String name) {
        if (name.length() > 1) {
            name = name.substring(0,1);
            name = name.concat("Sharp");
        }
        return Note.valueOf(name);
    }

    Note(final String text) {
        this.note = text;
    }
}