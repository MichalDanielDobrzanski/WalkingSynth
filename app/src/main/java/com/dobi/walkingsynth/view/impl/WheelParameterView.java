package com.dobi.walkingsynth.view.impl;

import android.content.Context;
import android.util.AttributeSet;

import com.dobi.walkingsynth.R;
import com.dobi.walkingsynth.view.ParameterView;
import com.github.shchurov.horizontalwheelview.HorizontalWheelView;

public class WheelParameterView extends HorizontalWheelView implements ParameterView {

    private String[] values;

    private String currentValue;

    private ParameterView.Callback callback;

    public WheelParameterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setNormaColor(context.getResources().getColor(R.color.colorAccent));
        setActiveColor(context.getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void initialize(String[] values, String currentValue) {
        this.values = values;
        this.currentValue = currentValue;

        setMarksCount(values.length);
        setEndLock(false);
        setSnapToMarks(true);

        setListener(new Listener() {
            @Override
            public void onScrollStateChanged(int state) {
                super.onScrollStateChanged(state);

                if (state == 0) {
                    if (callback != null)
                        callback.notify(getCurrentValue());
                }
            }
        });
    }

    @Override
    public void setValue(String value) {
    }

    @Override
    public String getCurrentValue() {
        int index = (int)Math.round(getDegreesAngle() * values.length / 360);
        if (index == values.length)
            index = 0;
        else if (index < 0) {
            index += values.length;
        }
        return values[index];
    }

    @Override
    public void setCallback(ParameterView.Callback callback) {
        this.callback = callback;
    }
}
