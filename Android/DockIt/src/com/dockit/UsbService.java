package com.dockit;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

public class UsbService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private UsbManager usbManager;
	UsbAccessory accessory;
	ParcelFileDescriptor accessoryFileDescriptor;
	FileInputStream accessoryInput;
	FileOutputStream accessoryOutput;
	Thread mReadThread = null;
	static Handler messageHandler;

	ArrayList<DataAvailableInterface> listeners = new ArrayList<DataAvailableInterface>();

	public static final int TYPE_IN_MSG       = 1;
	public static final int TYPE_IO_ERROR_MSG = 2;

	private static final int ONGOING_NOTIFICATION = 424242;

	private static final String TAG = "UsbService";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Show UI
		Intent uiActivityIntent = new Intent(this, DebugActivity.class);
		uiActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(uiActivityIntent);

		// FIXME : todo
		//handleCommand(intent);
		Log.d(TAG, "onStartCommand");
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

		// Attach to accessory if one is connected
		attachAccessoryIfAny();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onbind");
		return mBinder;
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
		attachAccessoryIfAny();
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
					closeAccessory();
					// TODO display error or finish()...
					break;
				}
			}
		};

		// Register to USB DETACH event (not ATTACHED as it is only sent to activity...)
		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
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
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		UsbService getService() {
			// Return this instance of LocalService so clients can call public methods
			return UsbService.this;
		}
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
			if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				Log.d(TAG, "usbBroadcastReceiver: received ACTION_USB_ACCESSORY_DETACHED");
				closeAccessory();
			}
		}
	};

	private void openAccessory(UsbAccessory accessory) {
		if (accessoryFileDescriptor != null) {
			Log.d(TAG, "openAccessory: already opened...");
			return;
		}
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
			if (mReadThread != null && !mReadThread.isInterrupted())
				mReadThread.interrupt();
			if (accessoryFileDescriptor != null)
				accessoryFileDescriptor.close();
			Log.d(TAG, "closeAccessory: accessory closed");
		} catch (IOException e) {
			Log.e(TAG, "closeAccessory: "+e);
		}
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
		Intent notificationIntent = new Intent(this, DebugActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.notification_title),
				getText(R.string.notification_message), pendingIntent);
		return notification;
	}

	/**
	 * ************************************************************************
	 * 						Available to activities:
	 * ************************************************************************
	 */

	public void sendMessage(final byte[] buf, final int len) {
		Thread writeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					accessoryOutput.write(buf, 0, len);
				} catch (IOException e) {
					Log.d(TAG, "Exception in USB accessory input writing", e);
					closeAccessory();
				}
			}
		});
		writeThread.start();
	}

	public void listenForData(DataAvailableInterface listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void unlistenForData(DataAvailableInterface listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	protected void parseMessage(InMessage m) {
		if (m.len >= 0) {
			Log.v(TAG, "MSG received: (len: " + m.len + "), Command: " + m.command + " - Data: " + ((m.len > 0) ? new String(m.buf, 0, m.len) : "null"));
			if (m.len > 0 && m.command == 'M') {
				//...
				// TODO: parse message, store values...
			}
			for (DataAvailableInterface listener : listeners) {
				listener.newData(m);
			}			
		}
	}
}