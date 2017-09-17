package com.dobi.walkingsynth.musicgeneration.core;

import android.content.res.Resources;

import com.dobi.walkingsynth.musicgeneration.drums.DrumsPlayer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerPlayer;

import java.io.File;

public class CsoundMusicCreator extends CsoundBaseSetup implements MusicCreator {

    private MusicAnalyzer mMusicAnalyzer;

    private DrumsPlayer mDrums;

    private SynthesizerPlayer mSynth;

    private int mStepCounter;

    private static CsoundMusicCreator mInstance;

    public static void createInstance(Resources res, File cDir) {
        if (mInstance == null)
            mInstance = new CsoundMusicCreator(res, cDir);
    }

    public static CsoundMusicCreator getInstance() {
        return mInstance;
    }

    private CsoundMusicCreator(Resources res, File cDir) {
        super(res, cDir);

        mStepCounter = 0;

        mMusicAnalyzer = new MusicAnalyzer();

        mDrums = new DrumsPlayer(csoundObj);
        mMusicAnalyzer.addPositionListener(mDrums);

        mSynth = new SynthesizerPlayer(csoundObj);
        mMusicAnalyzer.addPositionListener(mSynth);
        mMusicAnalyzer.addBarListener(mSynth);
    }

    @Override
    public void start() {
        startCSound();
    }

    @Override
    public void destroy() {
        destroyCSound();
    }

    public int getCurrentTempo() {
        return mMusicAnalyzer.getTempo();
    }

    public void onStep(long milliseconds) {
        mStepCounter++;
        mDrums.invaliateStep(mStepCounter);
        mSynth.invaliateStep(mStepCounter);
        mMusicAnalyzer.onStep(milliseconds);
    }

    @Override
    public int getStepCount() {
        return mStepCounter;
    }

    public void invalidateBaseNote(int pos)
    {
        mSynth.invalidateBaseNote(pos);
    }

    public void invalidateScale(String scale) {
        mSynth.invalidateScale(scale);
    }

    public void invalidateStepInterval(int idx) {
        mDrums.invalidateStepInterval(idx);
        mSynth.invalidateStepInterval(idx);
    }
}
