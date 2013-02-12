package com.dockit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
	private UsbManager usbManager;
	String TAG = "MainActivity";
	UsbAccessory accessory;
	ParcelFileDescriptor accessoryFileDescriptor;
	FileInputStream accessoryInput;
	FileOutputStream accessoryOutput;
	Thread mReadThread;
	Handler messageHandler;
	Button mButSendCmd;
	TextView mTxtSensorValue;

	public static final int TYPE_IN_MSG       = 1;
	public static final int TYPE_IO_ERROR_MSG = 2;

	/**
	 * ************************************************************************
	 * 				Android APP life cycle
	 * ************************************************************************
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		messageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case TYPE_IN_MSG:
					InMessage m = (InMessage) msg.obj;
					parseMessage(m);
					break;
				case TYPE_IO_ERROR_MSG:
					Log.i(TAG, "Got message TYPE_IO_ERROR_MSG");
					closeAccessory();
					break;
				}
			}
		};

		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(usbBroadcastReceiver, filter);
		setContentView(R.layout.activity_main);

		mButSendCmd = (Button) findViewById(R.id.butSendCommand);
		mButSendCmd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byte val = (byte) Math.round(254.0 * Math.random());
				byte[] msg = new byte[64];
				msg[0] = 3;
				msg[1] = 'L';
				msg[2] = val;
				sendMessage(msg, 64);
			}
		});
		mTxtSensorValue = (TextView) findViewById(R.id.txtSensorValue);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (accessoryInput != null && accessoryOutput != null)
			return;

		// TODO: verify, docs don't do this simple thing, not sure why?
		UsbAccessory[] accessories = usbManager.getAccessoryList();
		accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null)
			openAccessory(accessory);
		else
			Log.e(TAG, "Failed to resume accessory.");
	}

	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(usbBroadcastReceiver);
		super.onDestroy();
	}

	/**
	 * ************************************************************************
	 * 						Data Management
	 * ************************************************************************
	 */

	// TODO: parse message, store values...
	protected void parseMessage(InMessage m) {
		Log.v(TAG, "MSG received:" + new String(m.buf));
		if (m.buf[0] > 0 && m.buf[1] == 'M') {
			mTxtSensorValue.setText(""+m.buf[2]);
		}
	}

	protected void sendMessage(final byte[] buf, final int len) {
		Thread writeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					accessoryOutput.write(buf, 0, len);
				} catch (IOException e) {
					Log.d(TAG, "Exception in USB accessory input writing", e);
				}
			}
		});
		writeThread.start();
	}

	/**
	 * ************************************************************************
	 * 						USB ACCESSORY STUFF
	 * ************************************************************************
	 */
	private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action))
			{
				synchronized (this)
				{
					UsbAccessory[] accessories = usbManager.getAccessoryList();
					accessory = (accessories == null ? null : accessories[0]);
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				closeAccessory();
			}
		}
	};

	private void openAccessory(UsbAccessory accessory)
	{
		accessoryFileDescriptor = usbManager.openAccessory(accessory);
		if (accessoryFileDescriptor != null)
		{
			this.accessory = accessory;
			FileDescriptor fd = accessoryFileDescriptor.getFileDescriptor();
			accessoryInput = new FileInputStream(fd);
			accessoryOutput = new FileOutputStream(fd);
			mReadThread = new Thread(null, new AccesoryReadThread(accessoryInput, messageHandler), "AndroidPCHost");
			Log.d(TAG, "accessory opened");
			mReadThread.start();
			// TODO: enable USB operations in the app
		}
		else
		{
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory()
	{
		// TODO: disable USB operations in the app
		try
		{
			mReadThread.interrupt();
			if (accessoryFileDescriptor != null)
				accessoryFileDescriptor.close();
		}
		catch (IOException e)
		{}
		finally
		{
			accessoryFileDescriptor = null;
			accessory = null;
		}
	}
}
