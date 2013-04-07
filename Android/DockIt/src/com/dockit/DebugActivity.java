package com.dockit;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DebugActivity extends UsbActivity {

	String TAG = "DebugActivity";
	Button mButSendCmd;
	TextView mTxtSensorValue;

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
	}

	/**
	 * ************************************************************************
	 * 						Data Management
	 * ************************************************************************
	 */
	
	@Override
	public void newData(InMessage m) {
		if (m.buf[0] > 0 && m.buf[1] == 'M') {
			mTxtSensorValue.setText(""+m.buf[2]);
		}
	}
}
