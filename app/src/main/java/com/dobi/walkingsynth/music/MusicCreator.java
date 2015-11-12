package com.dobi.walkingsynth.music;

import android.content.res.Resources;

import java.io.File;

/**
 * Main entry class for music creation.
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
                mDrums.invalidate(pos, el);
                mSynth.invalidate(pos, bc, el);
            }
        });
    }


    public MusicAnalyzer getAnalyzer() {
        return mMusicAnalyzer;
    }

}
