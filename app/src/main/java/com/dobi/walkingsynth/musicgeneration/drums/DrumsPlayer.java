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

    /**
     * This flag id for checking whether to invalidatePosition the note on specific time interval
     */
    private int bitFlag;

    private int mBarPosition;

    private DrumsSequencer mDrumsSequencer = new DrumsSequencer();

    public DrumsPlayer(CsoundObj csoundObj) {
        super(csoundObj);
    }

    @Override
    public void invalidatePosition(int position) {
        mBarPosition = position;
        playSequences(mDrumsSequencer.getSequences());
    }

    private void playSequences(ArrayList<Integer> sequences) {
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
            Log.d(TAG, "Instrument" + instrument + " at " + mBarPosition);
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



    public void invaliateStep(int stepcount) {
        if (stepcount % mStepInterval == 0) {
            Log.d(TAG,"Walked steps threshold. New csound_part rhythm score!");
            mDrumsSequencer.randomizeHiHat();
        }
    }

}
