package com.dockit;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

public class UsbService extends Service {

	private UsbManager usbManager;
	UsbAccessory accessory;
	ParcelFileDescriptor accessoryFileDescriptor;
	FileInputStream accessoryInput;
	FileOutputStream accessoryOutput;
	Thread mReadThread;
	Handler messageHandler;

	public static final int TYPE_IN_MSG       = 1;
	public static final int TYPE_IO_ERROR_MSG = 2;
	
	private static final int ONGOING_NOTIFICATION = 424242;

	private static final String TAG = "UsbService";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// FIXME : todo
		//handleCommand(intent);
		Log.d(TAG, "onStartCommand");
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		
		// Attach to accessory if one is connected
		// TODO: verify, docs don't do this simple thing, not sure why?
		attachAccessoryIfAny();
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onbind");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);
		Log.d(TAG, "onUnbind");
		return true;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.d(TAG, "onRebind");
		/*
		if(this.mFileDescriptor==null){
			this.connectUsb();
		}
		 */
		//		this.startThread();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

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
					// TODO display error or finish()...
					break;
				}
			}
		};

		// Register to USB events (attach or detach...)
		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(usbBroadcastReceiver, filter);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		unregisterReceiver(usbBroadcastReceiver);
		closeAccessory();
		super.onDestroy();
	}

	/**
	 * ************************************************************************
	 * 						USB ACCESSORY STUFF
	 * ************************************************************************
	 */

	private void attachAccessoryIfAny() {
		Log.d(TAG, "attachAccessoryIfAny");
		UsbAccessory[] accessories = usbManager.getAccessoryList();
		accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null)
			openAccessory(accessory);		
	}

	private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
				Log.d(TAG, "usbBroadcastReceiver: received ACTION_USB_ACCESSORY_ATTACHED");
				synchronized (this) {
					attachAccessoryIfAny();
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				Log.d(TAG, "usbBroadcastReceiver: received ACTION_USB_ACCESSORY_DETACHED");
				closeAccessory();
			}
		}
	};

	private void openAccessory(UsbAccessory accessory) {
		Log.d(TAG, "openAccessory");
		accessoryFileDescriptor = usbManager.openAccessory(accessory);
		if (accessoryFileDescriptor != null) {
			this.accessory = accessory;
			FileDescriptor fd = accessoryFileDescriptor.getFileDescriptor();
			accessoryInput = new FileInputStream(fd);
			accessoryOutput = new FileOutputStream(fd);
			mReadThread = new Thread(null, new AccesoryReadThread(accessoryInput, messageHandler), "AndroidPCHost");
			Log.d(TAG, "openAccessory: accessory opened");
			mReadThread.start();
			// TODO: enable USB operations in the app
			startForeground(ONGOING_NOTIFICATION, getNotification());
		} else {
			Log.d(TAG, "openAccessory: accessory open fail");
		}
	}

	private void closeAccessory() {
		Log.d(TAG, "closeAccessory");
		// TODO: disable USB operations in the app
		try {
			mReadThread.interrupt();
			if (accessoryFileDescriptor != null)
				accessoryFileDescriptor.close();
			Log.d(TAG, "closeAccessory: accessory closed");
		} catch (IOException e) { }
		finally {
			accessoryFileDescriptor = null;
			accessory = null;
			stopForeground(true);
			stopSelf(); // Will actually stop AFTER all clients unbind... 
		}
	}
	
	private Notification getNotification() {
		Notification notification = new Notification(R.drawable.ic_launcher, getText(R.string.notif_service_connected),
		        System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, ClockActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.notification_title),
		        getText(R.string.notification_message), pendingIntent);
		return notification;
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

	// TODO: parse message, store values...
	protected void parseMessage(InMessage m) {
		Log.v(TAG, "MSG received:" + new String(m.buf));
		if (m.buf[0] > 0 && m.buf[1] == 'M') {
			//...
			
		}
	}
}