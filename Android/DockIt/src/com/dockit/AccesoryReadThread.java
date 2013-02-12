package com.dockit;

import java.io.FileInputStream;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AccesoryReadThread implements Runnable {

	static final String TAG = "AccesoryReadThread";
	FileInputStream accessoryInput;
	Handler handler;
	
	public AccesoryReadThread(FileInputStream accessoryInput, Handler handler) {
		this.accessoryInput = accessoryInput;
		this.handler = handler;
	}
	
	/**
	 * Main USB reading loop, processing incoming data from accessory
	 */
	@Override
	public void run()
	{
		int ret = 0;
		byte[] buffer = new byte[64];

		while (ret >= 0) {
			try {
				ret = accessoryInput.read(buffer);
				//Log.d(TAG, "READ("+ret+")="+new String(buffer));
				Message m = Message.obtain(handler, MainActivity.TYPE_IN_MSG);
				m.obj = new InMessage(buffer);
				handler.sendMessage(m);
			} catch (IOException e) {
				Log.d(TAG, "Exception in USB accessory input reading", e);
				Message m = Message.obtain(handler, MainActivity.TYPE_IO_ERROR_MSG);
				handler.sendMessage(m);
				return;
			}
		}
	}
}
