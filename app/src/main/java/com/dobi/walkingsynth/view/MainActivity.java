package com.dobi.walkingsynth.view;

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

import com.dobi.walkingsynth.ApplicationMvp;
import com.dobi.walkingsynth.MainApplication;
import com.dobi.walkingsynth.R;
import com.dobi.walkingsynth.model.musicgeneration.core.AudioPlayer;
import com.dobi.walkingsynth.model.musicgeneration.time.TimeCounter;
import com.dobi.walkingsynth.model.musicgeneration.utils.Note;
import com.dobi.walkingsynth.model.musicgeneration.utils.Scale;
import com.dobi.walkingsynth.model.stepdetection.AccelerometerManager;
import com.dobi.walkingsynth.presenter.MainPresenter;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

/**
 * TODO: refactor to MVP
 * TODO: create view abstractions in order to separate from implementations
 * TODO: use Dagger2 a lot
 * TODO: refactor Csound and accelerometer to use RxJava
 */
public class MainActivity extends AppCompatActivity implements ApplicationMvp.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MAX_STEPS_COUNT = 999;

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
    FrameLayout graphFrameLayout;

    @BindView(R.id.threshold_seek_bar)
    SeekBar thresholdSeekBar;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TimeCounter timeCounter;

    @Inject
    GraphView accelerometerGraph;

    @Inject
    AccelerometerManager accelerometerManager;

    @Inject
    AudioPlayer audioPlayer;

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

        graphFrameLayout.addView(accelerometerGraph.createView(this));
    }

    private void attachPresenter() {
        presenter = (ApplicationMvp.Presenter) getLastCustomNonConfigurationInstance();
        if (presenter == null) {
            presenter = new MainPresenter(sharedPreferences, accelerometerManager, audioPlayer);
        }
        Log.d(TAG, "attachPresenter: hashCode= " + presenter.hashCode());
        presenter.attachView(this);
    }

    private void initializeThresholdSeekBar() {
        Log.d(TAG, "initializeThresholdSeekBar: to : " + presenter.getProgressFromThreshold());
        thresholdSeekBar.setProgress(presenter.getProgressFromThreshold());

        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    presenter.setThresholdFromProgress(progress);
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
        presenter.saveState();

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
        presenter.onResume();
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
    public void initialize(Note note, Scale scale, int interval, int steps, int tempo, String time, Integer[] intervals) {
        noteTextView.setText(note.note);

        notesParameterView.initialize(Note.toStringArray(), note.note);
        notesParameterView.setCallback(n -> presenter.setNote(Note.getNoteByName(n)));

        scalesParameterView.initialize(Scale.toStringArray(), scale.name());
        scalesParameterView.setCallback(s -> {
            Log.d(TAG, "scalesParameterView callback: scale= " + s);
            presenter.setScale(Scale.getScaleByName(s));
        });

        stepsTextView.setText(String.valueOf(steps));

        tempoTextView.setText(String.valueOf(tempo));

        timeTextView.setText(time);

        initializeIntervals(intervals, interval);

        initializeThresholdSeekBar();
    }

    private void initializeIntervals(Integer[] intervals, int interval) {
        Observable.fromArray(intervals)
                .map(String::valueOf)
                .toList()
                .subscribe(strings -> intervalParameterView.initialize(strings.toArray(
                        new String[intervals.length]), String.valueOf(interval)))
                .dispose();

        intervalParameterView.setCallback(i -> {
            Log.d(TAG, "intervalParameterView callback: " + i);
            presenter.setInterval(Integer.valueOf(i));
        });
    }

    @Override
    public void showNote(Note note) {
        noteTextView.setText(note.note);
    }

    @Override
    public void showScale(Scale scale) {
        scalesParameterView.setValue(scale.name());
    }

    @Override
    public void showSteps(int steps) {
        stepsTextView.setText(String.valueOf(steps % MAX_STEPS_COUNT));
    }

    @Override
    public void showTempo(int tempo) {
        tempoTextView.setText(String.valueOf(tempo));
    }

    @Override
    public void showTime(String time) {
        // TODO: move  Timer to Model. Presenter would handle it.
    }
}
