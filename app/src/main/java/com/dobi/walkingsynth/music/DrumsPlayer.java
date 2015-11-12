package com.dobi.walkingsynth.music;

import android.util.Log;

import com.csounds.CsoundObj;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Drum and rhythm creating class.
 */
public class DrumsPlayer extends BasePlayer {

    private static final String TAG = DrumsPlayer.class.getSimpleName();

    private DrumsSequencer mDrumsSequencer = new DrumsSequencer();

    public DrumsPlayer(CsoundObj csoundObj) {
        super(csoundObj);
    }

    /**
     * Main looping function for reading the playSequence values.
     * @param positionInBar time position.
     */
    public void invalidate(int pb, int es) {
        super.invalidate(pb,es);
        // read drum pattern from sequencer and play it:
        mDrumsSequencer.setTime(es);
        playSequence(mDrumsSequencer.getSequences());

    }

    /**
     * Csound playback.
     * @param instr selected instrument.
     */
    protected void playCsoundNote(int instr) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mCsoundObj.sendScore(
                String.format("i%d 0 ", instr)
                        + df.format(mDrumsSequencer.getParametrs(instr)[0]) + " "
                        + df.format(mDrumsSequencer.getParametrs(instr)[1])
        );
    }

    /**
     * Reads the current pattern sequences and passes it to the csound playback.
     * @param sequences the pattern sequences.
     */
    private void playSequence(ArrayList<Integer> sequences) {
        for (int i = 0; i < sequences.size(); ++i) {
            // reading sequences for every instrument
            playAt(sequences.get(i),i);
        }
        // move flag:
        moveFlag();
    }

}
