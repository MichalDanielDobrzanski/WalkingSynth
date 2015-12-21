package com.dobi.walkingsynth.music;

import android.content.res.Resources;

import com.dobi.walkingsynth.OnStepCountChangeListener;

import java.io.File;

/**
 * Main entry class for music creation. General class which handles
 * all information passed from UI.
 */
public class MusicCreator extends CsoundBaseSetup {

    private static final String TAG = MusicCreator.class.getSimpleName();

    private MusicAnalyzer mMusicAnalyzer = new MusicAnalyzer();
    private DrumsPlayer mDrums = new DrumsPlayer(csoundObj);
    private SynthesizerPlayer mSynth = new SynthesizerPlayer(csoundObj);

    public MusicCreator(Resources res, File cDir) {
        super(res, cDir);
        mMusicAnalyzer.setIntervalListener(new OnTimeIntervalListener() {
            @Override
            public void onInterval(int pos, int bc, int el) {
                mDrums.invalidate(pos, bc, el);
                mSynth.invalidate(pos, bc, el);
            }
        });
    }


    public MusicAnalyzer getAnalyzer() {
        return mMusicAnalyzer;
    }

    // dynamic from accelerometer
    public void invalidateStep(int nval) {
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
        mSynth.invalidateStepInterval(idx);
    }
}
