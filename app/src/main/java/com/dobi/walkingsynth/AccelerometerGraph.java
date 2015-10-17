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

    private GraphicalView view;

    private TimeSeries dataset = new TimeSeries("Accelerometer data");
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private XYSeriesRenderer renderer = new XYSeriesRenderer();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    public AccelerometerGraph() {
        // add single data set to multiple data set
        mDataset.addSeries(dataset);

        renderer.setColor(Color.RED);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);

        // enable zoom
        mRenderer.clearXTextLabels();
        mRenderer.setYTitle("Acc value");
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(10);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setApplyBackgroundColor(true);

        // add single renderer to multiple renderer
        mRenderer.addSeriesRenderer(renderer);
    }

    public void addNewPoint(double t, double v) {
        // moving plot
        if (t > 20)
            dataset.add(t,v);
        if (t > GRAPH_RESOLUTION)
            dataset.remove(0);
        // scaling Y

    }

    public void clear() {
        dataset.clear();
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