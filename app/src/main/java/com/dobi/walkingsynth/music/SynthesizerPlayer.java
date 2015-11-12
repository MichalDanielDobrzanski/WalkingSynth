package com.dobi.walkingsynth.music;
import com.csounds.CsoundObj;

import java.text.DecimalFormat;

/**
 * Synthesizer playing
 */
public class SynthesizerPlayer extends BasePlayer {

    private static final String TAG = SynthesizerPlayer.class.getSimpleName();

    private int mBeatCount = 0;

    public SynthesizerPlayer(CsoundObj csoundObj) {
        super(csoundObj);
    }

    protected void invalidate(int pb, int bc, int es) {
        super.invalidate(pb, es);
        mBeatCount = bc;
    }

    private void playCsoundNote(int instr) {
//        final DecimalFormat df = new DecimalFormat("#.##");
//        mCsoundObj.sendScore(
//                String.format("i%d 0 ", instr)
//                        + df.format(mParams[instr - 1][0]) + " "
//                        + df.format(mParams[instr - 1][1])
//        );
    }
}
