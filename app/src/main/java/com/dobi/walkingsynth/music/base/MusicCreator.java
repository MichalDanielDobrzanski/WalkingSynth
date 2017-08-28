package com.dobi.walkingsynth.music.base;

import android.content.res.Resources;

import com.dobi.walkingsynth.music.drums.DrumsPlayer;
import com.dobi.walkingsynth.music.synths.SynthesizerPlayer;

import java.io.File;

/**
 * Main entry class for music creation. General class which handles
 * all information passed from UI.
 */
public class MusicCreator extends CsoundBaseSetup {

    private MusicAnalyzer mMusicAnalyzer;

    private DrumsPlayer mDrums;

    private SynthesizerPlayer mSynth;

    public MusicCreator(Resources res, File cDir) {
        super(res, cDir);

        mMusicAnalyzer = new MusicAnalyzer();

        mDrums = new DrumsPlayer(csoundObj);
        mMusicAnalyzer.addPositionListener(mDrums);

        mSynth = new SynthesizerPlayer(csoundObj);
        mMusicAnalyzer.addPositionListener(mSynth);
        mMusicAnalyzer.addBarListener(mSynth);
    }


    public MusicAnalyzer getAnalyzer() {
        return mMusicAnalyzer;
    }

    // dynamic from accelerometer
    public void invalidateStep(int nval) {
        mDrums.invaliateStep(nval);
        mSynth.invaliateStep(nval);
    }

    // from UI spinner
    public void invalidateBaseNote(int pos)
    {
        mSynth.invalidateBaseNote(pos);
    }

    // from UI spinner
    public void invalidateScale(String scale) {
        mSynth.invalidateScale(scale);
    }

    // from UI spinner
    public void invalidateStepInterval(int idx) {
        mDrums.invalidateStepInterval(idx);
        mSynth.invalidateStepInterval(idx);
    }
}
