package com.genymobile.sommeil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

/**
 * Multiple temperature demo chart.
 */
public class MovingSleepStatsChartActivity extends Activity {
	
	Integer mIndex = 0;
	ArrayList<Double> mValuesX = new ArrayList<Double>();
	ArrayList<Double> mValuesY = new ArrayList<Double>();
	List<double[]> mCourbesX = new ArrayList<double[]>();
	List<double[]> mCourbesY = new ArrayList<double[]>();
	
	
	boolean mUpdating = false;
	
	GraphicalView mChart;
	LinearLayout mContainer;
	AsyncTask<Void, Integer, Void> mDrawTask;
	
	public static MovingSleepStatsChartActivity sInstance = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		double[] valuesX = new double[mValuesX.size()];
		for (int i = 0; i < mValuesX.size(); i++) {
			valuesX[i] = mValuesX.get(i);
		}
		mCourbesX.add(valuesX);
		
		double[] valuesY = new double[mValuesY.size()];
		for (int i = 0; i < mValuesY.size(); i++) {
			valuesY[i] = mValuesY.get(i);
		}
		mCourbesY.add(valuesY);
		
		Log.d("MOVING CHART", "Creating");
		mChart = new SleepStatsChart().create(this, mCourbesX, mCourbesY);
		
		setContentView(R.layout.stats);
		mContainer = (LinearLayout) findViewById(R.id.statsContainer);
		mContainer.addView(mChart);
		
		sInstance = this;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		sInstance = this;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		//mDrawTask.cancel(true);
		
		sInstance = null;
	}
	
	
	private List<Integer> mData = new ArrayList<Integer>(); 
	private long mLastInsert = System.currentTimeMillis();
	public void addValue(final int realValue) {
		
	    long now = System.currentTimeMillis();
	    mData.add(realValue);
        int averagetmp = 0;
	    if( mLastInsert + 1000 > now ) {
	        int total = 0;
	        for(Integer in : mData ) {
	            total += in;
	        }
	        averagetmp = total / mData.size();
	        mData.clear();
	    } else {
	        return;
	    }
	    final int average = averagetmp;
	    Log.d("DRAW", "" + average);
	    
	    mValuesX.add(mIndex.doubleValue());
        
        double realValue2 = ((double)average * 30d) / 255d;
        Log.d("VALUE", " "+realValue2);
    
    mValuesY.add(realValue2);
    
    mIndex++;
    
    mCourbesX.clear();
    mCourbesY.clear();
    
    double[] valuesX = new double[mValuesX.size()];
    for (int i = 0; i < mValuesX.size(); i++) {
        valuesX[i] = mValuesX.get(i);
    }
    mCourbesX.add(valuesX);
    
    double[] valuesY = new double[mValuesY.size()];
    for (int i = 0; i < mValuesY.size(); i++) {
        valuesY[i] = mValuesY.get(i);
    }
    mCourbesY.add(valuesY);
	    
	    
	    this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(mContainer != null ) {
                    
                
                mChart = new SleepStatsChart().create(MovingSleepStatsChartActivity.this, 
                        mCourbesX, mCourbesY);
                
                mContainer.removeAllViews();
                mContainer.addView(mChart);
               // mContainer.invalidate();
                }
                
            }
        });
	}
}
