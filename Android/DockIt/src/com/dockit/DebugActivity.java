package com.dockit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dockit.UsbService.LocalBinder;

public class DebugActivity extends Activity implements DataAvailableInterface {

	String TAG = "MainActivity";
	Button mButSendCmd;
	TextView mTxtSensorValue;
	UsbService mService;
	boolean mBound = false;
	Intent usbServiceIntent;

	/**
	 * ************************************************************************
	 * 				Android APP life cycle
	 * ************************************************************************
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_debug);

		mButSendCmd = (Button) findViewById(R.id.butSendCommand);
		mButSendCmd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byte val = (byte) Math.round(254.0 * Math.random());
				byte[] msg = new byte[64];
				msg[0] = 2;
				msg[1] = 'L';
				msg[2] = val;
				sendMessage(msg, 64);
			}
		});
		mTxtSensorValue = (TextView) findViewById(R.id.txtSensorValue);
		usbServiceIntent = new Intent(this, UsbService.class);
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			mService.listenForData(DebugActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override 
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent: notified...");
		bindService(usbServiceIntent, mConnection, Context.BIND_ABOVE_CLIENT | BIND_AUTO_CREATE);
	};

	@Override
	protected void onStart() {
		super.onStart();
		bindService(usbServiceIntent, mConnection, Context.BIND_ABOVE_CLIENT | BIND_AUTO_CREATE);
	};

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	};
	/**
	 * ************************************************************************
	 * 						Data Management
	 * ************************************************************************
	 */

	protected void sendMessage(final byte[] buf, final int len) {
		if (mBound) {
			mService.sendMessage(buf, len);
		}
	}

	@Override
	public void newData(InMessage m) {
		if (m.buf[0] > 0 && m.buf[1] == 'M') {
			mTxtSensorValue.setText(""+m.buf[2]);
		}
	}
}
