package com.dobi.walkingsynth.accelerometer;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Plotting accelerometer processed data.
 */
public class AccelerometerGraph {

    private static final String TAG = AccelerometerGraph.class.getSimpleName();

    private static final String THRESH = "Thresh";
    private static final String TITLE = "Accelerometer data";

    private static final int GRAPH_POINTS = 80;

    private TimeSeries mThreshold;
    private TimeSeries[] mSeries = new TimeSeries[AccelerometerSignals.count];
    private XYSeriesRenderer[] mRenderers = new XYSeriesRenderer[AccelerometerSignals.count];
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private GraphicalView view;

    private int mPointsCount = 0;
    private int[] mOffset = new int[AccelerometerSignals.count];

    public AccelerometerGraph() {
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
        addThresholdGraph();
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

    /**
     * Update all graphs on the View.
     * @param t time argument
     * @param v values array
     */
    public void addNewPoints(double t, double[] v) {
        for (int i = 0; i < AccelerometerSignals.count; ++i) {
            // moving plot
            mSeries[i].add(t, v[i] + mOffset[i]);
            mThreshold.add(t, AccelerometerProcessing.getInstance().getThreshold());
            if (mPointsCount > GRAPH_POINTS) {
                mSeries[i].remove(0);
                mThreshold.remove(0);
            }
        }
        view.repaint();
        ++mPointsCount;
    }

    /**
     * Adds threshold configuration for plotting.
     */
    private void addThresholdGraph() {
        mThreshold = new TimeSeries(THRESH);
        mDataset.addSeries(mThreshold);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2f);
        renderer.setColor(Color.BLACK);
        mRenderer.addSeriesRenderer(renderer);
    }

    public GraphicalView getView(Context context){
        view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
        return view;
    }
}