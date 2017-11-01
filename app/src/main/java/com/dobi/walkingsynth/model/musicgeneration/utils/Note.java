package com.dobi.walkingsynth.model.musicgeneration.utils;

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

    Note(final String text) {
        this.note = text;
    }

    public static Note getNoteByName(String name) {
        if (name.length() > 1) {
            name = name.substring(0,1);
            name = name.concat("Sharp");
        }
        return Note.valueOf(name);
    }

    public static String[] toStringArray() {
        String[] stringArray = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringArray[i] = values()[i].note;
        }
        return stringArray;
    }
}