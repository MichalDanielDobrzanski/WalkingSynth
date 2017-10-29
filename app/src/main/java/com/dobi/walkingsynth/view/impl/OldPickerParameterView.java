package com.dobi.walkingsynth.view.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.NumberPicker;

import com.dobi.walkingsynth.view.ParameterView;
import com.dobi.walkingsynth.view.ParameterViewCallback;

// TODO CREATE custom wheel view
public class OldPickerParameterView extends NumberPicker implements ParameterView {

    public static final String TAG = OldPickerParameterView.class.getSimpleName();

    private String[] values;

    private String currentValue;

    private ParameterViewCallback callback;

    public OldPickerParameterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initialize(String[] values, String currentValue) {
        this.values = values;
        this.currentValue = currentValue;

        this.setMinValue(0);
        this.setMaxValue(values.length - 1);

        this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        this.setFormatter(value -> values[value]);

        this.setOnValueChangedListener((picker, oldVal, newVal) -> {
            this.currentValue = values[newVal];
            Log.d(TAG, "onValueChange: newValue= " + newVal + " current= " + this.currentValue);
            if (callback != null)
                callback.notify(currentValue);
        });

    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public String getCurrentValue() {
        return currentValue;
    }

    @Override
    public void setCallback(ParameterViewCallback callback) {
        this.callback = callback;
    }

}
