package com.dobi.walkingsynth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.walkingsynth.musicgeneration.core.AudioPlayer;
import com.dobi.walkingsynth.musicgeneration.time.TimeCounter;
import com.dobi.walkingsynth.musicgeneration.utils.Note;
import com.dobi.walkingsynth.musicgeneration.utils.Scale;
import com.dobi.walkingsynth.presenter.MainPresenter;
import com.dobi.walkingsynth.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.stepdetection.graph.AccelerometerGraph;
import com.dobi.walkingsynth.view.ParameterView;

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
public class MainActivity extends AppCompatActivity implements ApplicationMvp.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.steps_text_view)
    TextView stepsTextView;

    @BindView(R.id.tempo_text_view)
    TextView tempoTextView;

    @BindView(R.id.time_text_view)
    TextView timeTextView;

    @BindView(R.id.note_parameter_view)
    ParameterView notesParameterView;

    @BindView(R.id.note_text_view)
    TextView noteTextView;

    @BindView(R.id.interval_parameter_view)
    ParameterView intervalParameterView;

    @BindView(R.id.scales_parameter_view)
    ParameterView scalesParameterView;

    @BindView(R.id.graph_frame_layout)
    FrameLayout mGraphFrameLayout;

    @BindView(R.id.threshold_seek_bar)
    SeekBar thresholdSeekBar;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TimeCounter timeCounter;

    @Inject
    AccelerometerGraph accelerometerGraph;

    @Inject
    AccelerometerManager accelerometerManager;

    @Inject
    AudioPlayer audioController;

    private ApplicationMvp.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);

        Locale.setDefault(Locale.ENGLISH);

        ((MainApplication) getApplication()).getApplicationComponent().inject(this);

        attachPresenter();

        presenter.initialize();

        timeCounter.setView(timeTextView);

        initializeNoteView();

        initializeScaleView();

        initializeStepView();

        mGraphFrameLayout.addView(accelerometerGraph.createView(this));
    }

    private void attachPresenter() {
        presenter = (ApplicationMvp.Presenter) getLastCustomNonConfigurationInstance();
        if (presenter == null) {
            presenter = new MainPresenter(sharedPreferences, accelerometerManager, audioController);
        }
        Log.d(TAG, "attachPresenter: hashCode= " + presenter.hashCode());
        presenter.attachView(this);
    }

    private void initializeNoteView() {
        notesParameterView.initialize(Note.toStringArray(), presenter.getNote().note);

        noteTextView.setText(presenter.getNote().note);

        notesParameterView.setCallback(n -> presenter.setNote(Note.getNoteByName(n)));
    }

    private void initializeScaleView() {
        scalesParameterView.initialize(Scale.toStringArray(), presenter.getScale().name());

        scalesParameterView.setCallback(s -> presenter.setScale(Scale.getScaleByName(s)));
    }

    private void initializeStepView() {
//        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
//                R.layout.support_simple_spinner_dropdown_item, mAudioController.getStepsAnalyzer().getStepsIntervals());
//        intervalParameterView.setAdapter(adapter);
//        intervalParameterView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mAudioController.getStepsAnalyzer().setStepsInterval(mAudioController.getStepsAnalyzer().getStepsIntervals()[position]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    private void initializeThresholdSeekBar(double thr) {
        thresholdSeekBar.setProgress(AccelerometerManager.thresholdToProgress(thr));

        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    accelerometerManager.setThreshold(AccelerometerManager.progressToThreshold(progress));
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
        accelerometerManager.saveThreshold();

        Toast.makeText(this, R.string.toast_threshold_saved, Toast.LENGTH_SHORT).show();
    }

    private void saveParameters() {
        presenter.saveState();

        Toast.makeText(this, R.string.toast_parameters_saved + " baseNote: " + presenter.getNote() +
                " scale: " + presenter.getScale() + " interval: " + presenter.getInterval(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initializeThresholdSeekBar(accelerometerManager.getThreshold());

        presenter.onResume();
    }

    @Override
    protected void onPause() {
        accelerometerManager.pauseAccelerometerAndGraph();

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");

        presenter.onStop();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");

        presenter.detachView();

        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    public void initialize(Note note, Scale scale, int interval, int steps, int tempo, String time) {
        noteTextView.setText(note.note);

        scalesParameterView.setValue(scale.name());

        intervalParameterView.setValue(Integer.toString(steps));

        stepsTextView.setText(String.valueOf(steps));

        tempoTextView.setText(String.valueOf(tempo));

        timeTextView.setText(time);
    }

    @Override
    public void showNote(Note note) {
        noteTextView.setText(note.note);
    }

    @Override
    public void showScale(Scale scale) {

    }

    @Override
    public void showSteps(int steps) {
        stepsTextView.setText(formatStep(steps));

    }

    @Override
    public void showTempo(int tempo) {
        tempoTextView.setText(String.valueOf(tempo));
    }

    @Override
    public void showTime(String time) {

    }
}
