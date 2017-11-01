package com.dobi.walkingsynth.view;

public interface ParameterView {

    void initialize(String[] values, String currentValue);

    void setValue(String value);

    String getCurrentValue();

    void setCallback(Callback callback);

    interface Callback {

        void notify(String newValue);
    }

}
