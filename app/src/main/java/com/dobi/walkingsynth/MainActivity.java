package com.dobi.walkingsynth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dobi.walkingsynth.music.MusicCreator;
import com.dobi.walkingsynth.music.TimeCounter;

import org.achartengine.GraphicalView;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Starting point. Sets the whole UI.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MActivity";

    private static final String PREFERENCES_NAME = "Values";
    private static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";
    private SharedPreferences preferences;
    private int mStepCount = 0;
    private AccelerometerDetector mAccelDetector;
    private AccelerometerGraph mAccelGraph = new AccelerometerGraph();
    private TextView mThreshValTextView;
    private TextView mStepCountTextView;
    private TextView mTempoValTextView;
    private TextView mTimeValTextView;
    private MusicCreator mMusicCreator;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set default locale:
        Locale.setDefault(Locale.ENGLISH);

        // instantiate music analyzer
        mMusicCreator = new MusicCreator(getResources(),getCacheDir());

        // config prefs
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        float threshVal = preferences.getFloat(PREFERENCES_VALUES_THRESHOLD_KEY, AccelerometerProcessing.THRESH_INIT);
        AccelerometerProcessing.setThreshold(threshVal);

        // configure spinners
        Spinner baseNotesSpinner = (Spinner) findViewById(R.id.base_notes_spinner);
        ArrayAdapter adapter =  ArrayAdapter.createFromResource(this, R.array.base_notes, R.layout.support_simple_spinner_dropdown_item);
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

        Spinner scalesSpinner = (Spinner) findViewById(R.id.scale_spinner);
        ArrayAdapter adapter2 =  ArrayAdapter.createFromResource(this, R.array.scales, R.layout.support_simple_spinner_dropdown_item);
        scalesSpinner.setAdapter(adapter2);
        scalesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMusicCreator.invalidateScale(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // get and configure text views
        mThreshValTextView = (TextView)findViewById(R.id.threshval_textView);
        formatThreshTextView(threshVal);
        mStepCountTextView = (TextView)findViewById(R.id.stepcount_textView);
        mStepCountTextView.setText(String.valueOf(0));
        mTempoValTextView = (TextView)findViewById(R.id.tempoval_textView);
        mTempoValTextView.setText(String.valueOf(mMusicCreator.getAnalyzer().getTempo()));
        mTimeValTextView = (TextView)findViewById(R.id.timeVal_textView);

        // timer counter
        TimeCounter timer = new TimeCounter(mHandler,mTimeValTextView);
        timer.start();

        // UI default setup
        GraphicalView view = mAccelGraph.getView(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle the click event on the chart
                if (mAccelGraph.isPainting)
                    mAccelGraph.isPainting(false);
                else {
                    mAccelGraph.isPainting(true);
                }
            }
        });
        LinearLayout graphLayout = (LinearLayout)findViewById(R.id.graph_layout);
        graphLayout.addView(view);

        // dynamic button creation
        createButtons();

        // initialize accelerometer
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelDetector = new AccelerometerDetector(sensorManager, view, mAccelGraph,preferences);
        mAccelDetector.setStepCountChangeListener(new OnStepCountChangeListener() {
            @Override
            public void onStepCountChange(long eventMsecTime) {
                ++mStepCount;
                mStepCountTextView.setText(String.valueOf(mStepCount));
                mMusicCreator.getAnalyzer().onStep(eventMsecTime);
                mMusicCreator.invalidateStep(mStepCount);
                mTempoValTextView.setText(
                        String.valueOf(mMusicCreator.getAnalyzer().getTempo()));
            }
        });
        // seek bar configuration
        final SeekBar seekBar = (SeekBar)findViewById(R.id.offset_seekBar);
        seekBar.setMax(130 - 90);
        seekBar.setProgress((int) AccelerometerProcessing.getThreshold());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                AccelerometerProcessing.changeThreshold(progress);
                formatThreshTextView(AccelerometerProcessing.getThreshold());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void formatThreshTextView(double v) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mThreshValTextView.setText(String.valueOf(df.format(v)));
    }

    private void createButtons() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout layout = (LinearLayout)findViewById(R.id.buttons_layout);
        for (int i = 0; i < AccelerometerSignals.OPTIONS.length; ++i) {
            final ToggleButton btn = new ToggleButton(this);
            btn.setTextOn(AccelerometerSignals.OPTIONS[i]);
            btn.setTextOff(AccelerometerSignals.OPTIONS[i]);
            btn.setLayoutParams(params);
            btn.setChecked(true);
            final int opt = i; // convert to flag convention
            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mAccelDetector.setVisibility(opt, isChecked);
                }
            });
            layout.addView(btn);
        }
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
        preferences.edit().putFloat(PREFERENCES_VALUES_THRESHOLD_KEY, (float) AccelerometerProcessing.getThreshold()).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume register Listeners");
        mAccelDetector.startDetector();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause UNregister Listeners");
        mAccelDetector.stopDetector();
        mStepCount = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMusicCreator.destroy();
    }

}
