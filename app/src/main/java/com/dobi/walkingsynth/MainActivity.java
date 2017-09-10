package com.dobi.walkingsynth;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import com.dobi.walkingsynth.musicgeneration.base.MusicCreator;
import com.dobi.walkingsynth.musicgeneration.synths.SynthesizerSequencer;
import com.dobi.walkingsynth.musicgeneration.time.TimeCounter;
import com.dobi.walkingsynth.stepdetection.accelerometer.AccelerometerProcessing;
import com.dobi.walkingsynth.stepdetection.plotting.AChartEngineAccelGraph;
import com.dobi.walkingsynth.stepdetection.plotting.AccelGraph;
import com.dobi.walkingsynth.stepdetection.steps.StepDetector;
import com.dobi.walkingsynth.stepdetection.steps.StepListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Starting point. Sets the whole UI.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String PREFERENCES_NAME = "Values";
    private static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";
    private SharedPreferences preferences;

    private StepDetector mAccelDetector;

    private AccelGraph mAccelGraph;

    private TextView mThreshValTextView;
    private TextView mStepCountTextView;
    private TextView mTempoValTextView;
    private TextView mTimeValTextView;

    private MusicCreator mMusicCreator;

    private TimeCounter mTimer;
    private Handler mHandler = new Handler();

    private final AccelerometerProcessing mAccelerometerProcessing = AccelerometerProcessing.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set default locale:
        Locale.setDefault(Locale.ENGLISH);

        // instantiate music analyzer
        mMusicCreator = new MusicCreator(getResources(), getCacheDir());

        // accelerometer graph setup:
        mAccelGraph = new AChartEngineAccelGraph();

        initializeNotesSpinner();
        initializeScalesSpinner();
        initializeStepsSpinner();

        mStepCountTextView = (TextView)findViewById(R.id.stepcount_textView);
        mStepCountTextView.setText(String.valueOf(0));

        mTempoValTextView = (TextView)findViewById(R.id.tempoval_textView);
        mTempoValTextView.setText(String.valueOf(mMusicCreator.getAnalyzer().getTempo()));

        mTimeValTextView = (TextView)findViewById(R.id.timeVal_textView);

        mTimer = new TimeCounter(mHandler,mTimeValTextView);
        mTimer.start();

        FrameLayout graphFrameLayout = (FrameLayout)findViewById(R.id.graph_frame_layout);
        graphFrameLayout.addView(mAccelGraph.createView(this));

        // initialize accelerometer
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelDetector = new StepDetector(sensorManager, mAccelGraph);
        mAccelDetector.setStepCountListener(new StepListener() {
            @Override
            public void onStepCount(int stepCount, long milisecStepTime) {
                mMusicCreator.getAnalyzer().onStep(milisecStepTime);
                mMusicCreator.invalidateStep(stepCount);

                mStepCountTextView.setText(Integer.toString(stepCount));

                mTempoValTextView.setText(String.valueOf(mMusicCreator.getAnalyzer().getTempo()));
            }
        });

        // seek bar configuration
        initializeSeekBar();
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
                    mAccelerometerProcessing.onProgressChange(progress);
                    mAccelGraph.onThresholdChange(mAccelerometerProcessing.getThreshold());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        // set string values for menu
        String[] titles = getResources().getStringArray(R.array.nav_drawer_items);
        for (int i = 0; i < titles.length; i++) {
            menu.getItem(i).setTitle(titles[i]);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_threshold:
                saveThreshold();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveThreshold() {
        preferences.edit().putFloat(
                PREFERENCES_VALUES_THRESHOLD_KEY,
                (float) AccelerometerProcessing.getInstance().getThreshold()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAccelDetector.startDetector();
        mMusicCreator.startCSound();
        mTimer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAccelDetector.stopDetector();
        mTimer.pause();
    }

    @Override
    protected void onStop() {
        mMusicCreator.destroyCSound();
        super.onStop();
    }
}
