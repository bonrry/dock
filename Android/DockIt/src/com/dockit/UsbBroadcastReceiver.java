package com.dockit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, UsbService.class);
	    context.startService(i);
	}
}
