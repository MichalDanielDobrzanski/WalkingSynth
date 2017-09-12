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

import com.dobi.walkingsynth.musicgeneration.core.CsoundMusicCreator;
import com.dobi.walkingsynth.musicgeneration.core.MusicCreator;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.time.TimeCounter;
import com.dobi.walkingsynth.stepdetection.AccelerometerGraph;
import com.dobi.walkingsynth.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.stepdetection.AccelerometerProcessor;
import com.dobi.walkingsynth.stepdetection.AchartEngineAccelerometerGraph;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String PREFERENCES_NAME = "Values";
    private static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";

    @BindView(R.id.stepCountTV)
    TextView mStepsTextView;

    @BindView(R.id.tempoValueTV)
    TextView mTempoTextView;

    @BindView(R.id.timeValueTV)
    TextView mTimeTextView;

    @BindView(R.id.graphFL)
    FrameLayout graphFrameLayout;

    private SharedPreferences preferences;

    private AccelerometerManager mAccelerometerManager;
    private AccelerometerGraph mAccelGraph;

    private MusicCreator mMusicCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);

        Locale.setDefault(Locale.ENGLISH);

        mMusicCreator = new CsoundMusicCreator(getResources(), getCacheDir());
        mAccelGraph = new AchartEngineAccelerometerGraph();

        initializeNotesSpinner();
        initializeScalesSpinner();
        initializeStepsSpinner();
        initializeSeekBar();

        mStepsTextView.setText(String.valueOf(0));
        mTempoTextView.setText(String.valueOf(mMusicCreator.getTempo()));

        TimeCounter.getInstance().startTimer();
        TimeCounter.getInstance().setView(mTimeTextView);

        graphFrameLayout.addView(mAccelGraph.createView(this));

        mAccelerometerManager = new AccelerometerManager(
                (SensorManager)getSystemService(Context.SENSOR_SERVICE),
                mAccelGraph);

        mAccelerometerManager.setOnStepChangeListener(new AccelerometerManager.StepListener() {
            @Override
            public void onStepChange(int stepCount, long milliseconds) {
                mMusicCreator.onStep(stepCount, milliseconds);

                mStepsTextView.setText(formatStep(stepCount));

                mTempoTextView.setText(String.valueOf(mMusicCreator.getTempo()));
            }
        });
    }

    private void initializeNotesSpinner() {
        ArrayList<String> notesList = new ArrayList<>();
        for ( String key : SynthesizerSequencer.notes.keySet()) {
            notesList.add(key);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,notesList);
        Spinner baseNotesSpinner = (Spinner) findViewById(R.id.base_notes_spinner);
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
        ArrayList<String> scalesList = new ArrayList<>();
        for ( String key : SynthesizerSequencer.scales.keySet())
        {
            scalesList.add(key);
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,scalesList);
        Spinner scalesSpinner = (Spinner) findViewById(R.id.scale_spinner);
        scalesSpinner.setAdapter(adapter2);
        scalesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMusicCreator.invalidateScale(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeStepsSpinner() {
        ArrayList<Integer> stepsList = new ArrayList<>();
        int l3 = SynthesizerSequencer.stepIntervals.length;
        for (int i = 0; i < l3; i++)
        {
            stepsList.add(SynthesizerSequencer.stepIntervals[i]);
        }
        ArrayAdapter<Integer> adapter3 = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,stepsList);
        Spinner timeIntervalsSpinner = (Spinner)findViewById(R.id.steps_interval_spinner);
        timeIntervalsSpinner.setAdapter(adapter3);
        timeIntervalsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMusicCreator.invalidateStepInterval(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeSeekBar() {
        final SeekBar seekBar = (SeekBar)findViewById(R.id.offset_seek_bar);
        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    AccelerometerProcessor.getInstance().onProgressChange(progress);
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
            case R.id.action_info:
                // TODO present info about me
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveThreshold() {
        // TODO: make this work
        preferences.edit().putFloat(
                PREFERENCES_VALUES_THRESHOLD_KEY,
                (float) AccelerometerProcessor.getInstance().getThreshold()).apply();
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
}
