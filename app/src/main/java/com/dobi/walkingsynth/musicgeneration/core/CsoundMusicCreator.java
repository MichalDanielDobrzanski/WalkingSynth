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

public class CsoundMusicCreator extends CsoundBaseSetup implements MusicCreator {

    private MusicAnalyzer mMusicAnalyzer;

    private List<BasePlayer> mPlayers;

    private static CsoundMusicCreator mInstance;

    public static void createInstance(MusicAnalyzer musicAnalyzer, Resources res, File cDir, int note, String scale, int steps) {
        if (mInstance == null) {
            mInstance = new CsoundMusicCreator(musicAnalyzer, res, cDir, note, scale, steps);
        }
    }

    public static CsoundMusicCreator getInstance() {
        return mInstance;
    }

    private CsoundMusicCreator(MusicAnalyzer musicAnalyzer, Resources res, File cDir, int note, String scale, int steps) {
        super(res, cDir);

        mMusicAnalyzer = musicAnalyzer;
        initializePlayers(note, scale, steps);
    }

    private void initializePlayers(int note, String scale, int steps) {
        mPlayers = new ArrayList<>();

        DrumsPlayer drums = new DrumsPlayer(csoundObj, steps, new DrumsSequencer());
        mMusicAnalyzer.addPositionListener(drums);
        mPlayers.add(drums);

        SynthesizerPlayer synth = new SynthesizerPlayer(csoundObj, steps, new SynthesizerSequencer(note, scale));
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

    public int getCurrentTempo() {
        return mMusicAnalyzer.getTempo();
    }

    public void onStep(long milliseconds) {
        mMusicAnalyzer.onStep(milliseconds);
    }

    @Override
    public int getStepCount() {
        return mMusicAnalyzer.getStepsCount();
    }

    public void invalidateBaseNote(Notes note) {
        for (BasePlayer bp : mPlayers) {
            bp.setNote(note);
        }
    }

    public void invalidateScale(Scales scale) {
        for (BasePlayer bp : mPlayers) {
            bp.setScale(scale);
        }
    }

    public void invalidateStep(int idx) {
        for (BasePlayer bp : mPlayers) {
            bp.setStepInterval(idx);
        }
    }
}
