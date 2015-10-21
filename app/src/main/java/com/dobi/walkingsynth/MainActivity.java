package com.dobi.walkingsynth;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import org.achartengine.GraphicalView;

import java.util.EnumSet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MActivity";
    private static final int MAX_OFFSET = 30;

    // accelerometer fields
    private AccelerometerDetector mAccelDetector;
    // graph fields
    private static GraphicalView mView;
    private AccelerometerGraph mAccelGraph = new AccelerometerGraph();
    private int mOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initial graph option
        mOffset = 0;

        // UI default setup
        mView = mAccelGraph.getView(this);
        mView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout graphLayout = (LinearLayout)findViewById(R.id.graph_layout);
        graphLayout.addView(mView);

        createButtons();

        final SeekBar seekBar = (SeekBar)findViewById(R.id.offset_seekBar);
        seekBar.setMax(MAX_OFFSET);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAccelGraph.addOffset(1,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // initialize accererometer
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelDetector = new AccelerometerDetector(sensorManager,mView, mAccelGraph);
    }

    private void createButtons() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout layout = (LinearLayout)findViewById(R.id.buttons_layout);
        for (int i = 0; i < Constants.OPTIONS.length; ++i) {
            final ToggleButton btn = new ToggleButton(this);
            btn.setTextOn(Constants.OPTIONS[i]);
            btn.setTextOff(Constants.OPTIONS[i]);
            btn.setLayoutParams(params);
            btn.setChecked(true);
            final int opt = i; // convert to flag convention
            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mAccelDetector.setCurrentOption(Constants.AccOptions.values()[opt]);
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
