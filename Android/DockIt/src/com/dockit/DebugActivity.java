package com.dockit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DebugActivity extends Activity {

	String TAG = "MainActivity";
	Button mButSendCmd;
	TextView mTxtSensorValue;

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
		Intent intent = new Intent(this, UsbService.class);
	    startService(intent);
	}

	/**
	 * ************************************************************************
	 * 						Data Management
	 * ************************************************************************
	 */

	protected void sendMessage(final byte[] buf, final int len) {
		// TODO forward to service...
	}

	protected void parseMessage(InMessage m) {
		if (m.buf[0] > 0 && m.buf[1] == 'M') {
			mTxtSensorValue.setText(""+m.buf[2]);
		}
	}
}
