package com.dobi.walkingsynth.musicgeneration.core;

import android.content.res.Resources;

import com.dobi.walkingsynth.musicgeneration.drums.DrumsPlayer;
import com.dobi.walkingsynth.musicgeneration.drums.DrumsSequencer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerPlayer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.utils.Notes;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CsoundMusicController extends CsoundBaseSetup implements MusicController {

    private WalkingMusicAnalyzer mMusicAnalyzer;
    private List<BasePlayer> mPlayers;

    private static CsoundMusicController mInstance;

    public static void createInstance(WalkingMusicAnalyzer musicAnalyzer, Resources res, File cDir) {
        if (mInstance == null) {
            mInstance = new CsoundMusicController(musicAnalyzer, res, cDir);
        }
    }

    public static CsoundMusicController getInstance() {
        return mInstance;
    }

    private CsoundMusicController(WalkingMusicAnalyzer musicAnalyzer, Resources res, File cDir) {
        super(res, cDir);

        mMusicAnalyzer = musicAnalyzer;
        mPlayers = new ArrayList<>();

        int stepInterval = musicAnalyzer.getCurrentStepsInterval();

        DrumsPlayer drums = new DrumsPlayer(csoundObj, stepInterval, new DrumsSequencer());
        mMusicAnalyzer.addPositionListener(drums);
        mPlayers.add(drums);

        Notes baseNote = musicAnalyzer.getCurrentBaseNote();
        Scales scale = musicAnalyzer.getCurrentScale();

        SynthesizerPlayer synth = new SynthesizerPlayer(csoundObj, stepInterval, new SynthesizerSequencer(baseNote, scale));
        mMusicAnalyzer.addPositionListener(synth);
        mPlayers.add(synth);
    }

    @Override
    public void start() {
        startCSound();
    }

    @Override
    public void destroy() {
        destroyCSound();
    }

    @Override
    public int getCurrentTempo() {
        return mMusicAnalyzer.getCurrentTempo();
    }

    @Override
    public void onStep(long milliseconds) {
        mMusicAnalyzer.onStep(milliseconds);
    }

    @Override
    public int getCurrentStepsCount() {
        return mMusicAnalyzer.getCurrentStepsCount();
    }

    @Override
    public Integer[] getStepsIntervals() {
        return mMusicAnalyzer.getStepsIntervals();
    }

    @Override
    public void setBaseNote(Notes baseNote) {
        mMusicAnalyzer.setBaseNote(baseNote);
        for (BasePlayer bp : mPlayers) {
            bp.setBaseNote(baseNote);
        }
    }

    @Override
    public Notes getCurrentBaseNote() {
        return mMusicAnalyzer.getCurrentBaseNote();
    }

    @Override
    public void setScale(Scales scale) {
        mMusicAnalyzer.setScale(scale);
        for (BasePlayer bp : mPlayers) {
            bp.setScale(scale);
        }
    }

    @Override
    public Scales getCurrentScale() {
        return mMusicAnalyzer.getCurrentScale();
    }

    @Override
    public void setStepsInterval(int stepsInterval) {
        mMusicAnalyzer.setStepsInterval(stepsInterval);
        for (BasePlayer bp : mPlayers) {
            bp.setStepInterval(stepsInterval);
        }
    }

    @Override
    public int getCurrentStepsInterval() {
        return mMusicAnalyzer.getCurrentStepsInterval();
    }

}
