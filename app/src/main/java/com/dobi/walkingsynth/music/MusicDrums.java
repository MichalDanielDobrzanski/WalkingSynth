package com.dobi.walkingsynth.music;

import com.csounds.CsoundObj;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Drum and rhythm creating class.
 */
public class MusicDrums {

    private static final String TAG = MusicDrums.class.getSimpleName();

    private CsoundObj mCsoundObj;
    private int mPositionInBar;
    private int mBarCount;
    private float[][] mParams = new float[3][2];

    public MusicDrums(CsoundObj csoundObj) {
        mCsoundObj = csoundObj;
        // hi hat:
        mParams[0][0] = 0.15f;
        mParams[0][1] = 10;
        // snare:
        mParams[1][0] = 1;
        mParams[1][1] = 10000;
        // kick:
        mParams[2][0] = 0.50f;
        mParams[2][1] = 70;
    }

    public void play(int positionInBar, int barCount) {
        mPositionInBar = positionInBar;
        mBarCount = barCount;
//        if (positionInBar == 0 | positionInBar == 4)
//            mCsoundObj.sendScore("i1 0 0.15 10");
//        if (barCount % 2 == 0 & positionInBar == 0)
//            mCsoundObj.sendScore("i3 0 0.50 70");
//        if (barCount % 2 == 1 & positionInBar == 0)
//            mCsoundObj.sendScore("i2 0 1 10000");
        playSequence(4,1);
        playAt(0, true,  3);
        playAt(0, false, 2);
    }




    /**
     * Works for distance = 1,2,4
     * @param dist the distance
     * @param instr
     */
    public void playSequence(int dist, int instr) {
        if ((mPositionInBar + dist) % dist == 0) {
            playNote(instr);
        }
    }

    public void playAt(int pos, boolean firstBar, int instr) {
        if (mPositionInBar == pos & (mBarCount % 2 == 0) == firstBar) {
            playNote(instr);
        }
    }

    private void playNote(int instr) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                String.format("i%d 0 ",instr)
                    + df.format(mParams[instr - 1][0])  + " "
                    + df.format(mParams[instr - 1][1])
                );
    }

}
