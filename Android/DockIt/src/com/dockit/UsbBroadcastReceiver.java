package com.dockit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UsbBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Broadcast Intent Detected by receiver.",
				Toast.LENGTH_LONG).show();
		Intent i = new Intent(context, UsbService.class);
	    context.startService(i);
	}

}
