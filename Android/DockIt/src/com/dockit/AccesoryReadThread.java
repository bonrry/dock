package com.dockit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AccesoryReadThread implements Runnable {

	
	// FIXME TODO : implementer le mecanisme d'envoie de messages

	static final int BUF_LEN = 64;
	static final int ACK_LEN = 1;
	static final String TAG = "AccesoryReadThread";
	FileInputStream accessoryInput;
	FileOutputStream accessoryOutput;
	Handler handler;
	
	public AccesoryReadThread(FileInputStream accessoryInput, FileOutputStream accessoryOutput, Handler handler) {
		this.accessoryInput = accessoryInput;
		this.accessoryOutput = accessoryOutput;
		this.handler = handler;
	}
	
	/**
	 * Main USB reading loop, processing incoming data from accessory
	 */
	@Override
	public void run()
	{
		int ret = 0;
		byte[] buffer = new byte[BUF_LEN];
		byte[] ackBuf = new byte[BUF_LEN];
		ackBuf[0] = ACK_LEN;
		ackBuf[1] = 'z';

		Log.d(TAG, "ReadThread starting...");
		while (ret >= 0) {
			try {
				ret = accessoryInput.read(buffer);
				if (ret > 0) {
					// Ack the message. This is a hack to avoid host PC simulator to block 
					// on read, when using libusb on MACOSX :-(
					accessoryOutput.write(ackBuf, 0, BUF_LEN);
				}
				//Log.d(TAG, "READ("+ret+")="+new String(buffer));
				Message m = Message.obtain(handler, UsbService.TYPE_IN_MSG);
				m.obj = new InMessage(buffer);
				handler.sendMessage(m);
			} catch (IOException e) {
				Log.d(TAG, "Exception in USB accessory input reading", e);
				Message m = Message.obtain(handler, UsbService.TYPE_IO_ERROR_MSG);
				handler.sendMessage(m);
				break;
			}
		}
		Log.e(TAG, "ReadThread aborting...");
	}
}
