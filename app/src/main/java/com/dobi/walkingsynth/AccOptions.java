package com.dobi.walkingsynth;

/**
 * Created by dobi on 17.10.15.
 */
public enum AccOptions {

    MAGNITUDE,
    GRAV_DIFF
    ;

    public static final int size = AccOptions.values().length;
    public static final String[] OPTIONS = {"|V|","\\u0394g"};

}
