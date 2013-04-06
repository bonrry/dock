package com.dockit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UsbAccessoryAttachedWorkaroundActivity extends Activity {

	/**
	 * Used only to catch the USB_ACCESSORY_ATTACHED event, has it is not broadcasted to 
	 * anything else than the activity with the right accessory_filter (no Service, nor Receiver...)
	 * DETACHED event is broadcasted as other events. 
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, UsbService.class);
		startService(intent);
		finish();
	}
}
