package com.dobi.walkingsynth.musicgeneration.core;

import android.content.res.Resources;

import com.dobi.walkingsynth.musicgeneration.drums.DrumsPlayer;
import com.dobi.walkingsynth.musicgeneration.drums.DrumsSequencer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerPlayer;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CsoundAudioPlayer extends CsoundBase implements AudioPlayer {

    private StepsAnalyzer stepsAnalyzer;

    private TempoAnalyzer tempoAnalyzer;

    private List<CsoundPlayer> players;

    public CsoundAudioPlayer(TempoAnalyzer tempoAnalyzer, StepsAnalyzer stepsAnalyzer, Resources res, File file) {
        super(res, file);

        this.tempoAnalyzer = tempoAnalyzer;
        this.stepsAnalyzer = stepsAnalyzer;

        this.players = new ArrayList<>();
    }

    public TempoAnalyzer getTempoAnalyzer() {
        return tempoAnalyzer;
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

    @Override
    public void initialize(Note note, Scale scale, int interval) {
        SynthesizerPlayer synth = new SynthesizerPlayer(csoundObj, interval,
                new SynthesizerSequencer(note, scale));

        this.stepsAnalyzer.addStepsListener(synth);
        this.tempoAnalyzer.addPositionListener(synth);
        players.add(synth);

        DrumsPlayer drums = new DrumsPlayer(csoundObj, interval,
                new DrumsSequencer());

        this.stepsAnalyzer.addStepsListener(drums);
        this.tempoAnalyzer.addPositionListener(drums);
        players.add(drums);
    }

    @Override
    public void invalidate(Note note) {
        for (CsoundPlayer player : players) {
            player.invalidateNote(note);
        }
    }

    @Override
    public void invalidate(Scale scale) {
        for (CsoundPlayer player : players) {
            player.invalidateScale(scale);
        }
    }

    @Override
    public void invalidate(int interval) {
        for (CsoundPlayer player : players) {
            player.invalidateInterval(interval);
        }
    }

}
