package com.dobi.walkingsynth.musicgeneration.core;

import android.content.res.Resources;

import com.dobi.walkingsynth.musicgeneration.drums.DrumsPlayer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerPlayer;

import java.io.File;

public class CsoundMusicCreator extends CsoundBaseSetup implements MusicCreator {

    private MusicAnalyzer mMusicAnalyzer;

    private DrumsPlayer mDrums;

    private SynthesizerPlayer mSynth;

    public CsoundMusicCreator(Resources res, File cDir) {
        super(res, cDir);

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

    public int getTempo() {
        return mMusicAnalyzer.getTempo();
    }

    public void onStep(int stepCount, long milliseconds) {
        mDrums.invaliateStep(stepCount);
        mSynth.invaliateStep(stepCount);
        mMusicAnalyzer.onStep(milliseconds);
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
