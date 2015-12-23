package com.dobi.walkingsynth.music;

/**
 * Interface for sending the information bout the current bar and current position in it.
 */
public interface OnTimeIntervalListener {
    // pos - the position in a bar
    // bc - bar count (how many have passed)
    // posti - distance in milliseconds between two positions
    // es - elapsed song
    void onInterval(int pos, int bc, long posti, int es);
}
