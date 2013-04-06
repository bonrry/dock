package com.dockit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ClockActivity extends Activity {

	String TAG = "ClockActivity";

	/**
	 * ************************************************************************
	 * 				Android APP life cycle
	 * ************************************************************************
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_clock);
	}
	
}
