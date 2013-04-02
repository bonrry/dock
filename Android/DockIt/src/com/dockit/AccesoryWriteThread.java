package com.dockit;

import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AccesoryWriteThread implements Runnable {

	static final String TAG = "AccesoryWriteThread";
	FileOutputStream accessoryOutput;
	Handler handler;
	byte[] buf;
	int len;

	public AccesoryWriteThread(FileOutputStream accessoryOutput, Handler handler, final byte[] buf, final int len) {
		this.accessoryOutput = accessoryOutput;
		this.handler = handler;
		this.buf = buf;
		this.len = len;
	}

	/**
	 * Main USB reading loop, processing incoming data from accessory
	 */
	@Override
	public void run() {
		Log.d(TAG, "WriteThread starting...");
		try {
			accessoryOutput.write(buf, 0, len);
		} catch (IOException e) {
			Log.d(TAG, "Exception in USB accessory writing", e);
			Message m = Message.obtain(handler, UsbService.TYPE_IO_ERROR_MSG);
			handler.sendMessage(m);
		}
		Log.d(TAG, "WriteThread  aborting...");
	}
}
