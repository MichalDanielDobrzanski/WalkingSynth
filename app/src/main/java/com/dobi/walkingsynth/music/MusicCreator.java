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

    public MusicCreator(Resources res, File cDir) {
        super(res, cDir);
        mMusicAnalyzer.setIntervalListener(new OnTimeIntervalListener() {
            @Override
            public void onInterval(int pos, int el) {
                mDrums.invalidate(pos, el);
            }
        });
    }


    public MusicAnalyzer getAnalyzer() {
        return mMusicAnalyzer;
    }

}
