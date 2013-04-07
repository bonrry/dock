package com.dockit;

import android.os.Bundle;

public class ClockActivity extends UsbActivity {

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
	
	@Override
	public void newData(InMessage m) {
		// FIXME TODO
	}
}
