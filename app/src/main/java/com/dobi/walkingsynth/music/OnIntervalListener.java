package com.dobi.walkingsynth.music;

/**
 * Interface for sending the information bout the current bar and current position in it.
 */
public interface OnIntervalListener {
    void onInterval(int pos, int bc);
}
