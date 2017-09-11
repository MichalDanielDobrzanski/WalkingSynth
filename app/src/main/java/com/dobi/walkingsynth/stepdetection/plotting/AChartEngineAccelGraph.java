package com.dobi.walkingsynth.stepdetection.plotting;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.dobi.walkingsynth.stepdetection.accelerometer.AccelerometerProcessing;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class AChartEngineAccelGraph implements AccelGraph {

    private static final String THRESHOLD_SERIES_TITLE = "Threshold";
    private static final String ACCELEROMETER_SERIES_TITLE = "Accelerometer data";

    private static final int GRAPH_POINTS_COUNT = 100;

    private TimeSeries mThreshold;
    private TimeSeries mSerie;

    private double mThresholdValue;

    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private int mPointsCount = 0;

    private GraphicalView view;

    public AChartEngineAccelGraph() {

        mThresholdValue = AccelerometerProcessing.THRESHOLD_INITIAL;

        mSerie = new TimeSeries(ACCELEROMETER_SERIES_TITLE);
        mDataset.addSeries(mSerie);

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setColor(Color.BLUE);

        mRenderer.addSeriesRenderer(renderer);

        addThresholdLine();

        mRenderer.clearXTextLabels();
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(20);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setXLabels(0);

        mRenderer.setClickEnabled(false);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
    }

    private void addThresholdLine() {
        mThreshold = new TimeSeries(THRESHOLD_SERIES_TITLE);
        mDataset.addSeries(mThreshold);

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(3f);
        renderer.setColor(Color.BLACK);

        mRenderer.addSeriesRenderer(renderer);
    }

    /**
     * Update all graphs on the View.
     */
    public void invalidate(double t, double v) {

        mSerie.add(t, v);
        if (mPointsCount > GRAPH_POINTS_COUNT)
            mSerie.remove(0);

        mThreshold.add(t, mThresholdValue);
        if (mPointsCount > GRAPH_POINTS_COUNT)
            mThreshold.remove(0);

        if (view != null)
            view.repaint();

        ++mPointsCount;
    }

    public void reset() {
        mSerie.clear();
        mThreshold.clear();
        mPointsCount = 0;
    }

    @Override
    public View createView(Context context) {
        view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public void onThresholdChange(double value) {
        mThresholdValue = value;
    }
}