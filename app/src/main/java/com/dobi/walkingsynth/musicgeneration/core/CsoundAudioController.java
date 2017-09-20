package com.dobi.walkingsynth.musicgeneration.core;

import android.content.res.Resources;

import com.dobi.walkingsynth.musicgeneration.core.interfaces.MusicAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.AudioController;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.drums.DrumsPlayer;
import com.dobi.walkingsynth.musicgeneration.drums.DrumsSequencer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerPlayer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scales;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CsoundAudioController extends CsoundBase implements AudioController {

    private StepsAnalyzer mStepsAnalyzer;
    private MusicAnalyzer mMusicAnalyzer;
    private List<CsoundPlayer> mPlayers;

    private static CsoundAudioController mInstance;

    public static void createInstance(MusicAnalyzer musicAnalyzer, StepsAnalyzer stepsAnalyzer, Resources res, File cDir) {
        if (mInstance == null) {
            mInstance = new CsoundAudioController(musicAnalyzer, stepsAnalyzer, res, cDir);
        }
    }

    public static CsoundAudioController getInstance() {
        return mInstance;
    }

    private CsoundAudioController(MusicAnalyzer musicAnalyzer, StepsAnalyzer stepsAnalyzer, Resources res, File cDir) {
        super(res, cDir);

        mMusicAnalyzer = musicAnalyzer;
        mStepsAnalyzer = stepsAnalyzer;

        mPlayers = new ArrayList<>();

        int stepInterval = mStepsAnalyzer.getStepsInterval();

        DrumsPlayer drums = new DrumsPlayer(csoundObj, stepInterval, new DrumsSequencer());
        mStepsAnalyzer.addStepsListener(drums);
        mMusicAnalyzer.addPositionListener(drums);

        mPlayers.add(drums);

        Note baseNote = musicAnalyzer.getBaseNote();
        Scales scale = musicAnalyzer.getScale();

        SynthesizerPlayer synth = new SynthesizerPlayer(csoundObj, stepInterval, new SynthesizerSequencer(baseNote, scale));
        mStepsAnalyzer.addStepsListener(synth);
        mMusicAnalyzer.addPositionListener(synth);

        mPlayers.add(synth);
    }

    public MusicAnalyzer getMusicAnalyzer() {
        return mMusicAnalyzer;
    }

    public StepsAnalyzer getStepsAnalyzer() {
        return mStepsAnalyzer;
    }

    @Override
    public void start() {
        startCSound();
    }

    @Override
    public void destroy() {
        destroyCSound();
    }

}
