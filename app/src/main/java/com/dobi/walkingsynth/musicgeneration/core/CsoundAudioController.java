package com.dobi.walkingsynth.musicgeneration.core;

import android.content.res.Resources;

import com.dobi.walkingsynth.musicgeneration.core.interfaces.AudioController;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.MusicAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.drums.DrumsPlayer;
import com.dobi.walkingsynth.musicgeneration.drums.DrumsSequencer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerPlayer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CsoundAudioController extends CsoundBase implements AudioController {

    private StepsAnalyzer stepsAnalyzer;

    private MusicAnalyzer musicAnalyzer;

    private List<CsoundPlayer> mPlayers;

    public CsoundAudioController(MusicAnalyzer musicAnalyzer, StepsAnalyzer stepsAnalyzer, Resources res, File file) {
        super(res, file);

        this.musicAnalyzer = musicAnalyzer;
        this.stepsAnalyzer = stepsAnalyzer;

        mPlayers = new ArrayList<>();

        int stepInterval = this.stepsAnalyzer.getStepsInterval();

        DrumsPlayer drums = new DrumsPlayer(csoundObj, stepInterval, new DrumsSequencer());
        this.stepsAnalyzer.addStepsListener(drums);
        this.musicAnalyzer.addPositionListener(drums);

        mPlayers.add(drums);

        Note baseNote = musicAnalyzer.getBaseNote();
        Scale scale = musicAnalyzer.getScale();

        SynthesizerPlayer synth = new SynthesizerPlayer(csoundObj, stepInterval, new SynthesizerSequencer(baseNote, scale));
        this.stepsAnalyzer.addStepsListener(synth);
        this.musicAnalyzer.addPositionListener(synth);

        mPlayers.add(synth);
    }

    public MusicAnalyzer getMusicAnalyzer() {
        return musicAnalyzer;
    }

    public StepsAnalyzer getStepsAnalyzer() {
        return stepsAnalyzer;
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
