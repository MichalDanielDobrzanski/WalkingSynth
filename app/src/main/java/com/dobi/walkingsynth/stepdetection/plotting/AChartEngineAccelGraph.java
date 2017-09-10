package com.dobi.walkingsynth.stepdetection.plotting;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.dobi.walkingsynth.stepdetection.accelerometer.AccelerometerProcessing;
import com.dobi.walkingsynth.stepdetection.accelerometer.AccelerometerSignals;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class AChartEngineAccelGraph implements AccelGraph {

    private static final String THRESH = "Threshold";
    private static final String TITLE = "Accelerometer data";

    // resolution:
    private static final int GRAPH_POINTS_COUNT = 100;

    private TimeSeries mThreshold;
    private double mThresholdValue;

    private TimeSeries[] mSeries = new TimeSeries[AccelerometerSignals.count];
    private XYSeriesRenderer[] mRenderers = new XYSeriesRenderer[AccelerometerSignals.count];

    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private int mPointsCount = 0;

    private GraphicalView view;

    public AChartEngineAccelGraph() {

        mThresholdValue = AccelerometerProcessing.THRESHOLD_INITIAL;

        // add single data set to multiple data set
        for (int i = 0; i < AccelerometerSignals.count; i++) {
            mSeries[i] = new TimeSeries(TITLE + (i + 1));
            mDataset.addSeries(mSeries[i]);
            mRenderers[i] = new XYSeriesRenderer();
            mRenderers[i].setPointStyle(PointStyle.CIRCLE);
            mRenderers[i].setFillPoints(true);
            mRenderer.addSeriesRenderer(mRenderers[i]);
        }
        mRenderers[0].setColor(Color.RED);
        mRenderers[1].setColor(Color.BLUE);

        // add measurement line
        addThresholdLine();

        // customize general view
        mRenderer.clearXTextLabels();
        mRenderer.setYTitle("Acc value");
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(20);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setXLabels(0);
        mRenderer.setClickEnabled(true);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
    }

    private void addThresholdLine() {
        mThreshold = new TimeSeries(THRESH);
        mDataset.addSeries(mThreshold);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2f);
        renderer.setColor(Color.BLACK);
        mRenderer.addSeriesRenderer(renderer);
    }

    /**
     * Update all graphs on the View.
     * @param t time plotting argument
     * @param v an array of values to plot
     */
    public void invalidate(double t, double[] v) {

        // signals:
        for (int i = 0; i < v.length; ++i) {
            mSeries[i].add(t, v[i]);
            if (mPointsCount > GRAPH_POINTS_COUNT) {
                mSeries[i].remove(0);
            }
        }

        // threshold:
        mThreshold.add(t, mThresholdValue);
        if (mPointsCount > GRAPH_POINTS_COUNT) {
            mThreshold.remove(0);
        }

        if (view != null)
            view.repaint();

        ++mPointsCount;
    }

    public void reset() {
        for (int i = 0; i < mSeries.length; ++i) {
            mSeries[i].clear();
            mThreshold.clear();
        }
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