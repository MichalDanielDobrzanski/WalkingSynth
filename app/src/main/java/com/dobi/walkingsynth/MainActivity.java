package com.dobi.walkingsynth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.achartengine.GraphicalView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MActivity";

    private static final String PREFERENCES_NAME = "ValuesSet";
    private static final int MAX_OFFSET = 30;
    private SharedPreferences preferences;
    // accelerometer fields
    private int mOffset;
    private AccelerometerDetector mAccelDetector;
    private AccelerometerGraph mAccelGraph = new AccelerometerGraph();
    private static GraphicalView mView;
    private TextView mThreshValTextView;
    private TextView mStepCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // config prefs
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

        // initial graph option
        mOffset = 0;

        // UI default setup
        mView = mAccelGraph.getView(this);
        mView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mView.setOnClickListener(new View.OnClickListener() {
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
        graphLayout.addView(mView);

        createButtons();

        final SeekBar seekBar = (SeekBar)findViewById(R.id.offset_seekBar);
        seekBar.setMax(MAX_OFFSET);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAccelGraph.setThresholdVal(progress);
                //mAccelGraph.addOffset(1,progress);
                mThreshValTextView.setText(mThreshValTextView.getText() + Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // get text views
        mThreshValTextView = (TextView)findViewById(R.id.threshval_textView);
        mThreshValTextView.setText(Integer.toString(AccelerometerGraph.THRESH_INIT));
        mStepCountTextView = (TextView)findViewById(R.id.stepcount_textView);
        mStepCountTextView.setText(Integer.toString(0));

        // initialize accelerometer
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelDetector = new AccelerometerDetector(sensorManager,mView, mAccelGraph,preferences);
        mAccelDetector.setStepCountChangeListener(new OnStepCountChangeListener() {
            @Override
            public void onStepCountChange(int v) {
                mStepCountTextView.setText(Integer.toString(v));
            }
        });
    }

    private void createButtons() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout layout = (LinearLayout)findViewById(R.id.buttons_layout);
        for (int i = 0; i < AccOptions.OPTIONS.length; ++i) {
            final ToggleButton btn = new ToggleButton(this);
            btn.setTextOn(AccOptions.OPTIONS[i]);
            btn.setTextOff(AccOptions.OPTIONS[i]);
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
    }
}
