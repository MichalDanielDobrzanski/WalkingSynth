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
        mRenderer.setClickEnabled(false);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
    }

    public void addNewPoint(double t, double[] v) {
        for (int i = 0; i < datasetsCount; ++i) {
            // moving plot
            if (t > 20)
                datasets[i].add(t, v[i] + mOffset[i]);
            if (t > GRAPH_RESOLUTION)
                datasets[i].remove(0);
        }
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

    public void addOffset(int i, int v) {
        mOffset[i] = v;
    }

//    public void addNewSeries() {
//        if (datasetsCount <= AccOptions.size) {
//            datasets[datasetsCount] = new TimeSeries(TITLE + (mDataset.getSeriesCount() + 1));
//            // add another data set to multiple data set
//            mDataset.addSeries(datasets[datasetsCount]);
//            datasetsCount += 1;
//            // create new renderer
//            XYSeriesRenderer renderer = new XYSeriesRenderer();
//            renderer.setColor(Color.BLUE);
//            renderer.setPointStyle(PointStyle.CIRCLE);
//            renderer.setFillPoints(true);
//            mRenderer.addSeriesRenderer(renderer);
//        } else {
//            throw new ArrayIndexOutOfBoundsException();
//        }
//    }

    public void clear() {
        for (TimeSeries ts : datasets) {
            ts.clear();
        }
    }

//    public void setProperties(){
//
//        //renderer.setClickEnabled(ClickEnabled);
//        renderer.setBackgroundColor(Color.WHITE);
//        renderer.setMarginsColor(Color.WHITE);
//
//        renderer.setApplyBackgroundColor(true);
//        if(greater < 100){
//            renderer.setXAxisMax(100);
//        }else{
//            renderer.setXAxisMin(greater-100);
//            renderer.setXAxisMax(greater);
//        }
//        renderer.setChartTitle("AccelerometerData");
//
//        renderer.setAxesColor(Color.BLACK);
//        XYSeriesRenderer renderer1 = new XYSeriesRenderer();
//        renderer1.setColor(Color.RED);
//        renderer.addSeriesRenderer(renderer1);
//        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
//        renderer2.setColor(Color.GREEN);
//        renderer.addSeriesRenderer(renderer2);
//        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
//        renderer3.setColor(Color.BLUE);
//        renderer.addSeriesRenderer(renderer3);
//    }

    public GraphicalView getView(Context context){
        view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
        return view;
    }
}