package com.dobi.walkingsynth;

import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.walkingsynth.musicgeneration.core.CsoundAudioController;
import com.dobi.walkingsynth.musicgeneration.core.CsoundMusicAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.CsoundStepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.AudioController;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.MusicAnalyzer;
import com.dobi.walkingsynth.musicgeneration.core.interfaces.StepsAnalyzer;
import com.dobi.walkingsynth.musicgeneration.time.TimeCounter;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;
import com.dobi.walkingsynth.stepdetection.AccelerometerGraph;
import com.dobi.walkingsynth.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.stepdetection.AccelerometerProcessor;
import com.dobi.walkingsynth.stepdetection.OnStepListener;
import com.dobi.walkingsynth.view.ParameterView;
import com.dobi.walkingsynth.view.ParameterViewCallback;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * TODO: refactor to MVP
 * TODO: create view abstractions in order to separate from implementations
 * TODO: use Dagger2 a lot
 * TODO: refactor Csound and accelerometer to use RxJava
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PREFERENCES_VALUES_BASENOTE_KEY = "base-note";
    public static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";
    public static final String PREFERENCES_VALUES_SCALE_KEY = "scale";
    public static final String PREFERENCES_VALUES_STEPS_INTERVAL_KEY = "steps-interval";

    @BindView(R.id.stepCountTV)
    TextView mStepsTextView;

    @BindView(R.id.tempoValueTV)
    TextView mTempoTextView;

    @BindView(R.id.timeValueTV)
    TextView mTimeTextView;

    @BindView(R.id.graphFL)
    FrameLayout mGraphFrameLayout;

    @BindView(R.id.threshold_seek_bar)
    SeekBar thresholdSeekBar;

    @BindView(R.id.base_notes_wheel)
    ParameterView notesParameterView;

    @BindView(R.id.steps_interval_spinner)
    Spinner stepsSpinner;

    @BindView(R.id.scales_spinner)
    Spinner scalesSpinner;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TimeCounter timeCounter;

    @Inject
    AccelerometerProcessor accelerometerProcessor;

    @Inject
    AccelerometerGraph accelerometerGraph;

    @Inject
    SensorManager sensorManager;

    private Note currentNote;

    private AccelerometerManager mAccelerometerManager;
    private AudioController mAudioController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);

        Locale.setDefault(Locale.ENGLISH);

        ((MainApplication)getApplication()).getApplicationComponent().inject(this);

        initializeOrRestoreState(savedInstanceState);

        timeCounter.setView(mTimeTextView);

        mAudioController = CsoundAudioController.getInstance();

        mStepsTextView.setText(String.valueOf(formatStep(mAudioController.getStepsAnalyzer().getStepsCount())));
        mTempoTextView.setText(String.valueOf(mAudioController.getMusicAnalyzer().getTempo()));

        initializeNoteView();

        initializeScaleView();

        initializeStepView();

        initializeAccelerometer();
    }


    private void initializeOrRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: Initialized.");

            float threshold = sharedPreferences.getFloat(PREFERENCES_VALUES_THRESHOLD_KEY, AccelerometerProcessor.THRESHOLD_INITIAL);

            accelerometerProcessor.setThreshold(threshold);

            initializeThresholdSeekBar(threshold);

            String note = sharedPreferences.getString(PREFERENCES_VALUES_BASENOTE_KEY, Note.C.name());
            String scale = sharedPreferences.getString(PREFERENCES_VALUES_SCALE_KEY, Scale.Pentatonic.name());
            int steps = sharedPreferences.getInt(PREFERENCES_VALUES_STEPS_INTERVAL_KEY, CsoundStepsAnalyzer.INITIAL_STEPS_INTERVAL);

            Log.d(TAG, "initializeOrRestoreState() note: " + note + " scale: " + scale + " steps: " + steps);

            restoreSpinnersState(note, scale, steps);

            CsoundAudioController.createInstance(
                    new CsoundMusicAnalyzer(note, scale),
                    new CsoundStepsAnalyzer(steps),
                    getResources(),
                    getCacheDir());

        } else {
            initializeThresholdSeekBar((float)accelerometerProcessor.getThreshold());
        }
    }

    private void restoreSpinnersState(String note, String scale, int steps) {
        int pos = Note.getNoteByName(note).ordinal();

//        notesParameterView.setSelection(Note.getNoteByName(note).ordinal(), false);
        scalesSpinner.setSelection(Scale.valueOf(scale).ordinal(), false);
    }

    private void initializeNoteView() {
        notesParameterView.initialize(Note.toStringArray(), Note.C.name());
        notesParameterView.setCallback(new ParameterViewCallback() {
            @Override
            public void notify(String newValue) {
                currentNote = Note.getNoteByName(newValue);

                mAudioController.getMusicAnalyzer().setBaseNote(currentNote);
            }
        });
    }

    private void initializeScaleView() {
        ArrayAdapter<Scale> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, Scale.values());

        scalesSpinner.setAdapter(adapter);
        scalesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAudioController.getMusicAnalyzer().setScale(Scale.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeStepView() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, mAudioController.getStepsAnalyzer().getStepsIntervals());
        stepsSpinner.setAdapter(adapter);
        stepsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAudioController.getStepsAnalyzer().setStepsInterval(mAudioController.getStepsAnalyzer().getStepsIntervals()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeThresholdSeekBar(float thr) {
        thresholdSeekBar.setProgress(AccelerometerProcessor.thresholdToProgress(thr));
        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    accelerometerProcessor.setThreshold(AccelerometerProcessor.progressToThreshold(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public String formatStep(int stepCount) {
        return String.format(Locale.getDefault(), "%d", stepCount);
    }

    private void initializeAccelerometer() {

        mGraphFrameLayout.addView(accelerometerGraph.createView(this));

        mAccelerometerManager = new AccelerometerManager(sensorManager, accelerometerGraph, accelerometerProcessor);

        mAccelerometerManager.addOnStepChangeListener(mAudioController.getStepsAnalyzer());
        mAccelerometerManager.addOnStepChangeListener(mAudioController.getMusicAnalyzer());
        mAccelerometerManager.addOnStepChangeListener(new OnStepListener() {
            @Override
            public void onStepDetected(long milliseconds) {
                mStepsTextView.setText(formatStep(mAudioController.getStepsAnalyzer().getStepsCount()));
                mTempoTextView.setText(String.valueOf(mAudioController.getMusicAnalyzer().getTempo()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_threshold:
                saveThreshold();
                return true;
            case R.id.action_save_parameters:
                saveParameters();
                return true;
            case R.id.action_info:
                // TODO present info about me
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void saveThreshold() {
        sharedPreferences.edit()
                .putFloat(PREFERENCES_VALUES_THRESHOLD_KEY,
                        (float) accelerometerProcessor.getThreshold())
                .apply();
        Toast.makeText(this, R.string.toast_threshold_saved, Toast.LENGTH_SHORT).show();
    }

    private void saveParameters() {
        MusicAnalyzer musicAnalyzer = mAudioController.getMusicAnalyzer();
        Note currentBaseNote = musicAnalyzer.getBaseNote();
        Scale currentScale = musicAnalyzer.getScale();

        StepsAnalyzer stepsAnalyzer = mAudioController.getStepsAnalyzer();
        int stepsInterval = stepsAnalyzer.getStepsInterval();
        sharedPreferences.edit()
                .putString(PREFERENCES_VALUES_BASENOTE_KEY, currentBaseNote.note)
                .putString(PREFERENCES_VALUES_SCALE_KEY, currentScale.name())
                .putInt(PREFERENCES_VALUES_STEPS_INTERVAL_KEY, stepsInterval)
                .apply();
        Toast.makeText(this, R.string.toast_parameters_saved +
                " baseNote: " + currentBaseNote +
                " scale: " + currentScale +
                " stepsInterval: " + stepsInterval,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAccelerometerManager.startAccelerometerAndGraph();
        mAudioController.start();
    }

    @Override
    protected void onPause() {
        mAccelerometerManager.stopAccelerometerAndGraph();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        mAudioController.destroy();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
}
