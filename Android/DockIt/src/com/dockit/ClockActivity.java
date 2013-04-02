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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Start USB service...
		// FIXME : check that intent is USB_ATTACHED...
		Intent intent = new Intent(this, UsbService.class);
	    startService(intent);
	}
}
