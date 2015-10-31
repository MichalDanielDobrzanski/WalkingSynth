package com.dobi.walkingsynth.music;

import android.content.res.Resources;

import java.io.File;

/**
 * Main entry class for music creation.
 */
public class MusicCreator extends CsoundBaseSetup {

    private static final String TAG = MusicCreator.class.getSimpleName();

    private MusicAnalyzer mMusicAnalyzer = new MusicAnalyzer();
    private MusicDrums mMusicDrums = new MusicDrums(csoundObj);

    public MusicCreator(Resources res, File cDir) {
        super(res, cDir);
        mMusicAnalyzer.setBasicIntervalListener(new OnBasicIntervalListener() {
            @Override
            public void onBasicInterval(int pos, int bc) {
                mMusicDrums.playSequence(pos,bc);
            }
        });
    }


    public MusicAnalyzer getAnalyzer() {
        return mMusicAnalyzer;
    }

}
