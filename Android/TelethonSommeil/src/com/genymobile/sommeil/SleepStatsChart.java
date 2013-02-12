package com.genymobile.sommeil;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Multiple temperature demo chart.
 */
public class SleepStatsChart extends AbstractDemoChart {
  
  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public GraphicalView create(Context context, List<double[]> sleepDataX, List<double[]> sleepDataY) {
    String[] titles = new String[] { "Limite de sommeil profond" };
    
    List<double[]> limit = new ArrayList<double[]>();
    int length = sleepDataY.get(0).length;
    double[] limitData = new double[length];
    for (int i = 0; i < length; i++) {
    	limitData[i] = 8;
    }
    limit.add(limitData);
    
    
    int[] colors = new int[] { Color.parseColor("#FF9900"), Color.BLACK };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
    setRenderer(renderer, colors, styles);
    
    setChartSettings(renderer, "", "", "", 0.5, 12.5, 0, 32,
        Color.LTGRAY, Color.LTGRAY);
    renderer.setXLabels(12);
    renderer.setBackgroundColor(Color.BLACK);
    renderer.setYLabels(0);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(false);
    renderer.setPanLimits(new double[] { 0, 0, 0, 0 });
    renderer.setZoomLimits(new double[] { 0, 0, 0, 0 });
    renderer.setZoomRate(1.00f);
    
    XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(1);
    seriesRenderer.setFillBelowLine(true);
    seriesRenderer.setFillBelowLineColor(Color.parseColor("#880033CC"));
    seriesRenderer.setLineWidth(2.5f);
    seriesRenderer.setDisplayChartValues(false);
    
    XYSeriesRenderer average = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
    average.setFillBelowLine(false);
    average.setLineWidth(1f);
    average.setDisplayChartValues(false);
    
    renderer.setLabelsColor(Color.WHITE);
    renderer.setXLabelsColor(Color.GREEN);
    renderer.setYLabelsColor(0, colors[0]);

    renderer.addYTextLabel(7.5f, "Sommeil profond");
    renderer.addYTextLabel(31, "Sommeil léger");
    
    renderer.setYLabelsColor(0, Color.WHITE);
    renderer.setYLabelsAngle(270);
    renderer.setYLabelsAlign(Align.RIGHT);
    
    XYMultipleSeriesDataset dataset = buildDataset(titles, sleepDataX, limit);
    
    addXYSeries(dataset, new String[] { "Courbe du sommeil" }, sleepDataX, sleepDataY, 1);
    
    GraphicalView view = ChartFactory.getCubeLineChartView(context, dataset, renderer, 0.3f);
    return view;
  }
}
