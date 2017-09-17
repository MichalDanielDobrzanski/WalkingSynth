package com.dobi.walkingsynth;

import android.content.Context;
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

import com.dobi.walkingsynth.musicgeneration.core.CsoundMusicCreator;
import com.dobi.walkingsynth.musicgeneration.core.MusicCreator;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.time.TimeCounter;
import com.dobi.walkingsynth.stepdetection.AccelerometerGraph;
import com.dobi.walkingsynth.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.stepdetection.AccelerometerProcessor;
import com.dobi.walkingsynth.stepdetection.AchartEngineAccelerometerGraph;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String PREFERENCES_NAME = "Values";
    private static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";
    private static final String PREFERENCES_VALUES_BASENOTE_KEY = "base-note";
    private static final String PREFERENCES_VALUES_SCALE_KEY = "scale";
    private static final String PREFERENCES_VALUES_STEPS_INTERVAL_KEY = "steps-interval";

    @BindView(R.id.stepCountTV)
    TextView mStepsTextView;

    @BindView(R.id.tempoValueTV)
    TextView mTempoTextView;

    @BindView(R.id.timeValueTV)
    TextView mTimeTextView;

    @BindView(R.id.graphFL)
    FrameLayout mGraphFrameLayout;

    @BindView(R.id.threshold_seek_bar)
    SeekBar mThresholdSeekBar;

    @BindView(R.id.base_notes_spinner)
    Spinner baseNotesSpinner;

    @BindView(R.id.steps_interval_spinner)
    Spinner mStepsIntervalSpinner;

    @BindView(R.id.scales_spinner)
    Spinner scalesSpinner;

    private SharedPreferences mPreferences;

    private AccelerometerManager mAccelerometerManager;

    private MusicCreator mMusicCreator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);

        Locale.setDefault(Locale.ENGLISH);

        mPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: Initialized.");

            float threshold = mPreferences.getFloat(PREFERENCES_VALUES_THRESHOLD_KEY, AccelerometerProcessor.THRESHOLD_INITIAL);
            AccelerometerProcessor.getInstance().setThreshold(threshold);
            initializeThresholdSeekBar(threshold);

            CsoundMusicCreator.createInstance(getResources(), getCacheDir());
            TimeCounter.getInstance().startTimer();
        } else {
            initializeThresholdSeekBar((float)AccelerometerProcessor.getInstance().getThreshold());
        }

        TimeCounter.getInstance().setView(mTimeTextView);

        mMusicCreator = CsoundMusicCreator.getInstance();

        mStepsTextView.setText(String.valueOf(formatStep(mMusicCreator.getStepCount())));
        mTempoTextView.setText(String.valueOf(mMusicCreator.getCurrentTempo()));

        initializeNotesSpinner();
        initializeScalesSpinner();
        initializeStepsSpinner();

        AccelerometerGraph accelerometerGraph = AchartEngineAccelerometerGraph.getInstance();
        mGraphFrameLayout.addView(accelerometerGraph.createView(this));

        mAccelerometerManager = new AccelerometerManager(
                (SensorManager)getSystemService(Context.SENSOR_SERVICE),
                accelerometerGraph);

        mAccelerometerManager.setOnStepChangeListener(new AccelerometerManager.StepListener() {
            @Override
            public void onStepDetected(long milliseconds) {
                mMusicCreator.onStep(milliseconds);

                mStepsTextView.setText(formatStep(mMusicCreator.getStepCount()));
                mTempoTextView.setText(String.valueOf(mMusicCreator.getCurrentTempo()));
            }
        });
    }

    private void initializeNotesSpinner() {
        ArrayAdapter<SynthesizerSequencer.Notes> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item,
                SynthesizerSequencer.Notes.values());

        baseNotesSpinner.setAdapter(adapter);
        baseNotesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMusicCreator.invalidateBaseNote(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeScalesSpinner() {
        ArrayAdapter<SynthesizerSequencer.Scales> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item,
                SynthesizerSequencer.Scales.values());

        scalesSpinner.setAdapter(adapter);
        scalesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMusicCreator.invalidateScale(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeStepsSpinner() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item,
                SynthesizerSequencer.stepIntervals);
        mStepsIntervalSpinner.setAdapter(adapter);
        mStepsIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMusicCreator.invalidateStepInterval(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeThresholdSeekBar(float thr) {
        mThresholdSeekBar.setProgress(AccelerometerProcessor.thresholdToProgress(thr));
        mThresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    AccelerometerProcessor.getInstance()
                            .setThreshold(AccelerometerProcessor.progressToThreshold(progress));
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
        mPreferences.edit()
                .putFloat(PREFERENCES_VALUES_THRESHOLD_KEY,
                        (float) AccelerometerProcessor.getInstance().getThreshold())
                .apply();
        Toast.makeText(this, R.string.toast_threshold_saved, Toast.LENGTH_SHORT).show();
    }

    private void saveParameters() {

        Toast.makeText(this,R.string.toast_parameters_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAccelerometerManager.startAccelerometerAndGraph();
        mMusicCreator.start();
    }

    @Override
    protected void onPause() {
        mAccelerometerManager.stopAccelerometerAndGraph();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        mMusicCreator.destroy();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
}
