package com.dobi.walkingsynth;
import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class AccelerometerGraph {

    // how many points in a graph frame
    private static final int GRAPH_RESOLUTION = 150;
    private static final String TITLE = "Accelerometer data";

    private GraphicalView view;

    private TimeSeries[] datasets = new TimeSeries[AccOptions.size];
    private int datasetsCount = 2;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private XYSeriesRenderer[] renderer = new XYSeriesRenderer[AccOptions.size];
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    public boolean isPainting = true;
    private int mPointsCount = 0;
    private int[] mOffset = new int[AccOptions.size];

    public AccelerometerGraph() {
        // add single data set to multiple data set
        for (int i = 0; i < datasetsCount; i++) {
            datasets[i] = new TimeSeries(TITLE + (i + 1));
            mDataset.addSeries(datasets[i]);
            renderer[i] = new XYSeriesRenderer();
            renderer[i].setPointStyle(PointStyle.CIRCLE);
            renderer[i].setFillPoints(true);
            mRenderer.addSeriesRenderer(renderer[i]);
        }
        renderer[0].setColor(Color.RED);
        renderer[1].setColor(Color.BLUE);
        // customize general view
        mRenderer.clearXTextLabels();
        mRenderer.setYTitle("Acc value");
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(20);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setApplyBackgroundColor(true);
        // add single renderer to multiple renderer
        mRenderer.setXLabels(0);
        mRenderer.setClickEnabled(true);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
    }

    public void addNewPoints(double t, double[] v) {
        for (int i = 0; i < datasetsCount; ++i) {
            // moving plot
            //if (t > 20)
            datasets[i].add(t, v[i] + mOffset[i]);
            if (mPointsCount > GRAPH_RESOLUTION)
                datasets[i].remove(0);
        }
        if (isPainting)
            view.repaint();
        ++mPointsCount;
    }

    public void isPainting(boolean v) {
        isPainting = v;
    }

    public void setVisibility(int opt, boolean show) {
        //AccOptions option = AccOptions.values()[opt];
        if (show) {
            // show current option
            renderer[opt].setColor(Color.BLUE);
            // show current option
        } else {
            renderer[opt].setColor(Color.TRANSPARENT);
        }
        view.repaint();
    }

    public void initialize() {
        // reset the point counter
        mPointsCount = 0;
    }

    public void addOffset(int i, int v) {
        mOffset[i] = v;
    }


    public void clear() {
//        for (TimeSeries ts : datasets) {
//            ts.clear();
//        }
    }

    public GraphicalView getView(Context context){
        view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
        return view;
    }
}