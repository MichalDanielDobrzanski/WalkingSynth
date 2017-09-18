package com.dobi.walkingsynth.musicgeneration.drums;

import android.util.Log;

import com.csounds.CsoundObj;
import com.dobi.walkingsynth.musicgeneration.core.BasePlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Drum and rhythm creating class.
 */
public class DrumsPlayer extends BasePlayer {

    private static final String TAG = DrumsPlayer.class.getSimpleName();

    private int bitFlag;

    private DrumsSequencer mDrumsSequencer;

    public DrumsPlayer(CsoundObj csoundObj, int steps, DrumsSequencer drumsSequencer) {
        super(csoundObj, steps);
        mDrumsSequencer = drumsSequencer;
    }

    @Override
    public void invalidate(int position, int steps) {
        Log.d(TAG, "invalidate() position: " + position + " steps: " + steps + "getStepInterval(): " + getStepInterval());
        if (steps + 1 % (getStepInterval() + 1) == 0) {
            mDrumsSequencer.randomizeHiHat();
        }
        playAllSequences(mDrumsSequencer.getSequences());
    }

    private void playAllSequences(ArrayList<Integer> sequences) {
        for (int i = 0; i < sequences.size(); ++i) {
            playAt(sequences.get(i), i);
        }
        moveFlag();
    }

    /**
     * Parsing the sequence (as number) to the playback.
     * The number values are compared with the moving flag.
     * @param binarySequence sequence of notes to hit (passed as a number)
     * @param instrument selected instrument.
     */
    private void playAt(int binarySequence, int instrument) {
        if ((binarySequence & bitFlag) > 0) {
            instrument++;
            playDrumInstrument(instrument);
        }
    }

    private void playDrumInstrument(int instrument) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                String.format("i%d 0 ", instrument)
                        + df.format(mDrumsSequencer.getParameters(instrument)[0]) + " "
                        + df.format(mDrumsSequencer.getParameters(instrument)[1])
        );
    }


    /**
     * This functions moves the flag to the left.
     * The flag has 8 positions.
     *      1 0 0 0 0 0 0 0
     * then 0 1 0 0 0 0 0 0
     * then 0 0 1 0 0 0 0 0
     * then 0 0 0 1 0 0 0 0
     * etc...
     * When reached zero, it is set to
     *      1 0 0 0 0 0 0 0
     */
    private void moveFlag() {
        bitFlag = bitFlag >> 1;
        if (bitFlag == 0)
            bitFlag = 128;
    }
}
