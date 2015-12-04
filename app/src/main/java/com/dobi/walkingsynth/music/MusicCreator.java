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

    public void invalidateStep(int nval) {
        mSynth.invaliateStep(nval);
    }

    public void invalidateBaseNote(int pos)
    {
        mSynth.invalidateBaseNote(pos);
    }

    public void invalidateScale(int pos) {
        mSynth.invalidateScale(pos);
    }
}
