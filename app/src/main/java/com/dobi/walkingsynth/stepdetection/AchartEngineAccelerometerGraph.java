package com.dobi.walkingsynth.stepdetection;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class AchartEngineAccelerometerGraph implements AccelerometerGraph {

    public static final String TAG = AchartEngineAccelerometerGraph.class.getSimpleName();

    private static final String THRESHOLD_SERIES_TITLE = "Threshold";
    private static final String ACCELEROMETER_SERIES_TITLE = "Accelerometer data";

    private static final int MAX_POINTS = 100;
    private static final int OFFSET = 4;
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 20;

    private int mPointsCount = 0;

    private TimeSeries mThresholdSeries;

    private TimeSeries mAccelerometerSeries;
    private XYMultipleSeriesRenderer mRenderer;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView view;

    private final AccelerometerProcessor accelerometerProcessor;

    public AchartEngineAccelerometerGraph(AccelerometerProcessor accelerometerProcessor) {
        this.accelerometerProcessor = accelerometerProcessor;

        mRenderer = new XYMultipleSeriesRenderer();
        mDataset = new XYMultipleSeriesDataset();

        prepareAccelerometerSeries();
        prepareThresholdSeries();
        prepareGraph();

        fillInitialPoints();
    }

    private void prepareAccelerometerSeries() {
        mAccelerometerSeries = new TimeSeries(ACCELEROMETER_SERIES_TITLE);

        mDataset.addSeries(mAccelerometerSeries);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setColor(Color.BLUE);
        mRenderer.addSeriesRenderer(renderer);

    }

    private void prepareThresholdSeries() {
        mThresholdSeries = new TimeSeries(THRESHOLD_SERIES_TITLE);
        mDataset.addSeries(mThresholdSeries);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(3f);
        renderer.setColor(Color.BLACK);
        mRenderer.addSeriesRenderer(renderer);
    }

    private void prepareGraph() {
        mRenderer.clearXTextLabels();
        mRenderer.setYAxisMin(MIN_Y);
        mRenderer.setYAxisMax(MAX_Y);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setXLabels(0);
        mRenderer.setClickEnabled(false);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
    }

    private void fillInitialPoints() {
        for (int i = 0; i < MAX_POINTS; i++) {
            mAccelerometerSeries.add(i, getAccelerometerValue());
            mThresholdSeries.add(i, getThresholdValue());
        }
        mPointsCount = MAX_POINTS;
    }

    private double getAccelerometerValue() {
        return accelerometerProcessor.getAccelerometerValue() - OFFSET;
    }

    private double getThresholdValue() {
        return accelerometerProcessor.getThreshold() - OFFSET;
    }

    public void invalidate(long eventTime) {
        Log.d(TAG, "invalidate(...) at time: " + eventTime);

        mAccelerometerSeries.add(eventTime, padValue(getAccelerometerValue()));
        if (mPointsCount > MAX_POINTS)
            mAccelerometerSeries.remove(0);

        mThresholdSeries.add(eventTime, padValue(getThresholdValue()));
        if (mPointsCount > MAX_POINTS)
            mThresholdSeries.remove(0);

        if (view != null)
            view.repaint();

        ++mPointsCount;
    }

    private double padValue(double inputValue) {
        if (inputValue < MIN_Y)
            return MIN_Y;
        if (inputValue > MAX_Y)
            return MAX_Y;
        return inputValue;
    }

    public void reset() {
        mAccelerometerSeries.clear();
        mThresholdSeries.clear();
        mPointsCount = 0;
    }

    @Override
    public View createView(Context context) {
        view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
        view.setLayoutParams(new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return view;
    }
}