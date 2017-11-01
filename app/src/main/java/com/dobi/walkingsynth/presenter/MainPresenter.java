package com.dobi.walkingsynth.presenter;

import android.content.SharedPreferences;
import android.util.Log;

import com.dobi.walkingsynth.ApplicationMvp;
import com.dobi.walkingsynth.model.musicgeneration.core.AudioPlayer;
import com.dobi.walkingsynth.model.musicgeneration.core.StepsAnalyzer;
import com.dobi.walkingsynth.model.musicgeneration.core.TempoAnalyzer;
import com.dobi.walkingsynth.model.musicgeneration.utils.Note;
import com.dobi.walkingsynth.model.musicgeneration.utils.Scale;
import com.dobi.walkingsynth.model.stepdetection.AccelerometerManager;

import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_BASENOTE_KEY;
import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_SCALE_KEY;
import static com.dobi.walkingsynth.di.MainApplicationModule.PREFERENCES_VALUES_STEPS_INTERVAL_KEY;

public class MainPresenter implements ApplicationMvp.Presenter, AccelerometerManager.OnStepListener,
        TempoAnalyzer.TempoListener {

    public static final String TAG = MainPresenter.class.getSimpleName();

    ApplicationMvp.View view;

    private final AudioPlayer audioPlayer;

    private final SharedPreferences sharedPreferences;

    private final AccelerometerManager accelerometerManager;

    private Note note;

    private Scale scale;

    private int interval;

    private int steps;

    private int tempo;

    private String time;

    public MainPresenter(SharedPreferences sharedPreferences, AccelerometerManager accelerometerManager,
                         AudioPlayer audioPlayer) {
        this.sharedPreferences = sharedPreferences;

        this.accelerometerManager = accelerometerManager;
        this.accelerometerManager.addOnStepChangeListener(this);

        this.note = Note.getNoteByName(sharedPreferences.getString(PREFERENCES_VALUES_BASENOTE_KEY, Note.C.name()));

        this.scale = Scale.getScaleByName(sharedPreferences.getString(PREFERENCES_VALUES_SCALE_KEY, Scale.Pentatonic.name()));

        this.interval = sharedPreferences.getInt(PREFERENCES_VALUES_STEPS_INTERVAL_KEY, StepsAnalyzer.INITIAL_STEPS_INTERVAL);

        this.steps = 0;

        this.tempo = audioPlayer.getTempoAnalyzer().getTempo();

        this.audioPlayer = audioPlayer;

        this.audioPlayer.initialize(note, scale, interval);

        this.audioPlayer.getTempoAnalyzer().addTempoListener(this);
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
        if (view != null) {
            view.initialize(note, scale, interval, steps, tempo, time,
                    audioPlayer.getStepsAnalyzer().getIntervals());

        }
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
        audioPlayer.start();
        accelerometerManager.resumeAccelerometerAndGraph();
    }

    @Override
    public void onStop() {
        audioPlayer.destroy();
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
        audioPlayer.invalidate(note);
    }

    @Override
    public void setScale(Scale scale) {
        this.scale = scale;

        if (view != null)
            view.showScale(scale);

        audioPlayer.invalidate(scale);
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
        audioPlayer.invalidate(interval);
    }

    @Override
    public void setSteps(int steps) {
        this.steps = steps;

        if (view != null)
            view.showSteps(steps);
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

        audioPlayer.getStepsAnalyzer().onStepDetected(milliseconds, stepsCount);
        audioPlayer.getTempoAnalyzer().onStepDetected(milliseconds, stepsCount);
    }

    @Override
    public void invalidateTempo(int tempo) {
        Log.d(TAG, "invalidateTempo: " + tempo);

        this.tempo = tempo;

        if (view != null)
            view.showTempo(tempo);
    }
}
