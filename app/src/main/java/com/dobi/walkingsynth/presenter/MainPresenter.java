package com.dobi.walkingsynth.presenter;

import android.content.SharedPreferences;

import com.dobi.walkingsynth.ApplicationMvp;
import com.dobi.walkingsynth.musicgeneration.core.CsoundStepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.AudioController;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;
import com.dobi.walkingsynth.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.stepdetection.OnStepListener;

import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_BASENOTE_KEY;
import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_SCALE_KEY;
import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_STEPS_INTERVAL_KEY;

public class MainPresenter implements ApplicationMvp.Presenter, OnStepListener {

    ApplicationMvp.View view;

    private final AudioController audioController;

    private final SharedPreferences sharedPreferences;

    private Note note;

    private Scale scale;

    private int interval;


    private int steps;

    private int tempo;

    private String time;

    public MainPresenter(SharedPreferences sharedPreferences, AccelerometerManager accelerometerManager,
                         AudioController audioController) {

        this.note = Note.getNoteByName(sharedPreferences.getString(PREFERENCES_VALUES_BASENOTE_KEY, Note.C.name()));

        this.scale = Scale.getScaleByName(sharedPreferences.getString(PREFERENCES_VALUES_SCALE_KEY, Scale.Pentatonic.name()));

        this.interval = sharedPreferences.getInt(PREFERENCES_VALUES_STEPS_INTERVAL_KEY, CsoundStepsAnalyzer.INITIAL_STEPS_INTERVAL);

        this.sharedPreferences = sharedPreferences;

        this.audioController = audioController;

        accelerometerManager.addOnStepChangeListener(audioController.getStepsAnalyzer());

        accelerometerManager.addOnStepChangeListener(audioController.getMusicAnalyzer());

        accelerometerManager.addOnStepChangeListener(this);
    }

    @Override
    public void attachView(ApplicationMvp.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void saveState() {
        sharedPreferences.edit()
                .putString(PREFERENCES_VALUES_BASENOTE_KEY, note.note)
                .putString(PREFERENCES_VALUES_SCALE_KEY, scale.name())
                .putInt(PREFERENCES_VALUES_STEPS_INTERVAL_KEY, interval)
                .apply();
    }

    @Override
    public void onResume() {
        audioController.start();
    }

    @Override
    public void onStop() {
        audioController.destroy();
    }

    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public Scale getScale() {
        return scale;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    @Override
    public void setNote(Note note) {
        this.note = note;
        if (view != null) {
            view.showNote(note);
        }
    }

    @Override
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    @Override
    public void setSteps(int steps) {
        this.steps = steps;
        if (view != null)
            view.showSteps(steps);
    }

    @Override
    public void setTempo(int tempo) {
        this.tempo = tempo;
        if (view != null)
            view.showTempo(tempo);
    }

    @Override
    public void setTime(String time) {
        this.time = time;
        if (view != null)
            view.showTime(time);
    }

    @Override
    public void onStepDetected(long milliseconds, int stepsCount) {
        this.steps = stepsCount;
        if (view != null) {
            view.showSteps(steps);
            view.showTempo(tempo);
        }
    }
}
