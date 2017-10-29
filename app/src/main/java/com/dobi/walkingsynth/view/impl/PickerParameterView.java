package com.dobi.walkingsynth.view.impl;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dobi.walkingsynth.view.ParameterView;
import com.dobi.walkingsynth.view.ParameterViewCallback;

import java.util.Arrays;

public class PickerParameterView extends RecyclerView implements ParameterView {

    public static final String TAG = PickerParameterView.class.getSimpleName();

    private RecyclerViewAdapter recyclerViewAdapter;

    public PickerParameterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        recyclerViewAdapter = new RecyclerViewAdapter();

        setAdapter(recyclerViewAdapter);
    }

    @Override
    public void initialize(String[] values, String currentValue) {
        recyclerViewAdapter.initialize(values, currentValue);
    }

    @Override
    public void setValue(String value) {
        recyclerViewAdapter.setCurrentValue(value);
    }

    @Override
    public String getCurrentValue() {
        return recyclerViewAdapter.getCurrentValue();
    }

    @Override
    public void setCallback(ParameterViewCallback callback) {
        // TODO
    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

        private String[] values;

        private String currentValue;

        private int currentIndex;

        private OnItemSelectedListener localOnItemSelectedListener;

        RecyclerViewAdapter() {
            this.localOnItemSelectedListener = position -> {
                Log.d(TAG, "OnPosition: " + position);

                this.currentIndex = position;

                notifyDataSetChanged();
            };
        }

        public void initialize(String[] values, String value) {
            this.values = values;

            setValue(value);
        }

        void setCurrentValue(String value) {
            setValue(value);

            notifyDataSetChanged();
        }

        private void setValue(String value) {
            this.currentValue = value;
            this.currentIndex = Arrays.asList(values).indexOf(value);
        }

        String getCurrentValue() {
            return currentValue;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());

            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setPadding(10, 0, 10, 0);
            textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            textView.setGravity(Gravity.CENTER);

            return new RecyclerViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.textView.setText(values[position]);
            if (position == currentIndex) {
                holder.textView.setTextSize(20f);
            } else {
                holder.textView.setTextSize(14f);
            }
        }

        @Override
        public int getItemCount() {
            return values.length;
        }

        interface OnItemSelectedListener {
            void onPosition(int position);
        }

        class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            RecyclerViewHolder(View itemView) {
                super(itemView);
                this.textView = (TextView)itemView;
                this.textView.setOnClickListener(v -> {
                    localOnItemSelectedListener.onPosition(getAdapterPosition());
                });
            }
        }
    }
}
