package com.dobi.walkingsynth.view.impl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dobi.walkingsynth.view.GraphView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class AchartEngineGraphView implements GraphView {

    public static final String TAG = AchartEngineGraphView.class.getSimpleName();

    private static final String THRESHOLD_SERIES_TITLE = "Threshold";
    private static final String ACCELEROMETER_SERIES_TITLE = "Accelerometer data";

    private static final int MAX_POINTS = 100;
    private static final int OFFSET = 4;
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 20;

    private int pointCount = 0;

    private TimeSeries mThresholdSeries;
    private TimeSeries accelerometerSeries;

    private XYMultipleSeriesRenderer renderer;

    private XYMultipleSeriesDataset dataset;

    private GraphicalView view;

    private double currentThreshold;

    private boolean resumed = false;

    public AchartEngineGraphView() {

        this.renderer = new XYMultipleSeriesRenderer();
        this.dataset = new XYMultipleSeriesDataset();

        prepareAccelerometerSeries();

        prepareThresholdSeries();

        prepareGraph();
    }

    @Override
    public void pause() {
//        accelerometerSeries.clear();
//        mThresholdSeries.clear();
//        pointCount = 0;
    }

    private void prepareAccelerometerSeries() {
        accelerometerSeries = new TimeSeries(ACCELEROMETER_SERIES_TITLE);

        dataset.addSeries(accelerometerSeries);

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setColor(Color.BLUE);

        this.renderer.addSeriesRenderer(renderer);
    }

    private void prepareThresholdSeries() {
        mThresholdSeries = new TimeSeries(THRESHOLD_SERIES_TITLE);
        dataset.addSeries(mThresholdSeries);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(3f);
        renderer.setColor(Color.BLACK);
        this.renderer.addSeriesRenderer(renderer);
    }

    private void prepareGraph() {
        renderer.clearXTextLabels();
        renderer.setYAxisMin(MIN_Y);
        renderer.setYAxisMax(MAX_Y);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setApplyBackgroundColor(true);
        renderer.setXLabels(0);
        renderer.setClickEnabled(false);
        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false, false);
    }

    @Override
    public View createView(Context context) {
        view =  ChartFactory.getLineChartView(context, dataset, renderer);
        view.setLayoutParams(new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public void resume() {
        resumed = true;
    }

    @Override
    public void invalidate(long eventTime, double accelerometerValue, double threshold) {
        currentThreshold = threshold;

        rewriteAccelerometerPast(eventTime, accelerometerValue);

        Log.d(TAG, "invalidateTempo() at time: " + eventTime + " accelValue: " + accelerometerValue + " thresh: " + currentThreshold);

        accelerometerSeries.add(eventTime, padValue(accelerometerValue));
        if (pointCount == MAX_POINTS)
            accelerometerSeries.remove(0);

        mThresholdSeries.add(eventTime, padValue(currentThreshold));
        if (pointCount == MAX_POINTS)
            mThresholdSeries.remove(0);

        if (view != null)
            view.repaint();

        if (pointCount < MAX_POINTS)
            ++pointCount;
    }

    private void rewriteAccelerometerPast(long eventTime, double accelerometerValue) {
        if (resumed && accelerometerSeries.getItemCount() == MAX_POINTS) {
            Log.d(TAG, "rewriteAccelerometerPast: " + accelerometerSeries.getItemCount());
            for (int i = MAX_POINTS; i > 0; i--) {
                accelerometerSeries.remove(0);
                mThresholdSeries.remove(0);
            }
            for (int i = MAX_POINTS; i > 0; i--) {
                long pastTime = eventTime - 20 * i;
                accelerometerSeries.add(pastTime, accelerometerValue);
                mThresholdSeries.add(pastTime, currentThreshold);
            }
            resumed = false;
        }
    }

    private double padValue(double inputValue) {
        if (inputValue < MIN_Y)
            return MIN_Y;
        if (inputValue > MAX_Y)
            return MAX_Y;
        return inputValue;
    }

    @Override
    public void onThreshold(double newValue) {
        this.currentThreshold = newValue;
    }
}