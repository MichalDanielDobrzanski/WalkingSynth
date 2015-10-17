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

import org.achartengine.GraphicalView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Ma";
    private int mCurrentOption;
    // accelerometer fields
    private AccelerometerDetector mAccelDetector;
    // graph fields
    private static GraphicalView mView;
    private AccelerometerGraph mAccelGraph = new AccelerometerGraph();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI default setup
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.activity_main_layout);
        mView = mAccelGraph.getView(this);
        mView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mainLayout.addView(mView);

        final Button selectButton = (Button)findViewById(R.id.select_button);
        selectButton.setText(R.string.accel_mag);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentOption = (mCurrentOption + 1) % 2;
                mAccelDetector.setOption(mCurrentOption);
                switch (mCurrentOption) {
                    case Constants.ACC_MAGNITUDE:
                        selectButton.setText(R.string.accel_mag);
                        break;
                    case Constants.ACC_GRAV_DIFF:
                        selectButton.setText(R.string.accel_grav);
                        break;
                }
            }
        });

        mCurrentOption = Constants.ACC_MAGNITUDE;
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // initialize accererometer
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
