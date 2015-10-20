package com.dobi.walkingsynth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import org.achartengine.GraphicalView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Ma";
    private static final int MAX_OFFSET = 30;

    private int mCurrentOption;
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
        mCurrentOption = Constants.ACC_MAGNITUDE;

        // UI default setup
        mView = mAccelGraph.getView(this);
        mView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout graphLayout = (LinearLayout)findViewById(R.id.graph_layout);
        graphLayout.addView(mView);

        final Button selectButton = (Button)findViewById(R.id.select_button);
        selectButton.setText(R.string.accel_mag);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentOption = (mCurrentOption + 1) % Constants.SERIES_COUNT;
                mAccelDetector.setOption(mCurrentOption);
                switch (mCurrentOption) {
                    case Constants.ACC_MAGNITUDE:
                        selectButton.setText(R.string.accel_mag);
                        break;
                    case Constants.ACC_GRAV_DIFF:
                        selectButton.setText(R.string.accel_grav);
                        break;
                    case Constants.ACC_ALL:
                        selectButton.setText(R.string.accel_all);
                        mAccelGraph.addNewSeries();
                        break;
                }
            }
        });

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
        mAccelDetector = new AccelerometerDetector(sensorManager,mView, mAccelGraph,mCurrentOption);
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
