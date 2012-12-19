package com.genymobile.sommeil;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Multiple temperature demo chart.
 */
public class SleepStatsChartActivity extends Activity {
  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		List<double[]> dataY = new ArrayList<double[]>();
		dataY.add(new double[] { 4.3, 4.9, 5.9, 8.8, 10.8, 4.9, 13.6, 12.8, 4.4, 9.5, 7.5, 5.5,
		        4.3, 14, 5.9, 5.8, 5.8, 5, 5, 5.6, 6.8, 11.4, 5.5, 7.5, 5.5});
		
		List<double[]> dataX = new ArrayList<double[]>();
		dataX.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
		       13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 });
	    
		View chart = new SleepStatsChart().create(this, dataX, dataY);
		
		setContentView(chart);
	}
}
