package com.dobi.walkingsynth;

import android.widget.TextView;

import com.dobi.walkingsynth.model.musicgeneration.utils.Note;
import com.dobi.walkingsynth.model.musicgeneration.utils.Scale;

import io.reactivex.Observable;

public interface ApplicationMvp {

    interface View {
        void initialize(Note note, Scale scale, int interval, int steps, int tempo, String time, Integer[] intervals);

        void showNote(Note note);

        void showScale(Scale scale);

        void showSteps(int steps);

        void showTempo(int tempo);

        void showTime(String time);

        TextView getTimeView();
    }

    interface Presenter {
        void attachView(View view);

        void onDestroy();

        void onResume();

        void onStop();

        void initialize();

        void saveState();

        Note getNote();

        Scale getScale();

        int getInterval();

        void setNote(Note note);

        void setScale(Scale scale);

        void setInterval(int interval);

        void setSteps(int steps);

        int getProgressFromThreshold();

        void setThresholdProgressObservable(Observable<Integer> observable);
    }

    interface Model {

    }
}
